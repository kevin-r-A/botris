package com.csl.cs108ademoapp.Generales;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.csl.cs108ademoapp.InventoryRfidTask;
import com.csl.cs108ademoapp.MainActivity;
import com.csl.cs108ademoapp.R;
import com.csl.cs108ademoapp.adapters.ReaderListAdapterinvtry;
import com.csl.cs108library4a.Cs108Library4A;
import com.csl.cs108library4a.ReaderDevice;

public final class LecturaCslRFID {

    static private InventoryRfidTask inventoryRfidTask = null;
    static private boolean bAdd2End = false;
    static private String mDid = null;
    static private boolean bMultiBank = false;
    static private boolean bExtraFilter = false;
    static private TextView rfidRunTime;
    static private TextView rfidVoltageLevel;
    static private boolean needResetData = false;
    static private MutableLiveData<ReaderDevice> readerDeviceMutableLiveData = new MutableLiveData<>();
    static private TextView rfidRateView;
    static private ReaderListAdapterinvtry readerListAdapter;
    static private Context context;
    static private ListView rfidListView;
    static private Button button;

    public static void setRfidListView(ListView rfidListView) {
        LecturaCslRFID.rfidListView = rfidListView;
    }

    public static void setButton(Button button) {
        LecturaCslRFID.button = button;
    }

    public static void setbAdd2End(boolean bAdd2End) {
        LecturaCslRFID.bAdd2End = bAdd2End;
    }

    public static void setmDid(String mDid) {
        LecturaCslRFID.mDid = mDid;
    }

    public static void setbMultiBank(boolean bMultiBank) {
        LecturaCslRFID.bMultiBank = bMultiBank;
    }

    public static void setbExtraFilter(boolean bExtraFilter) {
        LecturaCslRFID.bExtraFilter = bExtraFilter;
    }

    public static MutableLiveData<ReaderDevice> getReaderDeviceMutableLiveData() {
        return readerDeviceMutableLiveData;
    }

    public static Context getContext() {
        return context;
    }

    public static void setContext(Context context) {
        LecturaCslRFID.context = context;
    }

    public static void startStopHandler(boolean buttonTrigger) {
        if (buttonTrigger)
            MainActivity.mCs108Library4a.appendToLog("BARTRIGGER: getTriggerButtonStatus = " + MainActivity.mCs108Library4a.getTriggerButtonStatus());
        if (MainActivity.sharedObjects.runningInventoryBarcodeTask) {
            Toast.makeText(MainActivity.mContext, "Running barcode inventory", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean started = false;
        if (inventoryRfidTask != null)
            if (inventoryRfidTask.getStatus() == AsyncTask.Status.RUNNING) started = true;
        if (buttonTrigger && ((started && MainActivity.mCs108Library4a.getTriggerButtonStatus()) || (!started && !MainActivity.mCs108Library4a.getTriggerButtonStatus()))) {
            MainActivity.mCs108Library4a.appendToLog("BARTRIGGER: trigger ignore");
            return;
        }
        if (!started) {
            if (!MainActivity.mCs108Library4a.isBleConnected()) {
                Toast.makeText(MainActivity.mContext, R.string.toast_ble_not_connected, Toast.LENGTH_SHORT).show();
                return;
            } else if (MainActivity.mCs108Library4a.isRfidFailure()) {
                Toast.makeText(MainActivity.mContext, "Rfid is disabled", Toast.LENGTH_SHORT).show();
                return;
            } else if (MainActivity.mCs108Library4a.mrfidToWriteSize() != 0) {
                Toast.makeText(MainActivity.mContext, R.string.toast_not_ready, Toast.LENGTH_SHORT).show();
                return;
            }
            if (bAdd2End) rfidListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            else rfidListView.setSelection(0);
            startInventoryTask();
        } else {
            MainActivity.mCs108Library4a.appendToLogView("CANCELLING. Set taskCancelReason");
            if (bAdd2End) rfidListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
            if (buttonTrigger)
                inventoryRfidTask.taskCancelReason = InventoryRfidTask.TaskCancelRReason.BUTTON_RELEASE;
            else inventoryRfidTask.taskCancelReason = InventoryRfidTask.TaskCancelRReason.STOP;
        }
    }

    public static void destroy() {

        MainActivity.mCs108Library4a.appendToLogView("CANCELLING. Set taskCancelReason");
        if (bAdd2End) rfidListView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_NORMAL);
        if (inventoryRfidTask != null && MainActivity.sharedObjects.runningInventoryRfidTask)
            inventoryRfidTask.taskCancelReason = InventoryRfidTask.TaskCancelRReason.STOP;
        clearTagsList();
        setButton(null);
        setRfidListView(null);
        setContext(null);
        readerDeviceMutableLiveData = new MutableLiveData<>();
    }

    public static void clearTagsList() {
        MainActivity.mCs108Library4a.appendToLog("runningInventoryRfidTask = " + MainActivity.sharedObjects.runningInventoryRfidTask + ", readerListAdapter = " + (readerListAdapter != null ? String.valueOf(readerListAdapter.getCount()) : "NULL"));
        if (MainActivity.sharedObjects.runningInventoryRfidTask) return;
        MainActivity.tagSelected = null;
        MainActivity.sharedObjects.tagsList.clear();
        MainActivity.sharedObjects.tagsIndexList.clear();

        MainActivity.mLogView.setText("");
        if (readerListAdapter != null) readerListAdapter.notifyDataSetChanged();
    }

    static void startInventoryTask() {
        int extra1Bank = -1, extra2Bank = -1;
        int extra1Count = 0, extra2Count = 0;
        int extra1Offset = 0;
        int extra2Offset = 0;

        if (mDid != null) {
            extra1Bank = 2;
            extra1Offset = 0;
            extra1Count = 2;
            if (mDid.matches("E280B")) {
                extra1Count = 6;
//                extra2Bank = 3;
//                extra2Offset = 0x100;
//                extra2Count = 2;
            } else if (mDid.matches("E28240")) {
                extra2Bank = 3;
                extra2Offset = 8;
                extra2Count = 4;
            } else if (mDid.matches("E282403")) {
                extra1Bank = 0;
                extra1Offset = 13;
                extra1Count = 2;
                extra2Bank = 3;
                extra2Offset = 8;
                extra2Count = 4;

            } else {
                extra1Count = (mDid.length() * 4) / 16;
                if (extra1Count * 16 != mDid.length() * 4) extra1Count++;
            }
            MainActivity.mCs108Library4a.setSelectedTagByTID(mDid, 300);
            if (mDid.matches("E282403")) {
                MainActivity.mCs108Library4a.setInvSelectIndex(1);
                MainActivity.mCs108Library4a.setSelectCriteria(true, 4, 2, 3, 0xe0, "");
                MainActivity.mCs108Library4a.setInvSelectIndex(2);
                MainActivity.mCs108Library4a.setSelectCriteria(true, 4, 2, 3, 0xD0, "20");
                MainActivity.mCs108Library4a.appendToLog("setSelectCriteria 1 = TRUE");
                MainActivity.mCs108Library4a.setInvSelectIndex(0);
            }
        }

        if (!bMultiBank) {
            MainActivity.mCs108Library4a.startOperation(Cs108Library4A.OperationTypes.TAG_INVENTORY_COMPACT);
            inventoryRfidTask = new InventoryRfidTask(getContext(), -1, -1, 0, 0, 0, 0,
                    false, MainActivity.mCs108Library4a.getInventoryBeep(),
                    MainActivity.sharedObjects.tagsList, readerListAdapter, null, null, null,
                    rfidRunTime, null, rfidVoltageLevel, null, button, rfidRateView, readerDeviceMutableLiveData);
        } else {
            if ((extra1Bank != -1 && extra1Count != 0) || (extra2Bank != -1 && extra2Count != 0)) {
                if (extra1Bank == -1 || extra1Count == 0) {
                    extra1Bank = extra2Bank;
                    extra2Bank = 0;
                    extra1Count = extra2Count;
                    extra2Count = 0;
                    extra1Offset = extra2Offset;
                    extra2Offset = 0;
                }
                if (extra1Bank == 1) extra1Offset += 2;
                if (extra2Bank == 1) extra2Offset += 2;
                MainActivity.mCs108Library4a.setTagRead(extra2Count != 0 && extra2Count != 0 ? 2 : 1);
                MainActivity.mCs108Library4a.setAccessBank(extra1Bank, extra2Bank);
                MainActivity.mCs108Library4a.setAccessOffset(extra1Offset, extra2Offset);
                MainActivity.mCs108Library4a.setAccessCount(extra1Count, extra2Count);
                needResetData = true;
            } else resetSelectData();
            MainActivity.mCs108Library4a.startOperation(Cs108Library4A.OperationTypes.TAG_INVENTORY);
            inventoryRfidTask = new InventoryRfidTask(getContext(), extra1Bank, extra2Bank, extra1Count, extra2Count, extra1Offset, extra2Offset,
                    false, MainActivity.mCs108Library4a.getInventoryBeep(),
                    MainActivity.sharedObjects.tagsList, readerListAdapter, null, (bExtraFilter ? mDid : null), mDid,
                    rfidRunTime, null, rfidVoltageLevel, null, button, rfidRateView, readerDeviceMutableLiveData);
        }
        inventoryRfidTask.execute();
    }

    static void resetSelectData() {
        MainActivity.mCs108Library4a.restoreAfterTagSelect();
        if (needResetData) {
            MainActivity.mCs108Library4a.setTagRead(0);
            MainActivity.mCs108Library4a.setAccessBank(1);
            MainActivity.mCs108Library4a.setAccessOffset(0);
            MainActivity.mCs108Library4a.setAccessCount(0);
            needResetData = false;
        }
    }
}
