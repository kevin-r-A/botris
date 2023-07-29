package com.csl.cs108ademoapp.fragments.Recepciones;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.csl.cs108ademoapp.Dialogo;
import com.csl.cs108ademoapp.Entities.POJO.OrdenDespachoRecepcionDetalleWithNavigation;
import com.csl.cs108ademoapp.Generales.LecturaCslRFID;
import com.csl.cs108ademoapp.Generales.MetodosGenerales;
import com.csl.cs108ademoapp.MainActivity;
import com.csl.cs108ademoapp.R;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionRfidDto;
import com.csl.cs108ademoapp.adapters.DespachoAdapter;
import com.csl.cs108ademoapp.adapters.RecepcionAdapter;
import com.csl.cs108ademoapp.fragments.Despachos.DespachoFragment;
import com.csl.cs108ademoapp.fragments.Despachos.DespachoViewModel;
import com.csl.cs108library4a.ReaderDevice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class RecepcionFragment extends Fragment {

    private RecepcionViewModel mViewModel;
    private RecepcionAdapter recepcionAdapter;
    private AutoCompleteTextView cmbOrdenRecepcion;
    private ListView inventoryRfidList1;
    private Button inventoryRfidButton1, btn_guardar;
    private ArrayAdapter<OrdenDespachoRecepcionDto> ordenDespachoRecepcionDtoArrayAdapter;
    private OrdenDespachoRecepcionDto ordenDespachoRecepcionDto;
    private List<String> codigosRfidAProcesar = new ArrayList<>();
    private TextView lblCantidadTotal;

    public static RecepcionFragment newInstance() {
        return new RecepcionFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recepcion, container, false);
        cmbOrdenRecepcion = view.findViewById(R.id.cmbOrdenRecepcion);
        inventoryRfidList1 = view.findViewById(R.id.inventoryRfidList1);
        inventoryRfidButton1 = view.findViewById(R.id.inventoryRfidButton1);
        btn_guardar = view.findViewById(R.id.btn_guardar);
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
                if (modelClass.isAssignableFrom(RecepcionViewModel.class)) {
                    return (T) RecepcionViewModel.getInstance(getActivity().getApplication());
                } else {
                    throw new IllegalArgumentException("Unknown ViewModel class");
                }
            }
        }).get(RecepcionViewModel.class);

        ordenDespachoRecepcionDtoArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, new ArrayList<OrdenDespachoRecepcionDto>());
        cmbOrdenRecepcion.setAdapter(ordenDespachoRecepcionDtoArrayAdapter);

        recepcionAdapter = new RecepcionAdapter(getActivity(), R.layout.despacho_item_list, new ArrayList<OrdenDespachoRecepcionDetalleWithNavigation>());
        inventoryRfidList1.setAdapter(recepcionAdapter);

        mViewModel.GetRecepcionByUbicacionLiveData(MainActivity.ubicacionDto.Id).observeForever(new Observer<List<OrdenDespachoRecepcionDto>>() {
            @Override
            public void onChanged(@Nullable List<OrdenDespachoRecepcionDto> ordenDespachoRecepcionDtos) {
                ordenDespachoRecepcionDtoArrayAdapter.clear();
                ordenDespachoRecepcionDtoArrayAdapter.addAll(ordenDespachoRecepcionDtos);
                ordenDespachoRecepcionDtoArrayAdapter.notifyDataSetChanged();
            }
        });

        inventoryRfidButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LecturaCslRFID.startStopHandler(false);
            }
        });

        mViewModel.getDetalleMutableLiveData().observeForever(new Observer<List<OrdenDespachoRecepcionDetalleWithNavigation>>() {
            @Override
            public void onChanged(@Nullable List<OrdenDespachoRecepcionDetalleWithNavigation> ordenDespachoRecepcionDetalleDtos) {
                recepcionAdapter.clear();
                recepcionAdapter.addAll(ordenDespachoRecepcionDetalleDtos);
                recepcionAdapter.notifyDataSetChanged();
            }
        });

        cmbOrdenRecepcion.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ordenDespachoRecepcionDto = ordenDespachoRecepcionDtoArrayAdapter.getItem(position);
                mViewModel.GetDetalle(ordenDespachoRecepcionDto.Id);
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

    private void configureReader() {
        LecturaCslRFID.setButton(inventoryRfidButton1);
        LecturaCslRFID.setRfidListView(inventoryRfidList1);
        LecturaCslRFID.getReaderDeviceMutableLiveData().observe(this, new Observer<ReaderDevice>() {
            @Override
            public void onChanged(@Nullable ReaderDevice readerDevice) {
                if (readerDevice != null) {
                    codigosRfidAProcesar.add(readerDevice.getAddress());
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        LecturaCslRFID.destroy();
        super.onDestroy();
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

    private class ProcessData_ASYC extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            while (true) {
                try {
                    Thread.sleep(15);
                    if (codigosRfidAProcesar.size() > 0) {
                        String codigoRfid = codigosRfidAProcesar.get(0);
                        OrdenDespachoRecepcionDetalleWithNavigation selected = null;
                        recepcionAdapter.getObjects().forEach(ordenDespachoRecepcionDetalleWithNavigation -> {
                            OrdenDespachoRecepcionRfidDto item = ordenDespachoRecepcionDetalleWithNavigation.ordenDespachoRecepcionRfidDtos.stream().filter(x -> x.CodRfid.equals(codigoRfid)).findFirst().orElse(null);
                            if (item != null) {
                                item.Recepcion = true;
                                item.FechaRecepcion = Calendar.getInstance().getTime();
                                item.Enviar = true;
                            }
                        });
                        publishProgress();
                        codigosRfidAProcesar.remove(codigoRfid);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            recepcionAdapter.notifyDataSetChanged();
            int cantidad=0;
            for (OrdenDespachoRecepcionDetalleWithNavigation x : recepcionAdapter.getObjects()) {
                cantidad += x.ordenDespachoRecepcionRfidDtos.stream().filter(a->a.Recepcion).collect(Collectors.toList()).size();
            }
            lblCantidadTotal.setText(cantidad + "");
            super.onProgressUpdate(values);
        }
    }

    private class DialogGuardar extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pdia;

        @Override
        protected Boolean doInBackground(Void... voids) {

            List<OrdenDespachoRecepcionDetalleWithNavigation> objects = recepcionAdapter.getObjects();
            List<OrdenDespachoRecepcionRfidDto> items = objects.stream().map(x -> x.ordenDespachoRecepcionRfidDtos).flatMap(List::stream).collect(Collectors.toList());
            ordenDespachoRecepcionDto.OrdenDespachoRecepcionRfids = items;

            return mViewModel.SaveDespacho(ordenDespachoRecepcionDto);
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
            //super.onPostExecute(aVoid);
            pdia.dismiss();
            if (aVoid) {
                LecturaCslRFID.clearTagsList();
                ordenDespachoRecepcionDto = null;
                cmbOrdenRecepcion.setText("");
                recepcionAdapter.clear();
                recepcionAdapter.notifyDataSetChanged();
                MetodosGenerales.Mensaje("Items recibidos!", getActivity(), getResources());
            } else {
                MetodosGenerales.Mensaje("Items no recibidos!", getActivity(), getResources());
            }
        }
    }

}