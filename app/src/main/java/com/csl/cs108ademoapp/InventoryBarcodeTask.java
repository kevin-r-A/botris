package com.csl.cs108ademoapp;

import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.csl.cs108ademoapp.adapters.ReaderListAdapter;
import com.csl.cs108library4a.ReaderDevice;

import java.util.ArrayList;

public class InventoryBarcodeTask extends AsyncTask<Void, String, String> {
    final boolean DEBUG = false;
    public enum TaskCancelRReason {
        NULL, INVALD_REQUEST, DESTORY, STOP, BUTTON_RELEASE, TIMEOUT
    }
    final private boolean bAdd2End = false;
    final boolean endingRequest = false;

    public TaskCancelRReason taskCancelReason;
    int batteryCountInventory_old; long startTimeMillis, runTimeMillis;

    private int total, allTotal;
    private int yield = 0;
    private long timeMillis;

    boolean requestSound = false; boolean requestNewSound = false; boolean requestNewVibrate = false; long timeMillisNewVibrate;
    long timeMillisSound = 0;
    Handler handler = new Handler(); boolean bUseVibrateMode0 = false;

    protected void onPreExecute() {
        MainActivity.sharedObjects.runningInventoryBarcodeTask = true;
        button.setText("Stop"); if (button1 != null) button1.setText("Stop");
        total = 0; allTotal = 0; yield = 0;
        if (tagsList != null) {
            yield = tagsList.size();
            for (int i = 0; i < yield; i++) {
                allTotal += tagsList.get(i).getCount();
            }
        }
//            tagsList.clear();
//            readerListAdapter.notifyDataSetChanged();
        timeMillis = 0; startTimeMillis = System.currentTimeMillis(); runTimeMillis = startTimeMillis;
        taskCancelReason = TaskCancelRReason.NULL;
        if (barcodeYieldView != null) barcodeYieldView.setText("");

        MainActivity.mCs108Library4a.barcodeInvventory(true);
        if (DEBUG) MainActivity.mCs108Library4a.appendToLog("InventoryBarcodeFragment.InventoryRfidTask.onPreExecute()");
        if (MainActivity.mCs108Library4a.getInventoryVibrate() && bUseVibrateMode0 == false) MainActivity.mCs108Library4a.setVibrateOn(3);
    }

    @Override
    protected String doInBackground(Void... a) {
        while (MainActivity.mCs108Library4a.isBleConnected() && isCancelled() == false) {
            int batteryCount = MainActivity.mCs108Library4a.getBatteryCount();
            if (batteryCountInventory_old != batteryCount) {
                batteryCountInventory_old = batteryCount;
                publishProgress("VV");
            }
            if (System.currentTimeMillis() > runTimeMillis + 1000) {
                runTimeMillis = System.currentTimeMillis();
                publishProgress("WW");
            }
            if (System.currentTimeMillis() - timeMillisSound > 1000) {
                timeMillisSound = System.currentTimeMillis();
                requestSound = true;
            }
            byte[] onBarcodeEvent = MainActivity.mCs108Library4a.onBarcodeEvent();
            if (onBarcodeEvent != null) {
                String stringBar = null;
                if (false) stringBar = new String(onBarcodeEvent);
                else if (onBarcodeEvent.length != 0) {
                    for (int i = 0; i < onBarcodeEvent.length; i++) {
                        String stringLetter = "";
                        if (false && onBarcodeEvent[i] == 0x0D) { if (false) stringLetter = "<CR>"; }
                        else if (false && onBarcodeEvent[i] == 0x0A) { if (false) stringLetter = "<LF>"; }
                        else {
                            byte[] arrayLetter = new byte[1];
                            arrayLetter[0] = onBarcodeEvent[i];
                            stringLetter = new String(arrayLetter);
                            if (stringLetter.length() == 0) MainActivity.mCs108Library4a.appendToLog("Non-printable character = " + MainActivity.mCs108Library4a.byteArrayToString(arrayLetter));
                        }
                        if (stringBar == null) stringBar = stringLetter;
                        else stringBar += stringLetter;
                    }
                }
                if (stringBar != null) { if (stringBar.length() != 0) publishProgress(null, stringBar.trim()); }
                timeMillis = System.currentTimeMillis();
            } else if (System.currentTimeMillis() - timeMillis > 300) { if (taskCancelReason != TaskCancelRReason.NULL) cancel(true); }
            if (MainActivity.mCs108Library4a.isBleConnected() == false) taskCancelReason = TaskCancelRReason.DESTORY;
        }
        return "End of Asynctask()";
    }

    @Override
    protected void onProgressUpdate(String... output) {
        if (output != null) {
            if (output[0] != null) {
                if (output[0].length() == 2) {
                    if (output[0].contains("WW")) {
                        long timePeriod = (System.currentTimeMillis() - startTimeMillis) / 1000;
                        if (timePeriod > 0)
                            if (barcodeRunTime != null) barcodeRunTime.setText(String.format("Run time: %d sec", timePeriod));
                    } else if (taskCancelReason == TaskCancelRReason.NULL) {
                        if (barcodeVoltageLevel != null) barcodeVoltageLevel.setText(MainActivity.mCs108Library4a.getBatteryDisplay(true));
                    }
                }
                return;
            }
            if (registerBarValue != null) registerBarValue.setText(output[1]);
            boolean match = false;
            if (false || tagsList != null) {
                for (int i = 0; i < tagsList.size(); i++) {
                    if (output[1].matches(tagsList.get(i).getAddress())) {
                        ReaderDevice readerDevice = tagsList.get(i);
                        int count = readerDevice.getCount();
                        count++;
                        readerDevice.setCount(count);
                        tagsList.set(i, readerDevice);
                        match = true;
                        if (listAdapterMutableLiveData != null) listAdapterMutableLiveData.setValue(readerDevice);
                        break;
                    }
                }
            }
            if (match == false) {
                ReaderDevice readerDevice = new ReaderDevice(null, "", output[1], false, "", 1, 0);
                if (tagsList != null) {
                    if (bAdd2End) tagsList.add(readerDevice);
                    else tagsList.add(0, readerDevice);
                }
                yield++;
                if (barcodeYieldView != null) barcodeYieldView.setText("Unique:" + String.valueOf(yield));
                requestNewSound = true; requestNewVibrate = true;
                if (listAdapterMutableLiveData != null) listAdapterMutableLiveData.setValue(readerDevice);
            }
            total++; allTotal++;
            if (barcodeRateView != null) barcodeRateView.setText("Total:" + String.valueOf(allTotal));
            if (readerListAdapter != null) readerListAdapter.notifyDataSetChanged();

            if (playerN != null && playerO != null) {
                if (requestSound && playerO.isPlaying() == false && playerN.isPlaying() == false) {
                    if (true) {
                        if (bStartBeepWaiting == false) {
                            bStartBeepWaiting = true;
                            handler.postDelayed(runnableStartBeep, 250);
                        }
                        if (MainActivity.mCs108Library4a.getInventoryVibrate()) {
                            boolean validVibrate0 = false, validVibrate = false;
                            if (MainActivity.mCs108Library4a.getVibrateModeSetting() == 0) {
                                if (requestNewVibrate) validVibrate0 = true;
                            } else validVibrate0 = true;
                            requestNewVibrate = false;

                            if (bUseVibrateMode0 && validVibrate0 && bStartVibrateWaiting == false) {
                                if (System.currentTimeMillis() - timeMillisNewVibrate > MainActivity.mCs108Library4a.getVibrateWindow() * 1000 ) {
                                    timeMillisNewVibrate = System.currentTimeMillis();
                                    validVibrate = true;
                                }
                            }
                            if (validVibrate) {
                                MainActivity.mCs108Library4a.setVibrateOn(1);
                                bStartVibrateWaiting = true;
                                handler.postDelayed(runnableStartVibrate, MainActivity.mCs108Library4a.getVibrateTime());
                            }
                        }
                    } else {
                        requestSound = false;
                        if (requestNewSound) {
                            requestNewSound = false;
                            if (playerN != null) playerN.start();
                        } else {
                            if (playerO != null) playerO.start();
                        }
                    }
                }
            }
        } else {
            if (DEBUG) MainActivity.mCs108Library4a.appendToLog("InventoryBarcodeFragment with NULL data");
        }
    }

    boolean bStartBeepWaiting = false;
    Runnable runnableStartBeep = new Runnable() {
        @Override
        public void run() {
            bStartBeepWaiting = false;
            requestSound = false;
            if (requestNewSound) {
                requestNewSound = false;
                playerN.start(); playerN.setVolume(300, 300);
            } else {
                playerO.start(); playerN.setVolume(30, 30);
            }
        }
    };

    boolean bStartVibrateWaiting = false;
    Runnable runnableStartVibrate = new Runnable() {
        @Override
        public void run() {
            bStartVibrateWaiting = false;
        }
    };

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (DEBUG) MainActivity.mCs108Library4a.appendToLog("InventoryBarcodeFragment() onCancelled()");

        DeviceConnectTask4InventoryEnding(taskCancelReason);
    }

    @Override
    protected void onPostExecute(String result) {
        if (DEBUG) MainActivity.mCs108Library4a.appendToLog("InventoryBarcodeFragment() onPostExecute(): " + result);

        DeviceConnectTask4InventoryEnding(taskCancelReason);
    }

    private EditText registerBarValue;
    private ArrayList<ReaderDevice> tagsList;
    private ReaderListAdapter readerListAdapter;
    private TextView barcodeRunTime, barcodeVoltageLevel;
    private TextView barcodeYieldView;
    private Button button, button1; String textButton, btextButton1;
    private TextView barcodeRateView;
    private boolean noToast;
    CustomMediaPlayer playerO, playerN;
    MutableLiveData<ReaderDevice> listAdapterMutableLiveData;

    public InventoryBarcodeTask(ArrayList<ReaderDevice> tagsList, ReaderListAdapter readerListAdapter, EditText registerBarValue,
                                TextView barcodeRunTime, TextView barcodeVoltageLevel,
                                TextView barcodeYieldView, Button button, Button button1, TextView barcodeRateView, boolean noToast, MutableLiveData<ReaderDevice> listAdapterMutableLiveData) {
        this.registerBarValue = registerBarValue;
        this.tagsList = tagsList;
        this.readerListAdapter = readerListAdapter;
        this.barcodeRunTime = barcodeRunTime;
        this.barcodeVoltageLevel = barcodeVoltageLevel;
        this.barcodeYieldView = barcodeYieldView;
        this.button = button; textButton = button.getText().toString();
        this.button1 = button1; if (button1 != null) btextButton1 = button1.getText().toString();
        this.barcodeRateView = barcodeRateView;
        this.noToast = noToast;
        this.listAdapterMutableLiveData = listAdapterMutableLiveData;

        playerO = MainActivity.sharedObjects.playerO;
        playerN = MainActivity.sharedObjects.playerN;
    }

    void DeviceConnectTask4InventoryEnding(TaskCancelRReason taskCancelRReason) {
        if (readerListAdapter != null) readerListAdapter.notifyDataSetChanged();
        MainActivity.mCs108Library4a.barcodeInvventory(false);
        if (DEBUG) MainActivity.mCs108Library4a.appendToLog("DeviceConnectTask4InventoryEnding(): sent setBarcodeOn(false)");
        if (taskCancelReason == null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (noToast == false) Toast.makeText(MainActivity.mContext, R.string.toast_abort_by_END, Toast.LENGTH_SHORT).show();
                    button.setText(textButton);
                    if (button1 != null) button1.setText(btextButton1);
                }
            }, 5000);
        } else {
            if (endingRequest) {
                switch (taskCancelReason) {
                    case STOP:
                        if (noToast == false)
                            Toast.makeText(MainActivity.mContext, R.string.toast_abort_by_STOP, Toast.LENGTH_SHORT).show();
                        break;
                    case BUTTON_RELEASE:
                        if (noToast == false)
                            Toast.makeText(MainActivity.mContext, R.string.toast_abort_by_BUTTON, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
            button.setText(textButton);
            if (button1 != null) button1.setText(btextButton1);
        }
        MainActivity.sharedObjects.runningInventoryBarcodeTask = false;
        MainActivity.mCs108Library4a.setVibrateOn(0);
    }
}