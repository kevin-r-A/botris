package com.csl.cs108ademoapp.fragments;

import android.app.ProgressDialog;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.csl.cs108ademoapp.Dialogo;
import com.csl.cs108ademoapp.Efectos.Effectstype;
import com.csl.cs108ademoapp.Entities.Clases.STOCK;
import com.csl.cs108ademoapp.InventoryRfidTask;
import com.csl.cs108ademoapp.MainActivity;
import com.csl.cs108ademoapp.R;
import com.csl.cs108ademoapp.SaveList2ExternalTask;
import com.csl.cs108ademoapp.Sqlite.DBHelper;
import com.csl.cs108ademoapp.Web.Repository.WebServiceRepository;
import com.csl.cs108ademoapp.adapters.ReaderListAdapterinvtry;
import com.csl.cs108library4a.Cs108Connector;
import com.csl.cs108library4a.Cs108Library4A;
import com.csl.cs108library4a.ReaderDevice;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryRfidiMultiFragment extends CommonFragment {
    final private boolean bAdd2End = false;
    private boolean bMultiBank = false, bExtraFilter = false;
    private String mDid = null;
    int vibrateTimeBackup;

    private Spinner spinnerBank1, spinnerBank2;
    private ListView rfidListView;
    private TextView rfidEmptyView;
    private ListView rfidListViewCosechador;
    private TextView rfidEmptyViewCosechador;
    private TextView rfidRunTime, rfidVoltageLevel;
    private TextView rfidYieldView;
    private TextView inventoryRfidCosechador;
    private TextView rfidRateView;
    private Button button, btn_cancelar, btn_guardar;
    private MutableLiveData<ReaderDevice> readerDeviceMutableLiveData;
    private ArrayList<ReaderDevice> listCosechadores = new ArrayList<>();
    private ArrayList<ReaderDevice> listMallas = new ArrayList<>();
    private EditText txtCodigoSala;
    private EditText txtNombreSala;

    private ReaderListAdapterinvtry readerListAdapter;
    private ReaderListAdapterinvtry readerListAdapterCosechador;
    private WebServiceRepository webServiceRepository;
    private ReaderDevice readerDeviceSala = null;

    private InventoryRfidTask inventoryRfidTask;
    Effectstype effect;
    DBHelper db;

    void clearTagsList() {
        MainActivity.mCs108Library4a.appendToLog("runningInventoryRfidTask = " + MainActivity.sharedObjects.runningInventoryRfidTask + ", readerListAdapter = " + (readerListAdapter != null ? String.valueOf(readerListAdapter.getCount()) : "NULL"));
        if (MainActivity.sharedObjects.runningInventoryRfidTask) return;
        rfidYieldView.setText("");
        inventoryRfidCosechador.setText("");
        rfidRateView.setText("");
        MainActivity.tagSelected = null;
        MainActivity.sharedObjects.tagsList.clear();
        MainActivity.sharedObjects.tagsIndexList.clear();

        MainActivity.mLogView.setText("");

        readerDeviceSala = null;
        txtCodigoSala.setText("");
        txtNombreSala.setText("");
        listCosechadores.clear();
        listMallas.clear();
        readerListAdapterCosechador.notifyDataSetChanged();
        readerListAdapter.notifyDataSetChanged();
    }

    void sortTagsList() {
        if (MainActivity.sharedObjects.runningInventoryRfidTask) return;
        Collections.sort(MainActivity.sharedObjects.tagsList);
        readerListAdapter.notifyDataSetChanged();

        readerListAdapterCosechador.notifyDataSetChanged();
    }

    void saveTagsList() {
        if (MainActivity.sharedObjects.runningInventoryRfidTask) return;
        SaveList2ExternalTask saveExternalTask = new SaveList2ExternalTask(MainActivity.sharedObjects.tagsList);
        saveExternalTask.execute();
    }

    void shareTagsList() {
        SaveList2ExternalTask saveExternalTask = new SaveList2ExternalTask(MainActivity.sharedObjects.tagsList);
        String stringOutput = saveExternalTask.createStrEpcList();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, stringOutput);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Sharing to"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState, bMultiBank && mDid == null);
        return inflater.inflate(R.layout.fragment_inventory_rfid_multi, container, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuAction_1:
                clearTagsList();
                return true;
            case R.id.menuAction_2:
                sortTagsList();
                return true;
            case R.id.menuAction_3:
                saveTagsList();
                return true;
            case R.id.menuAction_4:
                shareTagsList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bMultiBank = getArguments().getBoolean("bMultiBank");
            mDid = getArguments().getString("mDid");
            bExtraFilter = getArguments().getBoolean("bExtraFilter");
            MainActivity.mCs108Library4a.appendToLog("bExtraFilter" + bExtraFilter + ", mDid = " + mDid);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        MainActivity.mCs108Library4a.abortOperation();
        webServiceRepository = new WebServiceRepository(getActivity().getApplication());
        if (bMultiBank && mDid == null) {
            android.support.v7.app.ActionBar actionBar;
            actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            actionBar.setTitle("M"); //"Multibank");
            LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.inventoryMultibankSetting);
            linearLayout.setVisibility(View.VISIBLE);
        }

        LinearLayout layoutSetting = (LinearLayout) getActivity().findViewById(R.id.inventoryMultibankSetting);

        ArrayAdapter<CharSequence> lockAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.memoryBank_options, R.layout.custom_spinner_layout);
        lockAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerBank1 = (Spinner) getActivity().findViewById(R.id.accessInventoryBank1);
        if (getActivity() == null)
            Log.i("Hello", "InventoryRfidMultiFragment.onActivityCreated: NULL getActivity()");
        else Log.i("Hello", "InventoryRfidMultiFragment.onActivityCreated: VALID getActivity()");
        if (spinnerBank1 == null)
            Log.i("Hello", "InventoryRfidMultiFragment.onActivityCreated: NULL spinnerBank1");
        else Log.i("Hello", "InventoryRfidMultiFragment.onActivityCreated: VALID spinnerBank1");
        if (lockAdapter == null)
            Log.i("Hello", "InventoryRfidMultiFragment.onActivityCreated: NULL lockAdapter");
        else Log.i("Hello", "InventoryRfidMultiFragment.onActivityCreated: VALID lockAdapter");

        txtCodigoSala = getActivity().findViewById(R.id.txtCodigoSala);
        txtCodigoSala.setText("");
        txtNombreSala = getActivity().findViewById(R.id.txtNombreSala);
        txtNombreSala.setText("");

        spinnerBank1.setAdapter(lockAdapter);
        spinnerBank1.setSelection(2);
        spinnerBank2 = (Spinner) getActivity().findViewById(R.id.accessInventoryBank2);
        spinnerBank2.setAdapter(lockAdapter);
        spinnerBank2.setSelection(3);

        rfidListView = (ListView) getActivity().findViewById(R.id.inventoryRfidList1);
        rfidEmptyView = (TextView) getActivity().findViewById(R.id.inventoryRfidEmpty1);
        rfidListView.setEmptyView(rfidEmptyView);

        rfidListViewCosechador = (ListView) getActivity().findViewById(R.id.inventoryRfidListCosechador);
        rfidEmptyViewCosechador = (TextView) getActivity().findViewById(R.id.inventoryRfidEmptyCosechador);
        rfidListViewCosechador.setEmptyView(rfidEmptyViewCosechador);

        boolean bSelect4detail = false;
        if (bMultiBank == false) bSelect4detail = true;
        else if (mDid != null) {
            if (mDid.matches("E282403") == false) bSelect4detail = true;
        }
        readerListAdapter = new ReaderListAdapterinvtry(getActivity(), R.layout.readers_list_item_inv, listMallas, bSelect4detail);
        rfidListView.setAdapter(readerListAdapter);
        rfidListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        rfidListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ReaderDevice readerDevice = readerListAdapter.getItem(position);
                if (DEBUG) MainActivity.mCs108Library4a.appendToLog("Position  = " + position);
                if (readerDevice.getSelected()) {
                    readerDevice.setSelected(false);
                } else {
                    readerDevice.setSelected(true);
                }
                MainActivity.sharedObjects.tagsList.set(position, readerDevice);
                MainActivity.tagSelected = readerDevice;
                for (int i = 0; i < MainActivity.sharedObjects.tagsList.size(); i++) {
                    if (i != position) {
                        ReaderDevice readerDevice1 = MainActivity.sharedObjects.tagsList.get(i);
                        if (readerDevice1.getSelected()) {
                            readerDevice1.setSelected(false);
                            MainActivity.sharedObjects.tagsList.set(i, readerDevice1);
                        }
                    }
                }
                readerListAdapter.notifyDataSetChanged();
            }
        });

        readerListAdapterCosechador = new ReaderListAdapterinvtry(getActivity(), R.layout.readers_list_item_inv, listCosechadores, bSelect4detail);
        rfidListViewCosechador.setAdapter(readerListAdapterCosechador);

        rfidRunTime = (TextView) getActivity().findViewById(R.id.inventoryRfidRunTime1);
        rfidVoltageLevel = (TextView) getActivity().findViewById(R.id.inventoryRfidVoltageLevel1);
        TextView rfidFilterOn = (TextView) getActivity().findViewById(R.id.inventoryRfidFilterOn1);
        if (MainActivity.mCs108Library4a.getSelectEnable() == false && MainActivity.mCs108Library4a.getInvMatchEnable() == false)
            rfidFilterOn.setVisibility(View.INVISIBLE);

        rfidYieldView = (TextView) getActivity().findViewById(R.id.inventoryRfidYield1);
        inventoryRfidCosechador = (TextView) getActivity().findViewById(R.id.inventoryRfidCosechador);
        rfidRateView = (TextView) getActivity().findViewById(R.id.inventoryRfidRate1);
        button = (Button) getActivity().findViewById(R.id.inventoryRfidButton1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStopHandler(false);
            }
        });

        readerDeviceMutableLiveData = new MutableLiveData<>();
        readerDeviceMutableLiveData.observeForever(new Observer<ReaderDevice>() {
            @Override
            public void onChanged(@Nullable final ReaderDevice readerDevice) {
                if (readerDevice != null) {
                    if (readerDevice.getAddress().startsWith("2222")) {
                        listCosechadores.add(readerDevice);
                        readerListAdapterCosechador.notifyDataSetChanged();
                        inventoryRfidCosechador.setText(listCosechadores.size() + "");

                    } else if (readerDevice.getAddress().startsWith("1111")) {
                        listMallas.add(readerDevice);
                        readerListAdapter.notifyDataSetChanged();
                        rfidYieldView.setText(listMallas.size() + "");
                    } else if (readerDevice.getAddress().startsWith("4444")) {
                        txtCodigoSala.setText(readerDevice.getAddress());
                        final List<String> strings = new ArrayList<>();
                        strings.add(readerDevice.getAddress());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final List<String> strings1 = webServiceRepository.GetSalas(strings);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (strings1.size() > 0) {
                                            txtNombreSala.setText(strings1.get(0));
                                            readerDeviceSala = readerDevice;
                                        } else {
                                            Toast.makeText(getContext(), "Trabajador no encontrado", Toast.LENGTH_SHORT).show();
                                            txtCodigoSala.setText("");
                                            txtNombreSala.setText("");
                                            readerDeviceSala = null;
                                        }
                                    }
                                });
                            }
                        }).start();
                    }
                }

            }
        });

        vibrateTimeBackup = MainActivity.mCs108Library4a.getVibrateTime();
        final Button buttonT1 = (Button) getActivity().findViewById(R.id.inventoryRfidButtonT1);
        buttonT1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String buttonText = buttonT1.getText().toString().trim();
                if (buttonText.toUpperCase().matches("BUZ")) {
                    MainActivity.mCs108Library4a.setVibrateTime(0);
                    MainActivity.mCs108Library4a.setVibrateOn(1);
                    buttonT1.setText("DETENER");
                } else {
                    MainActivity.mCs108Library4a.setVibrateOn(0);
                    buttonT1.setText("BUZ");
                }
            }
        });

        db = new DBHelper(getActivity(), "");

        btn_cancelar = getActivity().findViewById(R.id.btn_cancelar);
        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearTagsList();
                //db.EliminarLectura();
            }
        });

        btn_guardar = getActivity().findViewById(R.id.btn_guardar);
        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (rfidListView.getCount() > 0 && readerDeviceSala != null) {
                    DialogoConfirmacion();
                } else {
                    if (rfidListView.getCount() == 0) {
                        Mensaje("La lista no puede estar vacia...");
                    }
                    if (readerDeviceSala == null) {
                        Mensaje("No existe Operario...");
                    }
                }
            }
        });

//        rfidYieldView.setText(readerListAdapter.getCount());
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DEBUG)
            MainActivity.mCs108Library4a.appendToLog("InventoryRfidiMultiFragment().onResume(): userVisibleHint = " + userVisibleHint);
        if (userVisibleHint) {
            setNotificationListener();
        }
    }

    @Override
    public void onPause() {
        MainActivity.mCs108Library4a.setNotificationListener(null);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        MainActivity.mCs108Library4a.setNotificationListener(null);
        if (inventoryRfidTask != null) {
            if (DEBUG)
                MainActivity.mCs108Library4a.appendToLog("InventoryRfidiMultiFragment().onDestory(): VALID inventoryRfidTask");
            inventoryRfidTask.taskCancelReason = InventoryRfidTask.TaskCancelRReason.DESTORY;
        }
        resetSelectData();
        MainActivity.mCs108Library4a.setVibrateTime(vibrateTimeBackup);
        if (DEBUG)
            MainActivity.mCs108Library4a.appendToLog("InventoryRfidiMultiFragment().onDestory(): onDestory()");
        super.onDestroy();
    }

    boolean userVisibleHint = true;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            userVisibleHint = true;
            MainActivity.mCs108Library4a.appendToLog("InventoryRfidiMultiFragment is now VISIBLE");
            setNotificationListener();
        } else {
            userVisibleHint = false;
            MainActivity.mCs108Library4a.appendToLog("InventoryRfidiMultiFragment is now INVISIBLE");
            MainActivity.mCs108Library4a.setNotificationListener(null);
            if (inventoryRfidTask != null) {
                inventoryRfidTask.taskCancelReason = InventoryRfidTask.TaskCancelRReason.STOP;
            }
        }
    }

    public static InventoryRfidiMultiFragment newInstance(boolean bMultiBank, String mDid, boolean bExtraFilter) {
        InventoryRfidiMultiFragment myFragment = new InventoryRfidiMultiFragment();

        Bundle args = new Bundle();
        args.putBoolean("bMultiBank", bMultiBank);
        args.putString("mDid", mDid);
        args.putBoolean("bExtraFilter", bExtraFilter);
        myFragment.setArguments(args);

        return myFragment;
    }

    public InventoryRfidiMultiFragment() {
        super("InventoryRfidiMultiFragment");
    }

    void setNotificationListener() {
        MainActivity.mCs108Library4a.setNotificationListener(new Cs108Connector.NotificationListener() {
            @Override
            public void onChange() {
                MainActivity.mCs108Library4a.appendToLog("TRIGGER key is pressed.");
                startStopHandler(true);
            }
        });
    }

    boolean needResetData = false;

    void resetSelectData() {
        if (DEBUG) MainActivity.mCs108Library4a.appendToLog("needResetData = " + needResetData);
        MainActivity.mCs108Library4a.restoreAfterTagSelect();
        if (needResetData) {
            MainActivity.mCs108Library4a.setTagRead(0);
            MainActivity.mCs108Library4a.setAccessBank(1);
            MainActivity.mCs108Library4a.setAccessOffset(0);
            MainActivity.mCs108Library4a.setAccessCount(0);
            needResetData = false;
        }
    }

    void startStopHandler(boolean buttonTrigger) {
        if (buttonTrigger)
            MainActivity.mCs108Library4a.appendToLog("BARTRIGGER: getTriggerButtonStatus = " + MainActivity.mCs108Library4a.getTriggerButtonStatus());
        if (MainActivity.sharedObjects.runningInventoryBarcodeTask) {
            Toast.makeText(MainActivity.mContext, "Running barcode inventory", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean started = false;
        if (inventoryRfidTask != null)
            if (inventoryRfidTask.getStatus() == AsyncTask.Status.RUNNING) started = true;
        if (buttonTrigger && ((started && MainActivity.mCs108Library4a.getTriggerButtonStatus()) || (started == false && MainActivity.mCs108Library4a.getTriggerButtonStatus() == false))) {
            MainActivity.mCs108Library4a.appendToLog("BARTRIGGER: trigger ignore");
            return;
        }
        if (started == false) {
            if (MainActivity.mCs108Library4a.isBleConnected() == false) {
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

    void startInventoryTask() {
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
                if (true) {
                    extra1Bank = 0;
                    extra1Offset = 13;
                    extra1Count = 2;
                    extra2Bank = 3;
                    extra2Offset = 8;
                    extra2Count = 4;
                } else {
                    extra2Bank = 0;
                    extra2Offset = 13;
                    extra2Count = 2;
                }
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
//        extra2Offset = 0x10d; extra2Count = 1; //0 - 0x60: Ok, 70-B0, EC-F0: long, , C0-D0 no response for 30 seconds
        } else if (bMultiBank) {
            CheckBox checkBox = (CheckBox) getActivity().findViewById(R.id.accessInventoryBankTitle1);
            if (checkBox.isChecked()) {
                extra1Bank = spinnerBank1.getSelectedItemPosition();
                EditText editText = (EditText) getActivity().findViewById(R.id.accessInventoryOffset1);
                extra1Offset = Integer.valueOf(editText.getText().toString());
                editText = (EditText) getActivity().findViewById(R.id.accessInventoryLength1);
                extra1Count = Integer.valueOf(editText.getText().toString());
            }
            checkBox = (CheckBox) getActivity().findViewById(R.id.accessInventoryBankTitle2);
            if (checkBox.isChecked()) {
                extra2Bank = spinnerBank2.getSelectedItemPosition();
                EditText editText = (EditText) getActivity().findViewById(R.id.accessInventoryOffset2);
                extra2Offset = Integer.valueOf(editText.getText().toString());
                editText = (EditText) getActivity().findViewById(R.id.accessInventoryLength2);
                extra2Count = Integer.valueOf(editText.getText().toString());
            }
//                extra2Bank = 3; extra2Count = 4;
        }

        if (bMultiBank == false) {
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


    public void DialogoConfirmacion() {
        final Dialogo dialogBuilder = Dialogo.getInstance(getActivity());

        dialogBuilder
                .withTitle("Cayman Aviso")
                .withMessage("Los datos se guardarán. Desea Continuar?")
                .withIcon(getResources().getDrawable(R.drawable.pregunta))
                .isCancelableOnTouchOutside(false)
                .withDuration(700)
                .withEffect(effect)
                .withButton1Text("SI")
                .withButton2Text("NO")

                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DialogGuardar().execute();
                        dialogBuilder.cancel();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.cancel();
                    }
                })
                .show();
    }

    private class DialogGuardar extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pdia;

        @Override
        protected Boolean doInBackground(Void... voids) {
            List<String> tags = new ArrayList<>();
            tags.add(readerDeviceSala.getAddress());
            for (ReaderDevice listMalla : listMallas) {
                tags.add(listMalla.getAddress());
            }
            for (ReaderDevice listCosechadore : listCosechadores) {
                tags.add(listCosechadore.getAddress());
            }

            return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
            pdia.setMessage("Liberando...");
            pdia.setCancelable(false);
            pdia.show();
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            //super.onPostExecute(aVoid);
            pdia.dismiss();
            if (aVoid.booleanValue()) {
                clearTagsList();

            } else {
                Mensaje("Imposible Liberar!");
            }

            //txt_contador.setText("Códigos Guardados: "+ db.ContarStock().toString());
        }
    }


    public void InsertarTag() {
        db.ActualizaEstadoActivo();
        db.EliminarStockLectura();


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                ((MainActivity) getActivity()).database.activo_dao().EliminaEstadoActivo();
                ((MainActivity) getActivity()).database.stock_dao().deleteAll();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<String> ldatos;
        //ldatos= db.CargarLectura();

        for (int i = 0; i < readerListAdapter.getCount(); i++) {
            String cooood = readerListAdapter.getItem(i).getAddress();
            final STOCK stock = new STOCK();
            stock.STO_CODIGO = cooood;
            stock.STO_ESTADO = "S";
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) getActivity()).database.stock_dao().insert(stock);
                }
            });
            thread.start();
            // if(!ldatos.contains(cooood))
            //{
            db.GuardarStock(cooood, "S");
            //}
        }

    }


    public void Mensaje(String mensaje) {
        final Dialogo dialogBuilder = Dialogo.getInstance(getActivity());

        dialogBuilder
                .withTitle("Cayman Aviso")
                .withMessage(mensaje)
                .withIcon(getResources().getDrawable(R.drawable.informacion))
                .isCancelableOnTouchOutside(false)
                .withDuration(700)
                .withEffect(effect)
                .withButton1Text("OK")
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.cancel();
                    }
                })
                .show();
    }
}
