package com.csl.cs108ademoapp.fragments.AsignacionBarras;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelStoreOwner;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.csl.cs108ademoapp.InventoryBarcodeTask;
import com.csl.cs108ademoapp.MainActivity;
import com.csl.cs108ademoapp.R;
import com.csl.cs108ademoapp.Web.Responses.MovimientoProductoDto;
import com.csl.cs108ademoapp.Web.Responses.ProductoDto;
import com.csl.cs108ademoapp.adapters.AsignacionBarrasListAdapter;
import com.csl.cs108ademoapp.adapters.ReaderListAdapter;
import com.csl.cs108ademoapp.fragments.Asignacion.AsignacionViewModel;
import com.csl.cs108ademoapp.fragments.CommonFragment;
import com.csl.cs108library4a.Cs108Connector;
import com.csl.cs108library4a.ReaderDevice;

import java.util.ArrayList;
import java.util.List;

public class AsignacionBarrasFragment extends CommonFragment {

    private AsignacionBarrasViewModel mViewModel;
    private ListView barcodeListView;
    private TextView barcodeEmptyView;
    private TextView barcodeRunTime, barcodeVoltageLevel;
    private TextView barcodeYieldView, barcodeTotal;
    private ImageButton btnCancelar;
    private Button button;
    private Button btnAgregar, btnLimpiar, btnGuardar;
    boolean userVisibleHint = false;
    private MutableLiveData<ReaderDevice> readerDeviceMutableLiveData = new MutableLiveData<>();
    public AsignacionBarrasListAdapter readerListAdapter;
    private ArrayList<MovimientoProductoDto> movimientoProductoDtoList = new ArrayList<>();
    private EditText txtCodigoBarras, txtRfid;
    private boolean nuevaAsignacion = true;

    InventoryBarcodeTask inventoryBarcodeTask;

    void clearTagsList() {
        barcodeYieldView.setText("");
        barcodeTotal.setText("");
        MainActivity.sharedObjects.barsList.clear();
        movimientoProductoDtoList.clear();
        readerListAdapter.notifyDataSetChanged();
    }

    public static AsignacionBarrasFragment newInstance() {
        return new AsignacionBarrasFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_asignacion_barras, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        barcodeListView = (ListView) getActivity().findViewById(R.id.inventoryBarcodeList);
        barcodeEmptyView = (TextView) getActivity().findViewById(R.id.inventoryBarcodeEmpty);
        txtCodigoBarras = getActivity().findViewById(R.id.txtCodigoBarras);
        txtRfid = getActivity().findViewById(R.id.txtRfid);
        btnAgregar = getActivity().findViewById(R.id.btnAgregar);
        btnLimpiar = getActivity().findViewById(R.id.btnLimpiar);
        btnCancelar = getActivity().findViewById(R.id.btnCancelar);
        btnGuardar = getActivity().findViewById(R.id.btnGuardar);
        barcodeListView.setEmptyView(barcodeEmptyView);
        readerListAdapter = new AsignacionBarrasListAdapter(getActivity(), R.layout.asignacion_barras_list_item, movimientoProductoDtoList, true);
        barcodeListView.setAdapter(readerListAdapter);

        barcodeRunTime = (TextView) getActivity().findViewById(R.id.inventoryBarcodeRunTime);
        barcodeVoltageLevel = (TextView) getActivity().findViewById(R.id.inventoryBarcodeVoltageLevel);

        barcodeYieldView = (TextView) getActivity().findViewById(R.id.inventoryBarcodeYield);
        button = (Button) getActivity().findViewById(R.id.inventoryBarcodeButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStopHandler(false);
            }
        });

        barcodeTotal = (TextView) getActivity().findViewById(R.id.inventoryBarcodeTotal);
        if (!MainActivity.mCs108Library4a.isBleConnected()) {
            button.setEnabled(false);
        }
        mViewModel = new ViewModelProvider((ViewModelStoreOwner) this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(AsignacionBarrasViewModel.class)) {
                    return (T) AsignacionBarrasViewModel.getInstance(getActivity().getApplication());
                } else {
                    throw new IllegalArgumentException("Unknown ViewModel class");
                }
            }
        }).get(AsignacionBarrasViewModel.class);
        readerDeviceMutableLiveData.observeForever(new Observer<ReaderDevice>() {
            @Override
            public void onChanged(@Nullable ReaderDevice readerDevice) {
                if (txtCodigoBarras.getText().toString().equals("")) {
                    txtCodigoBarras.setText(readerDevice.getAddress());

                } else if (txtRfid.getText().toString().equals("")) {
                    txtRfid.setText(readerDevice.getAddress());
                }
                btnAgregar.performClick();
            }
        });

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nuevaAsignacion && !txtCodigoBarras.getText().toString().equals("")) {
                    MovimientoProductoDto productoDto = new MovimientoProductoDto();
                    productoDto.Id = java.util.UUID.randomUUID().toString();
                    productoDto.CodBarra = txtCodigoBarras.getText().toString();
                    productoDto.UbicacionId = MainActivity.ubicacionDto.Id;
                    productoDto.UbicacionNombre = MainActivity.ubicacionDto.Nombre;
                    ProductoDto product = mViewModel.findProduct(productoDto.CodBarra);
                    if (product != null) {
                        productoDto.ProductoId = product.Id;
                        productoDto.ProductoNombre = product.Nombre;
                    }
                    productoDto.Enviado = true;
                    productoDto.Eliminado = false;
                    movimientoProductoDtoList.add(productoDto);
                    nuevaAsignacion = false;
                    txtRfid.requestFocus();
                } else if (!nuevaAsignacion && !txtRfid.getText().toString().equals("")) {
                    if (txtRfid.getText().toString().length() != 12) {
                        Toast.makeText(getActivity(), "Codigo RFID invalido", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    MovimientoProductoDto dto = mViewModel.searchRfid(txtRfid.getText().toString());
                    MovimientoProductoDto dto1 = null;
                    for (MovimientoProductoDto productoDto : movimientoProductoDtoList) {
                        if (productoDto.CodRfid != null && productoDto.CodRfid.equals(txtRfid.getText().toString())) {
                            dto1 = productoDto;
                            break;
                        }
                    }
                    if (dto != null || dto1 != null) {
                        Toast.makeText(getActivity(), "Codigo RFID Duplicado", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    movimientoProductoDtoList.get(movimientoProductoDtoList.size() - 1).CodRfid = txtRfid.getText().toString();
                    txtCodigoBarras.setText("");
                    txtRfid.setText("");
                    nuevaAsignacion = true;
                    txtCodigoBarras.requestFocus();
                    //mViewModel.insertMovimientoProducto(movimientoProductoDtoList.get(movimientoProductoDtoList.size() - 1));
                }
                readerListAdapter.notifyDataSetChanged();
                barcodeYieldView.setText("Total: " + movimientoProductoDtoList.size());
            }
        });

        readerListAdapter.eliminacionEvent.observeForever(new Observer<MovimientoProductoDto>() {
            @Override
            public void onChanged(@Nullable MovimientoProductoDto movimientoProductoDto) {
                if (movimientoProductoDtoList!= null){
                    barcodeYieldView.setText("Total: " + movimientoProductoDtoList.size());
                }

            }
        });
        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
                clearTagsList();
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (MovimientoProductoDto productoDto : movimientoProductoDtoList) {
                    while (productoDto.CodRfid.length() < 24) {
                        productoDto.CodRfid +="0";
                    }
                    mViewModel.insertMovimientoProducto(productoDto);
                }
                btnLimpiar.performClick();
                Toast.makeText(getActivity(), "Items guardados", Toast.LENGTH_SHORT).show();
            }
        });

        txtCodigoBarras.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    btnAgregar.performClick();
                    return true;
                }
                return false;
            }
        });

        txtRfid.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    btnAgregar.performClick();
                    return true;
                }
                return false;
            }
        });

        readerListAdapter.eliminacionEvent.observeForever(new Observer<MovimientoProductoDto>() {
            @Override
            public void onChanged(@Nullable MovimientoProductoDto movimientoProductoDto) {
                if (movimientoProductoDto != null) {
                    movimientoProductoDto.Enviado = true;
                    mViewModel.updateMovimientoProductoDto(movimientoProductoDto);
                }


            }
        });
    }

    private void clearFields() {
        nuevaAsignacion = true;
        txtCodigoBarras.setText("");
        txtRfid.setText("");
        txtCodigoBarras.requestFocus();
        if (movimientoProductoDtoList.size() > 0) {
            MovimientoProductoDto movimientoProductoDto = movimientoProductoDtoList.get(movimientoProductoDtoList.size() - 1);
            if (movimientoProductoDto != null && (movimientoProductoDto.CodRfid == null || movimientoProductoDto.CodRfid.equals(""))) {
                readerListAdapter.remove(movimientoProductoDtoList.get(movimientoProductoDtoList.size() - 1));
                barcodeYieldView.setText("Total: " + movimientoProductoDtoList.size());
                readerListAdapter.notifyDataSetChanged();
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (userVisibleHint) {
            MainActivity.mCs108Library4a.setAutoBarStartSTop(true);
            setNotificationListener();
        }
    }

    @Override
    public void onPause() {
        MainActivity.mCs108Library4a.setNotificationListener(null);
        if (inventoryBarcodeTask != null) {
            inventoryBarcodeTask.taskCancelReason = InventoryBarcodeTask.TaskCancelRReason.DESTORY;
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        MainActivity.mCs108Library4a.setAutoBarStartSTop(false);
        MainActivity.mCs108Library4a.setNotificationListener(null);
        super.onDestroy();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            userVisibleHint = true;
            MainActivity.mCs108Library4a.setAutoBarStartSTop(true);
            setNotificationListener();
        } else {
            userVisibleHint = false;
            MainActivity.mCs108Library4a.setAutoBarStartSTop(false);
            MainActivity.mCs108Library4a.setNotificationListener(null);
        }
    }

    public AsignacionBarrasFragment() {
        super("AsignacionBarrasFragment");
    }

    void setNotificationListener() {
        MainActivity.mCs108Library4a.setNotificationListener(new Cs108Connector.NotificationListener() {
            @Override
            public void onChange() {
                startStopHandler(true);
            }
        });
    }

    void startStopHandler(boolean buttonTrigger) {
        if (buttonTrigger)
            MainActivity.mCs108Library4a.appendToLog("BARTRIGGER: getTriggerButtonStatus = " + MainActivity.mCs108Library4a.getTriggerButtonStatus());
        if (MainActivity.sharedObjects.runningInventoryRfidTask) {
            Toast.makeText(MainActivity.mContext, "Running Barcode", Toast.LENGTH_SHORT).show();
            return;
        }
        boolean started = false;
        if (inventoryBarcodeTask != null)
            if (inventoryBarcodeTask.getStatus() == AsyncTask.Status.RUNNING) started = true;
        if (buttonTrigger && ((started && MainActivity.mCs108Library4a.getTriggerButtonStatus()) || (started == false && MainActivity.mCs108Library4a.getTriggerButtonStatus() == false))) {
            return;
        }
        if (started == false) {
            if (MainActivity.mCs108Library4a.isBleConnected() == false) {
                Toast.makeText(MainActivity.mContext, R.string.toast_ble_not_connected, Toast.LENGTH_SHORT).show();
                return;
            }
            if (MainActivity.mCs108Library4a.isBarcodeFailure()) {
                Toast.makeText(MainActivity.mContext, "Barcode is disabled", Toast.LENGTH_SHORT).show();
                return;
            }
            started = true;
            inventoryBarcodeTask = new InventoryBarcodeTask(MainActivity.sharedObjects.barsList, null, null, barcodeRunTime, barcodeVoltageLevel, barcodeYieldView, button, null, barcodeTotal, false, readerDeviceMutableLiveData);
            inventoryBarcodeTask.execute();
        } else {
            if (buttonTrigger)
                inventoryBarcodeTask.taskCancelReason = InventoryBarcodeTask.TaskCancelRReason.BUTTON_RELEASE;
            else
                inventoryBarcodeTask.taskCancelReason = InventoryBarcodeTask.TaskCancelRReason.STOP;
        }
    }

}