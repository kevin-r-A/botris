package com.csl.cs108ademoapp.fragments.Despachos;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelStoreOwner;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.csl.cs108ademoapp.Dialogo;
import com.csl.cs108ademoapp.DrawerListContent;
import com.csl.cs108ademoapp.Entities.POJO.OrdenDespachoRecepcionDetalleWithNavigation;
import com.csl.cs108ademoapp.Generales.LecturaCslRFID;
import com.csl.cs108ademoapp.Generales.MetodosGenerales;
import com.csl.cs108ademoapp.MainActivity;
import com.csl.cs108ademoapp.R;
import com.csl.cs108ademoapp.Web.Responses.InventarioDto;
import com.csl.cs108ademoapp.Web.Responses.MovimientoProductoDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionDetalleDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionRfidDto;
import com.csl.cs108ademoapp.adapters.DespachoAdapter;
import com.csl.cs108ademoapp.fragments.Asignacion.AsignacionFragment;
import com.csl.cs108ademoapp.fragments.AsignacionBarras.AsignacionBarrasViewModel;
import com.csl.cs108ademoapp.fragments.DatePickerFragment;
import com.csl.cs108library4a.ReaderDevice;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DespachoFragment extends Fragment {

    private DespachoViewModel mViewModel;
    private ArrayAdapter<OrdenDespachoRecepcionDto> ordenDespachoRecepcionDtoArrayAdapter;
    private AutoCompleteTextView cmbOrdenDespacho;
    private OrdenDespachoRecepcionDto ordenDespachoRecepcionDto;
    private DespachoAdapter despachoAdapter;
    private ListView inventoryRfidList1;
    private Button inventoryRfidButton1, btn_guardar;
    private List<String> codigosRfidAProcesar = new ArrayList<>();
    private ImageButton btnAddDespacho;
    private EditText txtFecha;
    private ImageButton cmdFecha;
    private TextView lblCantidadTotal;

    public static DespachoFragment newInstance() {
        return new DespachoFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_despacho, container, false);
        cmbOrdenDespacho = view.findViewById(R.id.cmbOrdenDespacho);
        inventoryRfidList1 = view.findViewById(R.id.inventoryRfidList1);
        inventoryRfidButton1 = view.findViewById(R.id.inventoryRfidButton1);
        btn_guardar = view.findViewById(R.id.btn_guardar);
        btnAddDespacho = view.findViewById(R.id.btnAddDespacho);
        txtFecha = view.findViewById(R.id.txtFecha);
        cmdFecha = view.findViewById(R.id.cmdFecha);
        lblCantidadTotal = view.findViewById(R.id.lblCantidadTotal);
        return view;
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        configureReader();

        mViewModel = mViewModel = new ViewModelProvider((ViewModelStoreOwner) this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(DespachoViewModel.class)) {
                    return (T) DespachoViewModel.getInstance(getActivity().getApplication());
                } else {
                    throw new IllegalArgumentException("Unknown ViewModel class");
                }
            }
        }).get(DespachoViewModel.class);

        ordenDespachoRecepcionDtoArrayAdapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, new ArrayList<OrdenDespachoRecepcionDto>());
        cmbOrdenDespacho.setAdapter(ordenDespachoRecepcionDtoArrayAdapter);

        despachoAdapter = new DespachoAdapter(getActivity(), R.layout.despacho_item_list, new ArrayList<OrdenDespachoRecepcionDetalleWithNavigation>());
        inventoryRfidList1.setAdapter(despachoAdapter);

        mViewModel.getDetalleMutableLiveData().observeForever(new Observer<List<OrdenDespachoRecepcionDetalleWithNavigation>>() {
            @Override
            public void onChanged(@Nullable List<OrdenDespachoRecepcionDetalleWithNavigation> ordenDespachoRecepcionDetalleDtos) {
                //ordenDespachoRecepcionDto.OrdenDespachoRecepcionDetalles = ordenDespachoRecepcionDetalleDtos;
                despachoAdapter.clear();
                despachoAdapter.addAll(ordenDespachoRecepcionDetalleDtos);
                despachoAdapter.notifyDataSetChanged();
            }
        });

        cmbOrdenDespacho.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ordenDespachoRecepcionDto = ordenDespachoRecepcionDtoArrayAdapter.getItem(position);
                mViewModel.GetDetalle(ordenDespachoRecepcionDto.Id);
            }
        });

        inventoryRfidButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LecturaCslRFID.startStopHandler(false);
            }
        });

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogoConfirmacion();
            }
        });

        mViewModel.getCodigoValido().observeForever(new Observer<MovimientoProductoDto>() {
            @Override
            public void onChanged(@Nullable MovimientoProductoDto movimientoProductoDto) {
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
                    for (OrdenDespachoRecepcionDetalleWithNavigation x : objects) {
                        cantidad += x.ordenDespachoRecepcionRfidDtos.size();
                    }
                    lblCantidadTotal.setText(cantidad + "");
                }

            }
        });

        mViewModel.getCodigoNoValido().observeForever(new Observer<MovimientoProductoDto>() {
            @Override
            public void onChanged(@Nullable MovimientoProductoDto movimientoProductoDto) {
                if (movimientoProductoDto != null) {
                    Toast.makeText(getActivity(), movimientoProductoDto.CodRfid, Toast.LENGTH_SHORT).show();
                }
            }
        });

        mViewModel.getCodigoDespachado().observeForever(movimientoProductoDto -> {
            if (movimientoProductoDto != null) {
                Toast.makeText(getActivity(), "Codigo ya despachado: " + movimientoProductoDto.CodRfid, Toast.LENGTH_SHORT).show();
            }
        });

        btnAddDespacho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).selectItem(DrawerListContent.DrawerPositions.NEWDESPACHO);
            }
        });

        cmdFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        final String selectedDate = day + " / " + (month + 1) + " / " + year;
                        String date1 = year + "-" + (month + 1) + "-" + day;
                        DateFormat df = new SimpleDateFormat(MetodosGenerales.DOB_FORMAT);
                        try {
                            Date fecha2 = df.parse(date1);
                            mViewModel.GetDespachoByUbicacionLiveData(MainActivity.ubicacionDto.Id, fecha2).observeForever(new Observer<List<OrdenDespachoRecepcionDto>>() {
                                @Override
                                public void onChanged(@Nullable List<OrdenDespachoRecepcionDto> ordenDespachoRecepcionDtos) {
                                    ordenDespachoRecepcionDtoArrayAdapter.clear();
                                    ordenDespachoRecepcionDtoArrayAdapter.addAll(ordenDespachoRecepcionDtos);
                                    ordenDespachoRecepcionDtoArrayAdapter.notifyDataSetChanged();
                                }
                            });
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        txtFecha.setText(selectedDate);
                    }
                });

                newFragment.show(getActivity().getFragmentManager(), "datePicker");
            }
        });
        new ProcessData_ASYC().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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

    private class DialogGuardar extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pdia;

        @Override
        protected Boolean doInBackground(Void... voids) {

            List<OrdenDespachoRecepcionDetalleWithNavigation> objects = despachoAdapter.getObjects();
            List<OrdenDespachoRecepcionRfidDto> items = objects.stream().map(x -> x.ordenDespachoRecepcionRfidDtos).flatMap(List::stream).collect(Collectors.toList());
            ordenDespachoRecepcionDto.OrdenDespachoRecepcionRfids = items;

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
                ordenDespachoRecepcionDto = null;
                cmbOrdenDespacho.setText("");
                despachoAdapter.clear();
                despachoAdapter.notifyDataSetChanged();
                MetodosGenerales.Mensaje("Items despachados!", getActivity(), getResources());
            } else {
                MetodosGenerales.Mensaje("Items no despachados!", getActivity(), getResources());
            }
        }
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
}

