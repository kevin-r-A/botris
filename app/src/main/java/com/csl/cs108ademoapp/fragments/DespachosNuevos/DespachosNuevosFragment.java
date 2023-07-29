package com.csl.cs108ademoapp.fragments.DespachosNuevos;

import android.app.Application;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.csl.cs108ademoapp.Dialogo;
import com.csl.cs108ademoapp.Entities.POJO.OrdenDespachoRecepcionDetalleWithNavigation;
import com.csl.cs108ademoapp.Generales.LecturaCslRFID;
import com.csl.cs108ademoapp.Generales.MetodosGenerales;
import com.csl.cs108ademoapp.Generales.Sucursal;
import com.csl.cs108ademoapp.MainActivity;
import com.csl.cs108ademoapp.R;
import com.csl.cs108ademoapp.Web.Responses.MovimientoProductoDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionDetalleDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionRfidDto;
import com.csl.cs108ademoapp.Web.Responses.UbicacionDto;
import com.csl.cs108ademoapp.adapters.DespachoAdapter;
import com.csl.cs108ademoapp.fragments.Despachos.DespachoFragment;
import com.csl.cs108ademoapp.fragments.Despachos.DespachoViewModel;
import com.csl.cs108library4a.ReaderDevice;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

public class DespachosNuevosFragment extends Fragment {

    private DespachosNuevosViewModel mViewModel;
    private Spinner cmbSucursal, cmbBodega;
    private ListView inventoryRfidList1;
    private Button inventoryRfidButton1, btn_guardar;
    private List<String> codigosRfidAProcesar = new ArrayList<>();
    private DespachoAdapter despachoAdapter;
    private OrdenDespachoRecepcionDto ordenDespachoRecepcionDto;
    private UbicacionDto ubicacionDto= null;
    private TextView lblCantidadTotal;

    public static DespachosNuevosFragment newInstance() {
        return new DespachosNuevosFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_despachos_nuevos, container, false);
        cmbSucursal = view.findViewById(R.id.cmbSucursal);
        cmbBodega = view.findViewById(R.id.cmbBodega);
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
                if (modelClass.isAssignableFrom(DespachosNuevosViewModel.class)) {
                    return (T) DespachosNuevosViewModel.getInstance(getActivity().getApplication());
                } else {
                    throw new IllegalArgumentException("Unknown ViewModel class");
                }
            }
        }).get(DespachosNuevosViewModel.class);

        ArrayAdapter<Sucursal> sucursalArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.support_simple_spinner_dropdown_item, new ArrayList<Sucursal>());
        cmbSucursal.setAdapter(sucursalArrayAdapter);

        ArrayAdapter<UbicacionDto> ubicacionDtoArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, new ArrayList<UbicacionDto>());
        cmbBodega.setAdapter(ubicacionDtoArrayAdapter);

        despachoAdapter = new DespachoAdapter(getActivity(), R.layout.despacho_item_list, new ArrayList<OrdenDespachoRecepcionDetalleWithNavigation>());
        inventoryRfidList1.setAdapter(despachoAdapter);

        ordenDespachoRecepcionDto = new OrdenDespachoRecepcionDto();
        ordenDespachoRecepcionDto.Id = java.util.UUID.randomUUID().toString();

        inventoryRfidButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LecturaCslRFID.startStopHandler(false);
            }
        });

        mViewModel.getSucursales().observeForever(new Observer<List<Sucursal>>() {
            @Override
            public void onChanged(@Nullable List<Sucursal> sucursals) {
                sucursalArrayAdapter.clear();
                sucursalArrayAdapter.addAll(sucursals);
                sucursalArrayAdapter.notifyDataSetChanged();
            }
        });

        cmbSucursal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Sucursal item = sucursalArrayAdapter.getItem(position);
                ubicacionDtoArrayAdapter.clear();
                ubicacionDtoArrayAdapter.addAll(mViewModel.getBodegas(item.CodigoSucursal));
                ubicacionDtoArrayAdapter.notifyDataSetChanged();
                ubicacionDto = ubicacionDtoArrayAdapter.getItem(0);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cmbBodega.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ubicacionDto = ubicacionDtoArrayAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mViewModel.getCodigoValido().observeForever(new Observer<MovimientoProductoDto>() {
            @Override
            public void onChanged(@Nullable MovimientoProductoDto movimientoProductoDto) {
                if (movimientoProductoDto != null) {
                    int cantidad = 0;
                    List<OrdenDespachoRecepcionDetalleWithNavigation> objects = despachoAdapter.getObjects();
                    OrdenDespachoRecepcionDetalleWithNavigation detalle = objects.stream().filter(x -> x.ordenDespachoRecepcionDetalleDto.CodBarras.equals(movimientoProductoDto.CodBarra)).findFirst().orElse(null);
                    if (detalle != null) {
                        if (detalle.ordenDespachoRecepcionRfidDtos.stream().noneMatch(y -> y.CodRfid.equals(movimientoProductoDto.CodRfid))) {
                            OrdenDespachoRecepcionRfidDto recepcionDetalleDto = new OrdenDespachoRecepcionRfidDto();
                            recepcionDetalleDto.Id = java.util.UUID.randomUUID().toString();
                            recepcionDetalleDto.FechaDespacho = Calendar.getInstance().getTime();
                            recepcionDetalleDto.CodBarras = movimientoProductoDto.CodBarra;
                            recepcionDetalleDto.CodRfid = movimientoProductoDto.CodRfid;
                            recepcionDetalleDto.OrdenDespachoRecepcionId = ordenDespachoRecepcionDto.Id;
                            recepcionDetalleDto.Despacho = true;
                            recepcionDetalleDto.Enviar = true;
                            detalle.ordenDespachoRecepcionRfidDtos.add(recepcionDetalleDto);
                            despachoAdapter.notifyDataSetChanged();
                        }
                    } else {
                        detalle = new OrdenDespachoRecepcionDetalleWithNavigation();
                        detalle.ordenDespachoRecepcionDetalleDto = new OrdenDespachoRecepcionDetalleDto();
                        detalle.ordenDespachoRecepcionDetalleDto.Id = java.util.UUID.randomUUID().toString();
                        detalle.ordenDespachoRecepcionDetalleDto.ProductoId = movimientoProductoDto.ProductoId;
                        detalle.ordenDespachoRecepcionDetalleDto.CodBarras = movimientoProductoDto.CodBarra;
                        detalle.ordenDespachoRecepcionDetalleDto.OrdenDespachoRecepcionId = ordenDespachoRecepcionDto.Id;
                        detalle.producto = movimientoProductoDto.ProductoNombre;
                        OrdenDespachoRecepcionRfidDto recepcionDetalleDto = new OrdenDespachoRecepcionRfidDto();
                        recepcionDetalleDto.Id = java.util.UUID.randomUUID().toString();
                        recepcionDetalleDto.FechaDespacho = Calendar.getInstance().getTime();
                        recepcionDetalleDto.CodBarras = movimientoProductoDto.CodBarra;
                        recepcionDetalleDto.CodRfid = movimientoProductoDto.CodRfid;
                        recepcionDetalleDto.OrdenDespachoRecepcionId = ordenDespachoRecepcionDto.Id;
                        recepcionDetalleDto.Despacho = true;
                        recepcionDetalleDto.Enviar = true;
                        detalle.ordenDespachoRecepcionRfidDtos.add(recepcionDetalleDto);
                        despachoAdapter.add(detalle);
                        despachoAdapter.notifyDataSetChanged();
                    }
                    for (OrdenDespachoRecepcionDetalleWithNavigation x : objects) {
                        cantidad += x.ordenDespachoRecepcionRfidDtos.size();
                    }
                    lblCantidadTotal.setText(cantidad + "");
                }

            }
        });

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (despachoAdapter.getObjects().size() > 0) {
                    DialogoConfirmacion();
                }

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
        despachoAdapter.clear();
        despachoAdapter.notifyDataSetChanged();
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
        protected void onPreExecute() {
            MainActivity.mCs108Library4a.appendToLogView("");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while (true) {
                try {
                    Thread.sleep(40);
                    if (codigosRfidAProcesar.size() > 0) {
                        String codigoRfid = codigosRfidAProcesar.get(0);
                        mViewModel.BuscarCodigo(codigoRfid);
                        codigosRfidAProcesar.remove(codigoRfid);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    private class DialogGuardar extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pdia;

        @Override
        protected Boolean doInBackground(Void... voids) {

            List<OrdenDespachoRecepcionDetalleWithNavigation> objects = despachoAdapter.getObjects();
            objects.forEach(x -> {
                x.ordenDespachoRecepcionDetalleDto.Cantidad = x.ordenDespachoRecepcionRfidDtos.size();
                x.ordenDespachoRecepcionDetalleDto.CantidadDespachada = x.ordenDespachoRecepcionDetalleDto.Cantidad;
            });
            List<OrdenDespachoRecepcionRfidDto> items = objects.stream().map(x -> x.ordenDespachoRecepcionRfidDtos).flatMap(List::stream).collect(Collectors.toList());
            List<OrdenDespachoRecepcionDetalleDto> itemsDetalle = objects.stream().map(x -> x.ordenDespachoRecepcionDetalleDto).collect(Collectors.toList());
            ordenDespachoRecepcionDto.OrdenDespachoRecepcionRfids = items;
            ordenDespachoRecepcionDto.OrdenDespachoRecepcionDetalles = itemsDetalle;
            ordenDespachoRecepcionDto.UbicacionId = MainActivity.ubicacionDto.Id;
            ordenDespachoRecepcionDto.UbicacionDestinoId = ubicacionDto.Id;
            ordenDespachoRecepcionDto.Estado = 0;
            ordenDespachoRecepcionDto.Fecha = Calendar.getInstance().getTime();
            return mViewModel.SaveDespacho(ordenDespachoRecepcionDto);
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
            if (aVoid) {
                LecturaCslRFID.clearTagsList();
                ordenDespachoRecepcionDto = new OrdenDespachoRecepcionDto();
                ordenDespachoRecepcionDto.Id = java.util.UUID.randomUUID().toString();
                despachoAdapter.clear();
                despachoAdapter.notifyDataSetChanged();
                lblCantidadTotal.setText("0");
                MetodosGenerales.Mensaje("Items despachados!", getActivity(), getResources());
            } else {
                MetodosGenerales.Mensaje("Items no despachados!", getActivity(), getResources());
            }
        }
    }

}