package com.csl.cs108ademoapp.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.csl.cs108ademoapp.Dialogo;
import com.csl.cs108ademoapp.Efectos.Effectstype;
import com.csl.cs108ademoapp.Entities.Clases.SERVER;
import com.csl.cs108ademoapp.Generales.MetodosGenerales;
import com.csl.cs108ademoapp.MainActivity;
import com.csl.cs108ademoapp.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link Configuracion#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Configuracion extends CommonFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText txtPPC_ID, txtURL;
    private Button btnGuardar, btnCSV, btnWEBSERVICE;
    Effectstype effect;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    //private OnFragmentInteractionListener mListener;

    public Configuracion() {
        super("ConfiguracionFragment");
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Configuracion.
     */
    // TODO: Rename and change types and number of parameters
    public static Configuracion newInstance(String param1, String param2) {
        Configuracion fragment = new Configuracion();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Configuración");

        //MainActivity.mSensorConnector.mLocationDevice.turnOn(true);
        //MainActivity.mSensorConnector.mSensorDevice.turnOn(true);
        txtURL = getActivity().findViewById(R.id.txtURL);
        txtPPC_ID = getActivity().findViewById(R.id.txtPPC_ID);
        btnCSV = getActivity().findViewById(R.id.btnCSV);
        btnWEBSERVICE = getActivity().findViewById(R.id.btnWEBSERVICE);
        btnGuardar = getActivity().findViewById(R.id.btn_guardar);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialogo dialogBuilder = Dialogo.getInstance(getActivity());

                dialogBuilder
                        .withTitle("Cayman Aviso")
                        .withMessage("Los datos se guardaran. Desea Continuar?")
                        .withIcon(getResources().getDrawable(R.drawable.pregunta))
                        .isCancelableOnTouchOutside(false)
                        .withDuration(700)
                        .withEffect(effect)
                        .withButton1Text("SI")
                        .withButton2Text("NO")

                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new save().execute();
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
        });
        btnCSV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodosGenerales.TIPO_OPERACION = 2;
                Toast.makeText(getActivity(), "MODO ARCHIVO CSV...", Toast.LENGTH_SHORT).show();
            }
        });
        btnWEBSERVICE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MetodosGenerales.TIPO_OPERACION = 1;
                Toast.makeText(getActivity(), "MODO ARCHIVO WEBSERVICE...", Toast.LENGTH_SHORT).show();
            }
        });
        new init().execute();
    }

    /*@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreateView(inflater, container, savedInstanceState, true);
        return inflater.inflate(R.layout.fragment_configuracion, container, false);
    }

    private class init extends AsyncTask<Void, Void, Void> {
        SERVER server;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (server == null) server = new SERVER();
            txtPPC_ID.setText(server.PPC_ID);
            txtURL.setText(server.URL);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            server = MetodosGenerales.database.server_dao().getServer();
            return null;
        }
    }

    private class save extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pdia;
        private SERVER server = new SERVER();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
            pdia.setMessage("Guardando...");
            pdia.setCancelable(false);
            pdia.show();
            server.PPC_ID = txtPPC_ID.getText().toString();
            server.URL = txtURL.getText().toString();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pdia.dismiss();
            MetodosGenerales.txtPPC_ID = server.PPC_ID;
            MetodosGenerales.txtURL = server.URL;
            final Dialogo dialogBuilder = Dialogo.getInstance(getActivity());

            dialogBuilder
                    .withTitle("Cayman Aviso")
                    .withMessage("Datos Guardados")
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

        @Override
        protected Void doInBackground(Void... voids) {
            MetodosGenerales.database.server_dao().deleteAll();
            MetodosGenerales.database.server_dao().insert(server);

            return null;
        }
    }
}
