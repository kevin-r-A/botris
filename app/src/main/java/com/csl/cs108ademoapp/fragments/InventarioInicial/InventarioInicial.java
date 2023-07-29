package com.csl.cs108ademoapp.fragments.InventarioInicial;

import android.app.ProgressDialog;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.csl.cs108ademoapp.Generales.MetodosGenerales;
import com.csl.cs108ademoapp.R;
import com.csl.cs108ademoapp.Web.Responses.MercaderiaDto;
import com.csl.cs108ademoapp.adapters.InventarioInicialAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class InventarioInicial extends Fragment {

    private InventarioInicialViewModel mViewModel;
    private Button btn_descargardatos, btn_cancelar;
    private ImageButton btnSearch;
    private ListView inventoryRfidList1;
    private InventarioInicialAdapter inventarioInicialAdapter;
    private TextView lblCantidadTotal;
    private EditText txtSearch;

    public static InventarioInicial newInstance() {
        return new InventarioInicial();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inventario_inicial, container, false);
        btn_descargardatos = view.findViewById(R.id.btn_descargardatos);
        btn_cancelar = view.findViewById(R.id.btn_cancelar);
        btnSearch = view.findViewById(R.id.btnSearch);
        inventoryRfidList1 = view.findViewById(R.id.inventoryRfidList1);
        lblCantidadTotal = view.findViewById(R.id.lblCantidadTotal);
        txtSearch = view.findViewById(R.id.txtSearch);
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
                if (modelClass.isAssignableFrom(InventarioInicialViewModel.class)) {
                    return (T) InventarioInicialViewModel.getInstance(getActivity().getApplication());
                } else {
                    throw new IllegalArgumentException("Unknown ViewModel class");
                }
            }
        }).get(InventarioInicialViewModel.class);
        inventarioInicialAdapter = new InventarioInicialAdapter(getContext(), R.layout.card_inventario, new ArrayList<>());
        inventoryRfidList1.setAdapter(inventarioInicialAdapter);

        btn_descargardatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DialogDownload().execute();
            }
        });

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogLoad().execute();
            }
        });

        btn_cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogDelete().execute();
            }
        });

        new DialogLoad().execute();
    }

    private class DialogDownload extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pdia;
        private List<MercaderiaDto> mercaderiaDtos;

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                mercaderiaDtos = mViewModel.DownloadInventarioInicial();
            } catch (Exception ex) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
            pdia.setMessage("Descargando...");
            pdia.setCancelable(false);
            pdia.show();
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            pdia.dismiss();
            if (aVoid) {
                AtomicInteger cantidad = new AtomicInteger();
                cantidad.addAndGet(0);
                mercaderiaDtos.forEach(mercaderiaDto -> {
                    cantidad.addAndGet(mercaderiaDto.CANTIDAD);
                });
                lblCantidadTotal.setText(cantidad.get() + "");
                inventarioInicialAdapter.clear();
                inventarioInicialAdapter.addAll(mercaderiaDtos);
                inventarioInicialAdapter.notifyDataSetChanged();
                MetodosGenerales.Mensaje("Inventario Sistema guardado!", getActivity(), getResources());
            } else {
                MetodosGenerales.Mensaje("Inventario Sistema  no guardado!", getActivity(), getResources());
            }
        }
    }

    private class DialogLoad extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pdia;
        private List<MercaderiaDto> mercaderiaDtos;
        private String data;

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                if (data.equals("")) {
                    mercaderiaDtos = mViewModel.LoadInventarioInicial();
                } else {
                    mercaderiaDtos = mViewModel.LoadInventarioInicialFiltered(data);
                }


            } catch (Exception ex) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
            pdia.setMessage("Cargando...");
            pdia.setCancelable(false);
            pdia.show();

            data = txtSearch.getText().toString();
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            pdia.dismiss();
            if (aVoid) {
                AtomicInteger cantidad = new AtomicInteger();
                cantidad.addAndGet(0);
                mercaderiaDtos.forEach(mercaderiaDto -> {
                    cantidad.addAndGet(mercaderiaDto.CANTIDAD);
                });
                lblCantidadTotal.setText(cantidad.get() + "");
                inventarioInicialAdapter.clear();
                inventarioInicialAdapter.addAll(mercaderiaDtos);
                inventarioInicialAdapter.notifyDataSetChanged();
                MetodosGenerales.Mensaje("Cargado!", getActivity(), getResources());
            } else {
                MetodosGenerales.Mensaje("Imposible cargar inventario!", getActivity(), getResources());
            }
        }
    }

    private class DialogDelete extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pdia;

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                mViewModel.DeleteInventarioInicial();
            } catch (Exception ex) {
                return false;
            }

            return true;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
            pdia.setMessage("Eliminando...");
            pdia.setCancelable(false);
            pdia.show();

        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            pdia.dismiss();
            if (aVoid) {

                lblCantidadTotal.setText("0");
                inventarioInicialAdapter.clear();
                inventarioInicialAdapter.notifyDataSetChanged();
                MetodosGenerales.Mensaje("Eliminado!", getActivity(), getResources());
            } else {
                MetodosGenerales.Mensaje("Imposible eliminar inventario!", getActivity(), getResources());
            }
        }
    }

}