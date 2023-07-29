package com.csl.cs108ademoapp.fragments.Inventario;

import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelStoreOwner;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.csl.cs108ademoapp.Dialogo;
import com.csl.cs108ademoapp.Entities.Clases.STOCK;
import com.csl.cs108ademoapp.Entities.POJO.OrdenDespachoRecepcionDetalleWithNavigation;
import com.csl.cs108ademoapp.Generales.LecturaCslRFID;
import com.csl.cs108ademoapp.Generales.MetodosGenerales;
import com.csl.cs108ademoapp.MainActivity;
import com.csl.cs108ademoapp.R;
import com.csl.cs108ademoapp.Web.Responses.InventarioDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionRfidDto;
import com.csl.cs108ademoapp.adapters.InventarioAdapter;
import com.csl.cs108ademoapp.fragments.Despachos.DespachoFragment;
import com.csl.cs108ademoapp.fragments.Despachos.DespachoViewModel;
import com.csl.cs108ademoapp.fragments.Recepciones.RecepcionFragment;
import com.csl.cs108library4a.ReaderDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InventarioFragment extends Fragment {

    private InventarioViewModel mViewModel;
    private List<String> codigosRfidAProcesar = new ArrayList<>();
    private List<String> codigosRfidAProcesados = new ArrayList<>();
    private Button inventoryRfidButton1, btn_guardar, btn_cancelar;
    private ListView inventoryRfidList1;
    private InventarioAdapter inventarioAdapter;
    private TextView lblCantidadTotal;
    private ArrayAdapter<STOCK> stockArrayAdapter;
    private List<STOCK> stockList = new ArrayList<>();

    public static InventarioFragment newInstance() {
        return new InventarioFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventario, container, false);
        inventoryRfidList1 = view.findViewById(R.id.inventoryRfidList1);
        inventoryRfidButton1 = view.findViewById(R.id.inventoryRfidButton1);
        btn_guardar = view.findViewById(R.id.btn_guardar);
        btn_cancelar = view.findViewById(R.id.btn_cancelar);
        lblCantidadTotal = view.findViewById(R.id.lblCantidadTotal);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        configureReader();
        mViewModel = new ViewModelProvider((ViewModelStoreOwner) this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(InventarioViewModel.class)) {
                    return (T) InventarioViewModel.getInstance(getActivity().getApplication());
                } else {
                    throw new IllegalArgumentException("Unknown ViewModel class");
                }
            }
        }).get(InventarioViewModel.class);

        stockArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.simple_list_item,R.id.txtRfid, new ArrayList<>());
        inventoryRfidList1.setAdapter(stockArrayAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    stockList =  mViewModel.GetStock();
                    lblCantidadTotal.setText(stockList.size() + "");
                    stockArrayAdapter.addAll(stockList);
                    stockArrayAdapter.notifyDataSetChanged();
                } catch (Exception e){

                }

            }
        }).start();

        inventoryRfidButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //codigosRfidAProcesar.add("000000054506000000000000");
                LecturaCslRFID.startStopHandler(false);
            }
        });

        mViewModel.getInventarioDtoMutableLiveData().observeForever(inventarioDto -> {
            int cantidad = 0;
            if (inventarioDto != null) {
                InventarioDto inventarioDto1 = inventarioAdapter.getObjects().stream().filter(x -> x.ProductoId.equals(inventarioDto.ProductoId)).findFirst().orElse(null);
                if (inventarioDto1 == null) {
                    inventarioAdapter.add(inventarioDto);
                } else {
                    inventarioDto1.InventarioDetalleRfids.addAll(inventarioDto.InventarioDetalleRfids);
                }
                inventarioAdapter.notifyDataSetChanged();
                for (InventarioDto x : inventarioAdapter.getObjects()) {
                    cantidad += x.InventarioDetalleRfids.size();
                }
                lblCantidadTotal.setText(cantidad + "");
            }
        });

        mViewModel.getStockMutableLiveData().observeForever( stock -> {
            if (stock != null){
                stockList.add(stock);
                stockArrayAdapter.add(stock);
                stockArrayAdapter.notifyDataSetChanged();
                lblCantidadTotal.setText(stockArrayAdapter.getCount() + "");
            }

        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LecturaCslRFID.clearTagsList();
                stockList = new ArrayList<>();
                stockArrayAdapter.clear();
                stockArrayAdapter.notifyDataSetChanged();
                lblCantidadTotal.setText("0");
            }
        });

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogoConfirmacion();
            }
        });

        new ProcessData_ASYC().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    private void configureReader() {
        LecturaCslRFID.setButton(inventoryRfidButton1);
        LecturaCslRFID.setRfidListView(inventoryRfidList1);
        LecturaCslRFID.getReaderDeviceMutableLiveData().observe(this, new Observer<ReaderDevice>() {
            @Override
            public void onChanged(@Nullable ReaderDevice readerDevice) {
                if (readerDevice != null) {
                    codigosRfidAProcesar.add(readerDevice.getAddress());
                    // mViewModel.BuscarCodigo(readerDevice.getAddress());
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        //LecturaCslRFID.destroy();
        //inventarioAdapter.clear();
        //inventarioAdapter.notifyDataSetChanged();
        super.onDestroy();
    }

    private class ProcessData_ASYC extends AsyncTask<Void, Void, Void> {
        String codigoRfid;
        @Override
        protected void onPreExecute() {
            MainActivity.mCs108Library4a.appendToLogView("");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            MainActivity.mCs108Library4a.appendToLogView("Value RFID: " + codigoRfid);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (true) {
                try {
                    Thread.sleep(40);
                    if (codigosRfidAProcesar.size() > 0) {
                        codigoRfid = codigosRfidAProcesar.get(0);
                        codigosRfidAProcesados.add(codigoRfid);
                        mViewModel.ProcesarCodigo(codigoRfid);
                        //mViewModel.BuscarCodigo(codigoRfid, true);
                        codigosRfidAProcesar.remove(codigoRfid);
                        //publishProgress();
                    }
                } catch (InterruptedException e) {
                    //codigoRfid = e.getMessage();
                    //publishProgress();
                    e.printStackTrace();
                } catch (Exception exception) {
                    //codigoRfid = exception.getMessage();
                    //publishProgress();
                    exception.printStackTrace();
                }
            }
        }
    }

    private class DialogGuardar extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pdia;

        @Override
        protected Boolean doInBackground(Void... voids) {

            /*List<InventarioDto> objects = inventarioAdapter.getObjects();
            for (InventarioDto inventarioDto : objects) {
                inventarioDto.CantidadFisica = inventarioDto.InventarioDetalleRfids.size();
            }
            return mViewModel.SaveInventario(objects);*/

            return mViewModel.SaveStock(stockList);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
            pdia.setMessage("Guardando...");
            pdia.setCancelable(false);
            pdia.show();
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            pdia.dismiss();
            if (aVoid) {
                //LecturaCslRFID.clearTagsList();
                //LecturaCslRFID.clearTagsList();
                //inventarioAdapter.clear();
                //inventarioAdapter.notifyDataSetChanged();
                MetodosGenerales.Mensaje("Inventario guardado!", getActivity(), getResources());
            } else {
                MetodosGenerales.Mensaje("Inventario no guardado!", getActivity(), getResources());
            }
        }
    }
}