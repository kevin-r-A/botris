package com.csl.cs108ademoapp.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelStoreOwner;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.csl.cs108ademoapp.BuildConfig;
import com.csl.cs108ademoapp.Entities.Controllers.ReporteController;
import com.csl.cs108ademoapp.Generales.MetodosGenerales;
import com.csl.cs108ademoapp.Interface.InventarioReporte;
import com.csl.cs108ademoapp.MainActivity;
import com.csl.cs108ademoapp.Models.ListaInv;
import com.csl.cs108ademoapp.Models.Sobrantes;
import com.csl.cs108ademoapp.R;
import com.csl.cs108ademoapp.Web.Responses.InventarioDto;
import com.csl.cs108ademoapp.adapters.AdapterConciliados;
import com.csl.cs108ademoapp.adapters.AdapterFaltantes;
import com.csl.cs108ademoapp.adapters.AdapterSobrantes;
import com.csl.cs108ademoapp.adapters.InventarioAdapter;
import com.csl.cs108ademoapp.fragments.Despachos.DespachoViewModel;

import java.util.ArrayList;
import java.util.List;

public class Reporte extends CommonFragment {
    public TextView contarc, contarf, contars, txt_contar_faltantes_sistema;

    private ListView recyclerC, recyclerF, recyclerS;
    private InventarioAdapter adapterc;
    private InventarioAdapter adapterf;
    private InventarioAdapter adapters;
    private LinearLayoutManager lManagerC;
    private RecyclerView.LayoutManager lManagerF;
    private RecyclerView.LayoutManager lManagerS;
    private ReporteViewModel mViewModel;
    private Button btn_guardar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState, true);
        View view = inflater.inflate(R.layout.activity_reporte, container, false);
        btn_guardar = view.findViewById(R.id.btn_guardar);
        txt_contar_faltantes_sistema = view.findViewById(R.id.txt_contar_faltantes_sistema);
        return view;
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Reporte");

        MainActivity.mSensorConnector.mLocationDevice.turnOn(true);
        MainActivity.mSensorConnector.mSensorDevice.turnOn(true);
        //mHandler.post(updateRunnable);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mViewModel = mViewModel = new ViewModelProvider((ViewModelStoreOwner) this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            @SuppressWarnings("unchecked")
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                if (modelClass.isAssignableFrom(ReporteViewModel.class)) {
                    return (T) ReporteViewModel.getInstance(getActivity().getApplication());
                } else {
                    throw new IllegalArgumentException("Unknown ViewModel class");
                }
            }
        }).get(ReporteViewModel.class);

        //Conciliados
        contarc = (TextView) getActivity().findViewById(R.id.txt_contar_conciliadoss);
        recyclerC = getActivity().findViewById(R.id.ListaConciliados);
        //recyclerC.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManagerC = new LinearLayoutManager(getActivity());
        //recyclerC.setLayoutManager(lManagerC);

        //Faltantes
        contarf = (TextView) getActivity().findViewById(R.id.txt_contar_faltantes);
        recyclerF = getActivity().findViewById(R.id.ListaFaltantes);
        //recyclerF.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManagerF = new LinearLayoutManager(getActivity());
        //recyclerF.setLayoutManager(lManagerF);

        //Sobrantes
        contars = (TextView) getActivity().findViewById(R.id.txt_contar_sobrantes);
        recyclerS = getActivity().findViewById(R.id.ListaSobrantes);
        //recyclerS.setHasFixedSize(true);

        // Usar un administrador para LinearLayout
        lManagerS = new LinearLayoutManager(getActivity());
        //recyclerS.setLayoutManager(lManagerS);

        TabHost tabs = (TabHost) getActivity().findViewById(android.R.id.tabhost);
        tabs.setup();

        TabHost.TabSpec spec = tabs.newTabSpec("mitab1");
        spec.setContent(R.id.tab1);
        spec.setIndicator("CONCILIADOS");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("mitab2");
        spec.setContent(R.id.tab2);
        spec.setIndicator("FALTANTES");
        tabs.addTab(spec);

        spec = tabs.newTabSpec("mitab3");
        spec.setContent(R.id.tab3);
        spec.setIndicator("SOBRANTES");
        tabs.addTab(spec);

        tabs.getTabWidget().getChildAt(0).getLayoutParams().width = 40;

        tabs.setCurrentTab(0);

        mViewModel.getFaltantesLiveData().observeForever(new Observer<List<InventarioDto>>() {
            @Override
            public void onChanged(@Nullable List<InventarioDto> inventarioDtos) {
                txt_contar_faltantes_sistema.setText("0");
                if (inventarioDtos != null) {
                    adapterf = new InventarioAdapter(getContext(), R.layout.despacho_item_list, inventarioDtos);
                    adapterf.setReporte(true);
                    recyclerF.setAdapter(adapterf);
                    int cantidad = inventarioDtos.stream().mapToInt(x -> (int) x.CantidadFisica).sum();
                    contarf.setText(cantidad + "");
                    txt_contar_faltantes_sistema.setText(inventarioDtos.stream().mapToInt(x -> (int) x.CantidadSistema).sum() + "");
                }
            }
        });

        mViewModel.getConciliadosLiveData().observeForever(new Observer<List<InventarioDto>>() {
            @Override
            public void onChanged(@Nullable List<InventarioDto> inventarioDtos) {
                if (inventarioDtos != null) {
                    adapterc = new InventarioAdapter(getContext(), R.layout.despacho_item_list, inventarioDtos);
                    adapterc.setReporte(true);
                    recyclerC.setAdapter(adapterc);
                    int cantidad = inventarioDtos.stream().mapToInt(x -> (int) x.CantidadFisica).sum();
                    contarc.setText(cantidad + "");
                }
            }
        });

        mViewModel.getSobrantesLiveData().observeForever(new Observer<List<InventarioDto>>() {
            @Override
            public void onChanged(@Nullable List<InventarioDto> inventarioDtos) {
                if (inventarioDtos != null) {
                    adapters = new InventarioAdapter(getContext(), R.layout.despacho_item_list, inventarioDtos);
                    adapters.setReporte(true);
                    recyclerS.setAdapter(adapters);
                    int cantidad = inventarioDtos.stream().mapToInt(x -> (int) x.CantidadFisica).sum();
                    contars.setText(cantidad + "");
                }

            }
        });

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogSave().execute();
            }
        });

        new DialogReporte().execute();

    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }

    public Reporte() {
        super("ReporteFragment");
    }

    private class DialogReporte extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pdia;

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                mViewModel.GenerateInventory();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
            pdia.setMessage("Generando Reporte...");
            pdia.setCancelable(false);
            pdia.show();
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            //super.onPostExecute(aVoid);
            btn_guardar.setEnabled(false);
            pdia.dismiss();
            if (aVoid) {
                MetodosGenerales.Mensaje("Reporte generado", getContext(), getResources());
                if (mViewModel.hasInventory()) {
                    btn_guardar.setEnabled(true);
                }
            } else {
                MetodosGenerales.Mensaje("No se puede generar el reporte", getContext(), getResources());
            }

        }
    }

    private class DialogSave extends AsyncTask<Void, Void, Boolean> {
        private ProgressDialog pdia;

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                mViewModel.SaveInventory();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
            pdia.setMessage("Enviando Reporte...");
            pdia.setCancelable(false);
            pdia.show();
        }

        @Override
        protected void onPostExecute(Boolean aVoid) {
            //super.onPostExecute(aVoid);
            btn_guardar.setEnabled(false);
            pdia.dismiss();
            if (aVoid) {
                MetodosGenerales.Mensaje("Reporte enviado", getContext(), getResources());
                if (mViewModel.hasInventory()) {
                    btn_guardar.setEnabled(true);
                }
            } else {
                MetodosGenerales.Mensaje("No se puede enviar el reporte", getContext(), getResources());
            }

        }
    }
}
