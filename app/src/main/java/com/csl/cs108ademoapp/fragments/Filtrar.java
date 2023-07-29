package com.csl.cs108ademoapp.fragments;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.csl.cs108ademoapp.BuildConfig;
import com.csl.cs108ademoapp.Dialogo;
import com.csl.cs108ademoapp.Efectos.Effectstype;
import com.csl.cs108ademoapp.MainActivity;
import com.csl.cs108ademoapp.Models.Combos;
import com.csl.cs108ademoapp.R;
import com.csl.cs108ademoapp.Sqlite.DBHelper;

public class Filtrar extends CommonFragment {

    Spinner cmb_ugeo1,cmb_ugeo2,cmb_ugeo3,cmb_ugeo4,cmb_piso,cmb_uorg1,cmb_uorg2,cmb_uorg3,cmb_custodio;
    DBHelper db;
    Button btn_buscar, btn_guardar, btn_reiniciar;
    TextView total;
    ListView listaBusqueda;
    private ProgressDialog pdia;
    Effectstype effect;
    Combos combug1,combug2,combug3,combug4,combpiso,combor1,combor2,combor3,combcus;
    String idugeo1,idugeo2,idugeo3,idugeo4,idpiso,iduorg1,iduorg2,iduorg3,idcustodio;
    String nugeo1,nugeo2,nugeo3,nugeo4,npiso,nuorg1,nuorg2,nuorg3,ncustodio;
    ArrayAdapter<String> array;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState, true);
        return inflater.inflate(R.layout.activity_filtrar, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            //actionBar.setIcon(android.R.drawable.ic_menu_save);
            actionBar.setTitle("Filtros");
        }
        //ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        //actionBar.setTitle("Filtros");
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        db= new DBHelper(getActivity(),"");

        cmb_ugeo1 = getActivity().findViewById(R.id.cmb_ugeo1);
        cmb_ugeo2 = getActivity().findViewById(R.id.cmb_ugeo2);
        cmb_ugeo3 = getActivity().findViewById(R.id.cmb_ugeo3);
        cmb_ugeo4 = getActivity().findViewById(R.id.cmb_ugeo4);
        cmb_piso = getActivity().findViewById(R.id.cmb_piso);
        cmb_uorg1 = getActivity().findViewById(R.id.cmb_uorg1);
        cmb_uorg2 = getActivity().findViewById(R.id.cmb_uorg2);
        cmb_uorg3 = getActivity().findViewById(R.id.cmb_uorg3);
        cmb_custodio = getActivity().findViewById(R.id.cmb_custodio);

        btn_buscar=getActivity().findViewById(R.id.btn_buscar);
        btn_guardar=getActivity().findViewById(R.id.btn_guardar);
        btn_reiniciar=getActivity().findViewById(R.id.btn_reiniciar);
        total = getActivity().findViewById(R.id.totalf);
        listaBusqueda = getActivity().findViewById(R.id.lstitems);

        CargarCombos();

        cmb_ugeo1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                combug1 = (Combos) adapterView.getItemAtPosition(i);
                idugeo1= combug1.getIdc();
                nugeo1= (combug1.getNombrec()=="Seleccione Filtro")? "":combug1.getNombrec();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        cmb_ugeo2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                combug2 = (Combos) adapterView.getItemAtPosition(i);
                idugeo2= combug2.getIdc();
                nugeo2 = (combug2.getNombrec()=="Seleccione Filtro")? "":combug2.getNombrec();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        cmb_ugeo3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                combug3 = (Combos) adapterView.getItemAtPosition(i);
                idugeo3= combug3.getIdc();
                nugeo3= (combug3.getNombrec()=="Seleccione Filtro")? "":combug3.getNombrec();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        cmb_ugeo4.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                combug4 = (Combos) adapterView.getItemAtPosition(i);
                idugeo4= combug4.getIdc();
                nugeo4= (combug4.getNombrec()=="Seleccione Filtro")? "":combug4.getNombrec();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        cmb_piso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                combpiso = (Combos) adapterView.getItemAtPosition(i);
                idpiso= combpiso.getIdc();
                npiso= (combpiso.getNombrec()=="Seleccione Filtro")? "":combpiso.getNombrec();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        cmb_uorg1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                combor1 = (Combos) adapterView.getItemAtPosition(i);
                iduorg1= combor1.getIdc();
                nuorg1= (combor1.getNombrec()=="Seleccione Filtro")? "":combor1.getNombrec();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        cmb_uorg2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                combor2 = (Combos) adapterView.getItemAtPosition(i);
                iduorg2= combor2.getIdc();
                nuorg2= (combor2.getNombrec()=="Seleccione Filtro")? "":combor2.getNombrec();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        cmb_uorg3.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                combor3 = (Combos) adapterView.getItemAtPosition(i);
                iduorg3= combor3.getIdc();
                nuorg3= (combor3.getNombrec()=="Seleccione Filtro")? "":combor3.getNombrec();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        cmb_custodio.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                combcus = (Combos) adapterView.getItemAtPosition(i);
                idcustodio= combcus.getIdc();
                ncustodio= (combcus.getNombrec()=="Seleccione Filtro")? "":combcus.getNombrec();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        btn_buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!idugeo1.equals("0") || !idugeo2.equals("1") || !idugeo3.equals("2") || !idugeo4.equals("3") || !idpiso.equals("4") || !iduorg1.equals("5") || !iduorg2.equals("6") || !iduorg3.equals("7") || !idcustodio.equals("8")) {
                    array = new ArrayAdapter<String>(getActivity(), R.layout.result_item, db.CargarFiltros(nugeo1,nugeo2,nugeo3,nugeo4,npiso,nuorg1,nuorg2,nuorg3,ncustodio));
                    listaBusqueda.setAdapter(array);
                    total.setText("Total: " + Contar().toString());

                } else {
                    Mensaje("Seleccione un filtro de busqueda");
                }
            }
        });

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (listaBusqueda.getCount() > 0) {
                    ConfirmacionGuardar();

                } else {
                    Mensaje("Lista vacia, realice la busqueda de items");
                }
            }
        });

        btn_reiniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ConfirmacionReiniciar();
            }
        });
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
    }
    public Filtrar() {
        super("FiltrarFragment");
    }

    public void ConfirmacionGuardar() {
        final Dialogo dialogBuilder = Dialogo.getInstance(getActivity());

        dialogBuilder
                .withTitle("Cayman Aviso")
                .withMessage("Los items se modificarán. Desea Continuar?")
                .withIcon(getResources().getDrawable(R.drawable.pregunta))
                .isCancelableOnTouchOutside(false)
                .withDuration(700)
                .withEffect(effect)
                .withButton1Text("SI")
                .withButton2Text("NO")

                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DialogProgressGuardar().execute();
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

    private class DialogProgressGuardar extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            db.ReiniciaEstadoActivos();
            db.EliminarStockFiltro();
            db.EliminarReporteFiltros();
            GuardaItems();
            return null;
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
        protected void onPostExecute(Void aVoid) {
            pdia.dismiss();
            Mensaje("Datos Guardados Correctamente");
        }
    }

    public void GuardaItems(){

        for(int i=0; i< listaBusqueda.getCount(); i++)
        {
            db.ActualizaEstado(listaBusqueda.getItemAtPosition(i).toString());
        }
    }

    public void ConfirmacionReiniciar() {
        final Dialogo dialogBuilder = Dialogo.getInstance(getActivity());

        dialogBuilder
                .withTitle("Cayman Aviso")
                .withMessage("Los items se reiniciarán. Desea Continuar?")
                .withIcon(getResources().getDrawable(R.drawable.pregunta))
                .isCancelableOnTouchOutside(false)
                .withDuration(700)
                .withEffect(effect)
                .withButton1Text("SI")
                .withButton2Text("NO")

                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DialogProgressReiniciar().execute();
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

    private class DialogProgressReiniciar extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            db.ReiniciaEstadoActivos();
            db.EliminarStockFiltro();
            db.EliminarReporteFiltros();
            Limpiar();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
            pdia.setMessage("Reiniciando...");
            pdia.setCancelable(false);
            pdia.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            pdia.dismiss();
            Mensaje("Datos Reiniciados Correctamente");
        }
    }

    public void Limpiar() {

        //CargarCombos();

        //if (listaBusqueda.getCount()>0){
         //   listaBusqueda.setAdapter(null);
        //}

        //total.setText("Total: 0");

        //array.clear();
        //array.notifyDataSetChanged();

    }

    public void CargarCombos(){
        ArrayAdapter adaptador = new ArrayAdapter(getActivity(), R.layout.spinner_item, db.CargarComboUgeo1Filtro());
        cmb_ugeo1.setAdapter(adaptador);

        ArrayAdapter adaptador2 = new ArrayAdapter(getActivity(), R.layout.spinner_item, db.CargarComboUgeo2Filtro());
        cmb_ugeo2.setAdapter(adaptador2);

        ArrayAdapter adaptador3 = new ArrayAdapter(getActivity(), R.layout.spinner_item, db.CargarComboUgeo3Filtro());
        cmb_ugeo3.setAdapter(adaptador3);

        ArrayAdapter adaptador4 = new ArrayAdapter(getActivity(), R.layout.spinner_item, db.CargarComboUgeo4Filtro());
        cmb_ugeo4.setAdapter(adaptador4);

        ArrayAdapter adaptador5 = new ArrayAdapter(getActivity(), R.layout.spinner_item, db.CargarComboPisoFiltro());
        cmb_piso.setAdapter(adaptador5);

        ArrayAdapter adaptador6 = new ArrayAdapter(getActivity(), R.layout.spinner_item, db.CargarComboUorg1Filtro());
        cmb_uorg1.setAdapter(adaptador6);

        ArrayAdapter adaptador7 = new ArrayAdapter(getActivity(), R.layout.spinner_item, db.CargarComboUorg2Filtro());
        cmb_uorg2.setAdapter(adaptador7);

        ArrayAdapter adaptador8 = new ArrayAdapter(getActivity(), R.layout.spinner_item, db.CargarComboUorg3Filtro());
        cmb_uorg3.setAdapter(adaptador8);

        ArrayAdapter adaptador9 = new ArrayAdapter(getActivity(), R.layout.spinner_item, db.CargarComboCustodioFiltro());
        cmb_custodio.setAdapter(adaptador9);
    }


    public Integer Contar() {

        Integer contador = 0;
        for (int i = 0; i < listaBusqueda.getCount(); i++) {
            contador++;
        }

        return contador;
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
