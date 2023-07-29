package com.csl.cs108ademoapp.fragments.Asignacion;

import android.app.ProgressDialog;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelStoreOwner;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.csl.cs108ademoapp.Dialogo;
import com.csl.cs108ademoapp.Generales.MetodosGenerales;
import com.csl.cs108ademoapp.InventoryRfidTask;
import com.csl.cs108ademoapp.MainActivity;
import com.csl.cs108ademoapp.R;
import com.csl.cs108ademoapp.Web.Responses.AssignTagsRfidViewModelDto;
import com.csl.cs108ademoapp.Web.Responses.ProductoDto;
import com.csl.cs108ademoapp.adapters.ReaderListAdapterinvtry;
import com.csl.cs108ademoapp.fragments.CommonFragment;
import com.csl.cs108library4a.Cs108Library4A;
import com.csl.cs108library4a.ReaderDevice;

import java.util.ArrayList;
import java.util.List;

public class AsignacionFragment extends CommonFragment {

    private AsignacionViewModel mViewModel;
    private Spinner finca, tipoProducto, producto, variedad;
    private AutoCompleteTextView bloque;
    private Button button, btn_guardar;
    private InventoryRfidTask inventoryRfidTask;
    private ListView rfidListView;
    private TextView rfidEmptyView;
    private ReaderListAdapterinvtry readerListAdapter;
    private ArrayList<ReaderDevice> listMallas = new ArrayList<>();
    private MutableLiveData<ReaderDevice> readerDeviceMutableLiveData;
    private TextView rfidYieldView;
    private ReaderDevice readerDeviceSala = null;
    private TextView rfidRateView;
    final private boolean bAdd2End = false;
    private String mDid = null;
    private boolean bMultiBank = false, bExtraFilter = false;
    private Spinner spinnerBank1, spinnerBank2;
    private TextView rfidRunTime, rfidVoltageLevel;
    private boolean needResetData = false;
    private ArrayAdapter<ProductoDto> productoDtoArrayAdapter;
    private AssignTagsRfidViewModelDto modelDto = new AssignTagsRfidViewModelDto();

    public static AsignacionFragment newInstance() {
        return new AsignacionFragment();
    }

    public static AsignacionFragment newInstance(boolean bMultiBank, String mDid, boolean bExtraFilter) {
        AsignacionFragment myFragment = new AsignacionFragment();

        Bundle args = new Bundle();
        args.putBoolean("bMultiBank", bMultiBank);
        args.putString("mDid", mDid);
        args.putBoolean("bExtraFilter", bExtraFilter);
        myFragment.setArguments(args);

        return myFragment;
    }

    public AsignacionFragment() {
        super("InventoryRfidiMultiFragment");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_asignacion, container, false);
        finca = view.findViewById(R.id.finca);
        bloque = view.findViewById(R.id.bloque);
        tipoProducto = view.findViewById(R.id.tipoProducto);
        variedad = view.findViewById(R.id.variedad);
        producto = view.findViewById(R.id.producto1);
        button = view.findViewById(R.id.inventoryRfidButton1);
        btn_guardar = view.findViewById(R.id.btn_guardar);
        rfidListView = view.findViewById(R.id.inventoryRfidList1);
        rfidEmptyView = view.findViewById(R.id.inventoryRfidEmpty1);
        rfidYieldView = view.findViewById(R.id.inventoryRfidYield1);
        rfidRateView = view.findViewById(R.id.inventoryRfidRate1);
        spinnerBank1 = view.findViewById(R.id.accessInventoryBank1);
        rfidRunTime = view.findViewById(R.id.inventoryRfidRunTime1);
        rfidVoltageLevel = view.findViewById(R.id.inventoryRfidVoltageLevel1);
        rfidListView.setEmptyView(rfidEmptyView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider((ViewModelStoreOwner) this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(AsignacionViewModel.class)) {
                    return (T) AsignacionViewModel.getInstance(getActivity().getApplication());
                } else {
                    throw new IllegalArgumentException("Unknown ViewModel class");
                }
            }
        }).get(AsignacionViewModel.class);
        if (bMultiBank && mDid == null) {
            android.support.v7.app.ActionBar actionBar;
            actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            actionBar.setTitle("M"); //"Multibank");
            LinearLayout linearLayout = (LinearLayout) getActivity().findViewById(R.id.inventoryMultibankSetting);
            linearLayout.setVisibility(View.VISIBLE);
        }

        ArrayAdapter<CharSequence> lockAdapter = ArrayAdapter.createFromResource(getActivity(), R.array.memoryBank_options, R.layout.custom_spinner_layout);
        lockAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        if (getActivity() == null)
            Log.i("Hello", "InventoryRfidMultiFragment.onActivityCreated: NULL getActivity()");
        else Log.i("Hello", "InventoryRfidMultiFragment.onActivityCreated: VALID getActivity()");
        if (spinnerBank1 == null)
            Log.i("Hello", "InventoryRfidMultiFragment.onActivityCreated: NULL spinnerBank1");
        else Log.i("Hello", "InventoryRfidMultiFragment.onActivityCreated: VALID spinnerBank1");
        if (lockAdapter == null)
            Log.i("Hello", "InventoryRfidMultiFragment.onActivityCreated: NULL lockAdapter");
        else Log.i("Hello", "InventoryRfidMultiFragment.onActivityCreated: VALID lockAdapter");

        spinnerBank1.setAdapter(lockAdapter);
        spinnerBank1.setSelection(2);
        spinnerBank2 = (Spinner) getActivity().findViewById(R.id.accessInventoryBank2);
        spinnerBank2.setAdapter(lockAdapter);
        spinnerBank2.setSelection(3);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStopHandler(false);
            }
        });
        boolean bSelect4detail = false;
        if (bMultiBank == false) bSelect4detail = true;
        else if (mDid != null) {
            if (mDid.matches("E282403") == false) bSelect4detail = true;
        }
        readerListAdapter = new ReaderListAdapterinvtry(getActivity(), R.layout.readers_list_item_inv, listMallas, bSelect4detail);
        rfidListView.setAdapter(readerListAdapter);

        readerDeviceMutableLiveData = new MutableLiveData<>();
        readerDeviceMutableLiveData.observeForever(new Observer<ReaderDevice>() {
            @Override
            public void onChanged(@Nullable final ReaderDevice readerDevice) {
                if (readerDevice != null) {
                    if (readerDevice.getAddress().startsWith("1111")) {
                        listMallas.add(readerDevice);
                        readerListAdapter.notifyDataSetChanged();
                        rfidYieldView.setText(listMallas.size() + "");
                    }
                }
            }
        });

        mViewModel.loadProductos(null);
        productoDtoArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, new ArrayList<ProductoDto>());
        producto.setAdapter(productoDtoArrayAdapter);


        mViewModel.getProductoMutableLiveData().observeForever(new Observer<List<ProductoDto>>() {
            @Override
            public void onChanged(@Nullable List<ProductoDto> productoDtos) {
                productoDtoArrayAdapter.clear();
                ProductoDto nullValue = new ProductoDto();
                nullValue.Id = null;
                nullValue.Nombre = "Seleccione...";
                productoDtoArrayAdapter.add(nullValue);
                productoDtoArrayAdapter.addAll(productoDtos);
                productoDtoArrayAdapter.notifyDataSetChanged();
            }
        });




        producto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ProductoDto productoDto = productoDtoArrayAdapter.getItem(position);
                modelDto.ProductoId = productoDto.Id;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });



        bloque.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                modelDto.BloqueId = null;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    DialogoConfirmacion();
                }
            }
        });
    }

    public boolean validate() {
        String mensaje = "";
        if (modelDto.FincaId == null) {
            mensaje += "Seleccione una finca\n";
        }
        if (modelDto.BloqueId == null) {
            mensaje += "Seleccione un bloque\n";
        }
        if (modelDto.ProductoId == null) {
            mensaje += "Seleccione un producto\n";
        }
        if (modelDto.VariedadId == null) {
            mensaje += "Seleccione una variedad\n";
        }
        if (modelDto.TipoProductoId == null) {
            mensaje += "Seleccione un tipo de producto\n";
        }
        if (listMallas.size() == 0) {
            mensaje += "La lista no puede estar vacia\n";
        }
        if (!mensaje.equals("")) {
            Mensaje(mensaje);
            return false;
        }
        return true;
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

    void clearTagsList() {
        MainActivity.mCs108Library4a.appendToLog("runningInventoryRfidTask = " + MainActivity.sharedObjects.runningInventoryRfidTask + ", readerListAdapter = " + (readerListAdapter != null ? String.valueOf(readerListAdapter.getCount()) : "NULL"));
        if (MainActivity.sharedObjects.runningInventoryRfidTask) return;
        rfidYieldView.setText("");
        //rfidRateView.setText("");
        MainActivity.tagSelected = null;
        MainActivity.sharedObjects.tagsList.clear();
        MainActivity.sharedObjects.tagsIndexList.clear();

        MainActivity.mLogView.setText("");

        readerDeviceSala = null;
        listMallas.clear();
        readerListAdapter.notifyDataSetChanged();
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

    public void DialogoConfirmacion() {
        final Dialogo dialogBuilder = Dialogo.getInstance(getActivity());

        dialogBuilder
                .withTitle("Cayman Aviso")
                .withMessage("Los datos se guardarán. Desea Continuar?")
                .withIcon(getResources().getDrawable(R.drawable.pregunta))
                .isCancelableOnTouchOutside(false)
                .withDuration(700)
                .withEffect(MetodosGenerales.effect)
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
            for (ReaderDevice listMalla : listMallas) {
                tags.add(listMalla.getAddress());
            }

            modelDto.tagsRfid = tags;

            boolean b = mViewModel.asignarTags(modelDto);
            if (b) {
                return true;
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
            pdia.setMessage("Asignando...");
            pdia.setCancelable(false);
            pdia.show();
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            //super.onPostExecute(aVoid);
            pdia.dismiss();
            if (aVoid.booleanValue()) {
                clearTagsList();
                Mensaje("Tags Asignados!");
                finca.setSelection(0);
                producto.setSelection(0);
                variedad.setSelection(0);
                tipoProducto.setSelection(0);
                bloque.setText("");
            } else {
                Mensaje("Imposible Asignar!");
            }

            //txt_contador.setText("Códigos Guardados: "+ db.ContarStock().toString());
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
                .withEffect(MetodosGenerales.effect)
                .withButton1Text("OK")
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.cancel();
                    }
                })
                .show();
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
        MainActivity.sharedObjects.tagsList.clear();
        MainActivity.sharedObjects.tagsIndexList.clear();
        // MainActivity.mCs108Library4a.setVibrateTime(vibrateTimeBackup);
        if (DEBUG)
            MainActivity.mCs108Library4a.appendToLog("InventoryRfidiMultiFragment().onDestory(): onDestory()");
        super.onDestroy();
    }
}