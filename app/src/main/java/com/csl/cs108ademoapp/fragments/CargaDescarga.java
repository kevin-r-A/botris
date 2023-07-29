package com.csl.cs108ademoapp.fragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.csl.cs108ademoapp.Dialogo;
import com.csl.cs108ademoapp.Efectos.Effectstype;
import com.csl.cs108ademoapp.Entities.Clases.ACTIVO;
import com.csl.cs108ademoapp.Entities.Clases.CUSTODIO;
import com.csl.cs108ademoapp.Entities.Clases.GRUPO;
import com.csl.cs108ademoapp.Entities.Clases.REPORTE;
import com.csl.cs108ademoapp.Entities.Clases.STOCK;
import com.csl.cs108ademoapp.Entities.Clases.UORGANICA;
import com.csl.cs108ademoapp.Entities.POJO.ACTIVO_POJO;
import com.csl.cs108ademoapp.Generales.MetodosGenerales;
import com.csl.cs108ademoapp.Interface.CargarActivos;
import com.csl.cs108ademoapp.MainActivity;
import com.csl.cs108ademoapp.Models.ActivosC;
import com.csl.cs108ademoapp.R;
import com.csl.cs108ademoapp.Sqlite.DBHelper;
import com.csl.cs108ademoapp.Web.JsonWebI;
import com.csl.cs108ademoapp.Web.JsonWebSt;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;


public class CargaDescarga extends CommonFragment {

    public Button cargar, descargar;
    Effectstype effect;
    int contadorcargar = 0;

    int contlist = 0;
    ArrayList<ActivosC> list = new ArrayList<>();
    Iterator rowIter;
    FileOutputStream out;
    XSSFWorkbook wb;
    XSSFSheet sheet;
    XSSFRow row;
    XSSFCell cell;

    DBHelper db;

    List<REPORTE> reporteList;

    String ACT_CODIGO, ACT_UBIC1, ACT_UBIC2, ACT_UBIC3, ACT_UBIC4, ACT_PISO, ACT_UOR1, ACT_UOR2, ACT_UOR3, ACT_UORCOD, ACT_CUSCOD, ACT_CUSNOM, ACT_FFACTURA, ACT_FNUM, ACT_PROVEEDOR, ACT_CODANTCLI1, ACT_CODANTCLI2, ACT_CODBARRAS, ACT_CODRFID, ACT_GRUPO, ACT_SUBGRUPO, ACT_DESCRIPCION, ACT_DESCRIPLARGA, ACT_MARCA, ACT_MODELO, ACT_SERIE, ACT_ANIO, ACT_ESTADOAC, ACT_OBSERVACIONES, ACT_COLOR,
            ACT_ESTRUCTURA, ACT_COMPONENTE;

    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState, true);
        return inflater.inflate(R.layout.activity_carga_descarga, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle("Carga/Descarga");

        MainActivity.mSensorConnector.mLocationDevice.turnOn(true);
        MainActivity.mSensorConnector.mSensorDevice.turnOn(true);

        db = new DBHelper(getActivity(), "");
        cargar = (Button) getActivity().findViewById(R.id.btn_cargardatos);
        descargar = (Button) getActivity().findViewById(R.id.btn_descargardatos);

        cargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    /*Msg_Garga();
                    if (0 == 0) {
                        return;
                    }*/
                    if (MetodosGenerales.TIPO_OPERACION == 1) {
                        Msg_Garga();
                    } else {
                        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/ACTIVOS.xlsx");

                        if (file.exists()) {

                            FileInputStream archivo = new FileInputStream(file);

                            XSSFWorkbook libroExcel = new XSSFWorkbook(archivo);
                            XSSFSheet hssfSheet = libroExcel.getSheetAt(0);
                            rowIter = hssfSheet.rowIterator();

                            Msg_Garga();

                            archivo.close();
                        } else {
                            Mensaje("Agrege archivo con nombre ACTIVOS.xlsx en la carpeta de Descargas de su dispositivo");
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Mensaje("Error al cargar archivo");
                } catch (IOException e) {
                    e.printStackTrace();
                    Mensaje("Error al cargar archivo");
                }
            }
        });


        descargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Msg_Descarga();
            }
        });

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public CargaDescarga() {

        super("CargaDescargaFragment");
    }


    public void Msg_Garga() {
        final Dialogo dialogBuilder = Dialogo.getInstance(getActivity());

        dialogBuilder
                .withTitle("Cayman Aviso")
                .withMessage("Los datos se cargarán. Desea Continuar?")
                .withIcon(getResources().getDrawable(R.drawable.pregunta))
                .isCancelableOnTouchOutside(false)
                .withDuration(700)
                .withEffect(effect)
                .withButton1Text("SI")
                .withButton2Text("NO")

                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DialogProgressCarga().execute();
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

    public void Msg_Descarga() {
        final Dialogo dialogBuilder = Dialogo.getInstance(getActivity());

        dialogBuilder
                .withTitle("Cayman Aviso")
                .withMessage("Los datos se descargarán. Desea Continuar?")
                .withIcon(getResources().getDrawable(R.drawable.pregunta))
                .isCancelableOnTouchOutside(false)
                .withDuration(700)
                .withEffect(effect)
                .withButton1Text("SI")
                .withButton2Text("NO")

                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new DialogProgressDescarga().execute();
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

    private class DialogProgressCarga extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pdia;

        @Override
        protected Void doInBackground(Void... voids) {
            CargarArchivo();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdia = new ProgressDialog(getActivity(), R.style.AppTheme_Dark_Dialog);
            pdia.setMessage("Cargando...");
            pdia.setCancelable(false);
            pdia.show();

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            pdia.dismiss();
            Mensaje("Activos Cargados : " + String.valueOf(contadorcargar));
        }
    }

    private class DialogProgressDescarga extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pdia;

        @Override
        protected Void doInBackground(Void... voids) {
            GuardarArchivo();
            return null;
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
        protected void onPostExecute(Void aVoid) {
            //super.onPostExecute(aVoid);
            pdia.dismiss();
            Mensaje("REPORTE Guardado, consulte el mismo en Cayman Activo Web");
        }
    }

    public void CargarArchivo() {
        if (MetodosGenerales.TIPO_OPERACION == 1) {
            JsonWebSt<List<ACTIVO>> jsonWebSt = new JsonWebSt<>(getContext(), new TypeToken<List<ACTIVO>>() {
            }, "ACTIVOes", false, true);
            jsonWebSt.setREQUEST("GET");
            jsonWebSt.setGetParametros("?PPC_ID=" + MetodosGenerales.txtPPC_ID);
            jsonWebSt.setOnComplete(new JsonWebI() {
                @Override
                public void onComplete(Object result) {
                    MetodosGenerales.database.activo_dao().deleteAll();
                    MetodosGenerales.database.stock_dao().deleteAll();
                    MetodosGenerales.database.reporte_dao().deleteAll();

                    List<ACTIVO> activos = ((List<ACTIVO>) result);
                    MetodosGenerales.database.activo_dao().insertAll(activos);
                    contadorcargar = activos.size();
                }

                @Override
                public void onError(String msg) {

                }

                @Override
                public void onAcepted(String msg) {

                }
            });

            jsonWebSt.start();
            try {
                jsonWebSt.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            CargaGrupos();
            CargaUorganicas();
            CargaCustodios();
        } else {
            db.setOnActivos(new CargarActivos() {
                @Override
                public void DeleteActivos() {
                }

            });
            MetodosGenerales.database.activo_dao().deleteAll();
            MetodosGenerales.database.stock_dao().deleteAll();
            MetodosGenerales.database.reporte_dao().deleteAll();
            MetodosGenerales.database.grupo_dao().deleteAll();
            MetodosGenerales.database.custodio_dao().deleteAll();
            MetodosGenerales.database.uorganica_dao().deleteAll();
            List<ACTIVO> activos = new ArrayList<>();

            while (rowIter.hasNext()) {
                contadorcargar++;

                Row row = (Row) rowIter.next();
                Iterator<Cell> cite = row.cellIterator();
                DataFormatter ft = new DataFormatter();

                if (contadorcargar > 1) {

                    while (cite.hasNext()) {
                        Log.i("CargaDescarga", contadorcargar + "");
                        ACTIVO activo = new ACTIVO();
                        for (int i = 0; i < 32; i++) {
                            Cell c = cite.next();
                            if (c.getColumnIndex() != i) {
                                i = c.getColumnIndex();
                            }
                            switch (i) {
                                case 0:
                                    activo.ACT_ID = Integer.parseInt(ft.formatCellValue(c));
                                    break;
                                case 1:
                                    activo.ACT_UBIC1 = ft.formatCellValue(c);
                                    break;
                                case 2:
                                    activo.ACT_UBIC2 = ft.formatCellValue(c);
                                    break;
                                case 3:
                                    activo.ACT_UBIC3 = ft.formatCellValue(c);
                                    break;
                                case 4:
                                    activo.ACT_UBIC4 = ft.formatCellValue(c);
                                    break;
                                case 5:
                                    activo.ACT_PISO = ft.formatCellValue(c);
                                    break;
                                case 6:
                                    UORGANICA uorganica1 = BuscaOrganica(ft.formatCellValue(c), 0, 0);
                                    activo.UOR_ID1 = uorganica1.UOR_ID;
                                    activo.ACT_UOR1 = uorganica1.UOR_NOMBRE;
                                    break;
                                case 7:
                                    UORGANICA uorganica2 = BuscaOrganica(ft.formatCellValue(c), 1, activo.UOR_ID1);
                                    activo.UOR_ID2 = uorganica2.UOR_ID;
                                    activo.ACT_UOR2 = uorganica2.UOR_NOMBRE;
                                    break;
                                case 8:
                                    UORGANICA uorganica3 = BuscaOrganica(ft.formatCellValue(c), 2, activo.UOR_ID2);
                                    activo.UOR_ID3 = uorganica3.UOR_ID;
                                    break;
                                case 9:
                                    activo.ACT_UORCOD = ft.formatCellValue(c);
                                    break;
                                case 10:
                                    activo.ACT_CUSCOD = ft.formatCellValue(c);
                                    break;
                                case 11:
                                    CUSTODIO custodio = BuscaCustodio(ft.formatCellValue(c));
                                    activo.CUS_ID1 = custodio.CUS_ID;
                                    break;
                                case 12:
                                    activo.ACT_FFACTURA = ft.formatCellValue(c);
                                    break;
                                case 13:
                                    activo.ACT_FNUM = ft.formatCellValue(c);
                                    break;
                                case 14:
                                    activo.ACT_PROVEEDOR = ft.formatCellValue(c);
                                    break;
                                case 15:
                                    activo.ACT_CODIGO1 = ft.formatCellValue(c);
                                    break;
                                case 16:
                                    activo.ACT_CODANTCLI2 = ft.formatCellValue(c);
                                    break;
                                case 17:
                                    activo.ACT_CODBARRAS = ft.formatCellValue(c);
                                    break;
                                case 18:
                                    activo.ACT_CODRFID = ft.formatCellValue(c);
                                    break;
                                case 19:
                                    GRUPO grupo = BuscaGrupo(ft.formatCellValue(c), 0, 0);
                                    activo.GRU_ID1 = grupo.GRU_ID;
                                    activo.ACT_GRUPO = grupo.GRU_NOMBRE;
                                    break;
                                case 20:
                                    GRUPO grupo2 = BuscaGrupo(ft.formatCellValue(c), activo.GRU_ID1, 1);
                                    activo.GRU_ID2 = grupo2.GRU_ID;
                                    activo.ACT_SUBGRUPO = grupo2.GRU_NOMBRE;
                                    break;
                                case 21:
                                    GRUPO grupo3 = BuscaGrupo(ft.formatCellValue(c), activo.GRU_ID2, 2);
                                    activo.GRU_ID3 = grupo3.GRU_ID;
                                    break;
                                case 22:
                                    activo.ACT_DESCRIPCIONLARGA = ft.formatCellValue(c);
                                    break;
                                case 23:
                                    activo.ACT_MARCA = ft.formatCellValue(c);
                                    break;
                                case 24:
                                    activo.ACT_MODELO = ft.formatCellValue(c);
                                    break;
                                case 25:
                                    activo.ACT_SERIE1 = ft.formatCellValue(c);
                                    break;
                                case 26:
                                    activo.ACT_ANIO = Integer.parseInt(ft.formatCellValue(c));
                                    break;
                                case 27:
                                    activo.ACT_ESTADOAC = ft.formatCellValue(c);
                                    break;
                                case 28:
                                    activo.ACT_OBSERVACIONES = ft.formatCellValue(c);
                                    break;
                                case 29:
                                    activo.ACT_COLOR = ft.formatCellValue(c);
                                    break;
                                case 30:
                                    activo.ACT_ESTRUCTURA = ft.formatCellValue(c);
                                    break;
                                case 31:
                                    activo.ACT_COMPONENTE = ft.formatCellValue(c);
                                    break;

                            }
                        }
                        activos.add(activo);
//                        db.GuardarActivos(ACT_CODIGO, ACT_UBIC1, ACT_UBIC2, ACT_UBIC3, ACT_UBIC4, ACT_PISO, ACT_UOR1, ACT_UOR2, ACT_UOR3, ACT_UORCOD, ACT_CUSCOD, ACT_CUSNOM, ACT_FFACTURA, ACT_FNUM, ACT_PROVEEDOR, ACT_CODANTCLI1, ACT_CODANTCLI2, ACT_CODBARRAS, ACT_CODRFID, ACT_GRUPO, ACT_SUBGRUPO, ACT_DESCRIPCION, ACT_DESCRIPLARGA, ACT_MARCA, ACT_MODELO, ACT_SERIE, ACT_ANIO, ACT_ESTADOAC, ACT_OBSERVACIONES, ACT_COLOR,
//                                ACT_ESTRUCTURA, ACT_COMPONENTE, "", "", "");
                    }
                    contadorcargar--;
                    MetodosGenerales.database.activo_dao().insertAll(activos);
                }
            }
        }
    }

    public void GuardarArchivo() {
        if (MetodosGenerales.TIPO_OPERACION == 1) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    reporteList = MetodosGenerales.database.reporte_dao().getAll();
                }
            });
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Gson gson = new Gson();
            String send = gson.toJson(reporteList);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(os, "UTF-8");
                outputStreamWriter.write(send);
                outputStreamWriter.close();
                outputStreamWriter.flush();
                os.flush();
            } catch (Exception ex) {

            }
            JsonWebSt<List<ACTIVO>> jsonWebSt = new JsonWebSt<>(getContext(), new TypeToken<String>() {
            }, "REPORTEs", true, true);
            jsonWebSt.setByteArrayOutputStream(os);
            jsonWebSt.setOnComplete(new JsonWebI() {
                @Override
                public void onComplete(Object result) {

                }

                @Override
                public void onError(String msg) {

                }

                @Override
                public void onAcepted(String msg) {

                }
            });
            jsonWebSt.start();
            try {
                jsonWebSt.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else {
            List<ACTIVO_POJO> conciliados = MetodosGenerales.database.activo_dao().getConciliados();
            List<ACTIVO_POJO> faltantes = MetodosGenerales.database.activo_dao().getFaltantes();
            List<STOCK> sobrantes = MetodosGenerales.database.stock_dao().getSobrantes();
            File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/REPORTE.xlsx");
            try {
                if (file.exists()) {
                    file.delete();
                }
                wb = new XSSFWorkbook();
                CreateSheet("CONCILIADO", conciliados);
                CreateSheet("FALTANTE", faltantes);
                CreateSheetSobrante("SOBRANTE", sobrantes);

                try (FileOutputStream out = new FileOutputStream(file)) {
                    wb.write(out);
                    out.flush();
                }

            } catch (IOException e) {
                e.printStackTrace();

            }
        }


//
//
//        try {
//            // write data to Excel file
//
//            if (!file.exists()) {

//            } else {
//
//                file.delete();
//                wb = new XSSFWorkbook();
//                sheet = wb.createSheet();
//                row = sheet.createRow(0);
//                cell = row.createCell(0);
//                cell.setCellValue("CODIGO");
//                cell = row.createCell(1);
//                cell.setCellValue("UBICACION GEOGRAFICA 1");
//                cell = row.createCell(2);
//                cell.setCellValue("UBICACION GEOGRAFICA 2");
//                cell = row.createCell(3);
//                cell.setCellValue("UBICACION GEOGRAFICA 3");
//                cell = row.createCell(4);
//                cell.setCellValue("UBICACION GEOGRAFICA 4");
//                cell = row.createCell(5);
//                cell.setCellValue("PISO");
//                cell = row.createCell(6);
//                cell.setCellValue("DISTRIBUCION ORGANICA NIVEL 1");
//                cell = row.createCell(7);
//                cell.setCellValue("DISTRIBUCION ORGANICA NIVEL 2");
//                cell = row.createCell(8);
//                cell.setCellValue("CENTRO DE COSTO");
//                cell = row.createCell(9);
//                cell.setCellValue("CODIGO CENTRO DE COSTO");
//                cell = row.createCell(10);
//                cell.setCellValue("CODIGO DE CUSTODIO");
//                cell = row.createCell(11);
//                cell.setCellValue("CUSTODIO");
//                cell = row.createCell(12);
//                cell.setCellValue("FECHA DE FACTURA");
//                cell = row.createCell(13);
//                cell.setCellValue("NUMERO DE FACTURA");
//                cell = row.createCell(14);
//                cell.setCellValue("PROVEEDOR");
//                cell = row.createCell(15);
//                cell.setCellValue("CODIGO ANTERIOR DEL CLIENTE");
//                cell = row.createCell(16);
//                cell.setCellValue("CODIGO ANTERIOR DEL CLIENTE 2");
//                cell = row.createCell(17);
//                cell.setCellValue("CODIGO DE BARRAS AVALUAC");
//                cell = row.createCell(18);
//                cell.setCellValue("CODIGO RFID");
//                cell = row.createCell(19);
//                cell.setCellValue("GRUPO");
//                cell = row.createCell(20);
//                cell.setCellValue("SUBGRUPO");
//                cell = row.createCell(21);
//                cell.setCellValue("DESCRIPCION");
//                cell = row.createCell(22);
//                cell.setCellValue("DESCRIPCION LARGA");
//                cell = row.createCell(23);
//                cell.setCellValue("MARCA");
//                cell = row.createCell(24);
//                cell.setCellValue("MODELO");
//                cell = row.createCell(25);
//                cell.setCellValue("SERIE");
//                cell = row.createCell(26);
//                cell.setCellValue("AÑO");
//                cell = row.createCell(27);
//                cell.setCellValue("ESTADOAC");
//                cell = row.createCell(28);
//                cell.setCellValue("OBSERVACIONES");
//                cell = row.createCell(29);
//                cell.setCellValue("COLOR");
//                cell = row.createCell(30);
//                cell.setCellValue("ESTRUCTURA");
//                cell = row.createCell(31);
//                cell.setCellValue("COMPONENTE");
//                cell = row.createCell(32);
//                cell.setCellValue("ESTADO");
//
//                row = sheet.createRow(1);
//
//                for (ActivosC rep : list) {
//                    contlist++;
//                    row = sheet.createRow(contlist);
//                    cell = row.createCell(0);
//                    cell.setCellValue(rep.getACT_CODIGO());
//                    cell = row.createCell(1);
//                    cell.setCellValue(rep.getACT_UBIC1());
//                    cell = row.createCell(2);
//                    cell.setCellValue(rep.getACT_UBIC2());
//                    cell = row.createCell(3);
//                    cell.setCellValue(rep.getACT_UBIC3());
//                    cell = row.createCell(4);
//                    cell.setCellValue(rep.getACT_UBIC4());
//                    cell = row.createCell(5);
//                    cell.setCellValue(rep.getACT_PISO());
//                    cell = row.createCell(6);
//                    cell.setCellValue(rep.getACT_UOR1());
//                    cell = row.createCell(7);
//                    cell.setCellValue(rep.getACT_UOR2());
//                    cell = row.createCell(8);
//                    cell.setCellValue(rep.getACT_UOR3());
//                    cell = row.createCell(9);
//                    cell.setCellValue(rep.getACT_UORCOD());
//                    cell = row.createCell(10);
//                    cell.setCellValue(rep.getACT_CUSCOD());
//                    cell = row.createCell(11);
//                    cell.setCellValue(rep.getACT_CUSNOM());
//                    cell = row.createCell(12);
//                    cell.setCellValue(rep.getACT_FFACTURA());
//                    cell = row.createCell(13);
//                    cell.setCellValue(rep.getACT_FNUM());
//                    cell = row.createCell(14);
//                    cell.setCellValue(rep.getACT_PROVEEDOR());
//                    cell = row.createCell(15);
//                    cell.setCellValue(rep.getACT_CODANTCLI1());
//                    cell = row.createCell(16);
//                    cell.setCellValue(rep.getACT_CODANTCLI2());
//                    cell = row.createCell(17);
//                    cell.setCellValue(rep.getACT_CODBARRAS());
//                    cell = row.createCell(18);
//                    cell.setCellValue(rep.getACT_CODRFID());
//                    cell = row.createCell(19);
//                    cell.setCellValue(rep.getACT_GRUPO());
//                    cell = row.createCell(20);
//                    cell.setCellValue(rep.getACT_SUBGRUPO());
//                    cell = row.createCell(21);
//                    cell.setCellValue(rep.getACT_DESCRIPCION());
//                    cell = row.createCell(22);
//                    cell.setCellValue(rep.getACT_DESCRIPLARGA());
//                    cell = row.createCell(23);
//                    cell.setCellValue(rep.getACT_MARCA());
//                    cell = row.createCell(24);
//                    cell.setCellValue(rep.getACT_MODELO());
//                    cell = row.createCell(25);
//                    cell.setCellValue(rep.getACT_SERIE());
//                    cell = row.createCell(26);
//                    cell.setCellValue(rep.getACT_ANIO());
//                    cell = row.createCell(27);
//                    cell.setCellValue(rep.getACT_ESTADOAC());
//                    cell = row.createCell(28);
//                    cell.setCellValue(rep.getACT_OBSERVACIONES());
//                    cell = row.createCell(29);
//                    cell.setCellValue(rep.getACT_COLOR());
//                    cell = row.createCell(30);
//                    cell.setCellValue(rep.getACT_ESTRUCTURA());
//                    cell = row.createCell(31);
//                    cell.setCellValue(rep.getACT_COMPONENTE());
//                    cell = row.createCell(32);
//                    cell.setCellValue(rep.getACT_ESTADO());
//                }
//
//                try (FileOutputStream out = new FileOutputStream(file)) {
//                    wb.write(out);
//                    out.flush();
//                } catch (Exception e) {
//                    Mensaje("Error al cargar archivo");
//                }
//                out.close();
//            }
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            Mensaje("Error al cargar archivo");
//        }
    }

    private void CreateSheetSobrante(String name, List<STOCK> list) {
        sheet = wb.createSheet(name);
        row = sheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue("CODIGO");
        row = sheet.createRow(1);
        contlist = 0;
        for (STOCK rep : list) {
            contlist++;
            row = sheet.createRow(contlist);
            cell = row.createCell(0);
            cell.setCellValue(rep.STO_CODIGO);
        }

    }

    private void CreateSheet(String name, List<ACTIVO_POJO> list) {
        sheet = wb.createSheet(name);
        row = sheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue("CODIGO");
        cell = row.createCell(1);
        cell.setCellValue("UBICACION GEOGRAFICA 1");
        cell = row.createCell(2);
        cell.setCellValue("UBICACION GEOGRAFICA 2");
        cell = row.createCell(3);
        cell.setCellValue("UBICACION GEOGRAFICA 3");
        cell = row.createCell(4);
        cell.setCellValue("UBICACION GEOGRAFICA 4");
        cell = row.createCell(5);
        cell.setCellValue("PISO");
        cell = row.createCell(6);
        cell.setCellValue("DISTRIBUCION ORGANICA NIVEL 1");
        cell = row.createCell(7);
        cell.setCellValue("DISTRIBUCION ORGANICA NIVEL 2");
        cell = row.createCell(8);
        cell.setCellValue("CENTRO DE COSTO");
        cell = row.createCell(9);
        cell.setCellValue("CODIGO CENTRO DE COSTO");
        cell = row.createCell(10);
        cell.setCellValue("CODIGO DE CUSTODIO");
        cell = row.createCell(11);
        cell.setCellValue("CUSTODIO");
        cell = row.createCell(12);
        cell.setCellValue("FECHA DE FACTURA");
        cell = row.createCell(13);
        cell.setCellValue("NUMERO DE FACTURA");
        cell = row.createCell(14);
        cell.setCellValue("PROVEEDOR");
        cell = row.createCell(15);
        cell.setCellValue("CODIGO ANTERIOR DEL CLIENTE");
        cell = row.createCell(16);
        cell.setCellValue("CODIGO ANTERIOR DEL CLIENTE 2");
        cell = row.createCell(17);
        cell.setCellValue("CODIGO DE BARRAS AVALUAC");
        cell = row.createCell(18);
        cell.setCellValue("CODIGO RFID");
        cell = row.createCell(19);
        cell.setCellValue("GRUPO");
        cell = row.createCell(20);
        cell.setCellValue("SUBGRUPO");
        cell = row.createCell(21);
        cell.setCellValue("DESCRIPCION");
        cell = row.createCell(22);
        cell.setCellValue("DESCRIPCION LARGA");
        cell = row.createCell(23);
        cell.setCellValue("MARCA");
        cell = row.createCell(24);
        cell.setCellValue("MODELO");
        cell = row.createCell(25);
        cell.setCellValue("SERIE");
        cell = row.createCell(26);
        cell.setCellValue("AÑO");
        cell = row.createCell(27);
        cell.setCellValue("ESTADOAC");
        cell = row.createCell(28);
        cell.setCellValue("OBSERVACIONES");
        cell = row.createCell(29);
        cell.setCellValue("COLOR");
        cell = row.createCell(30);
        cell.setCellValue("ESTRUCTURA");
        cell = row.createCell(31);
        cell.setCellValue("COMPONENTE");
        cell = row.createCell(32);
        cell.setCellValue("ESTADO");

        row = sheet.createRow(1);
        contlist = 0;
        for (ACTIVO_POJO rep : list) {
            contlist++;
            row = sheet.createRow(contlist);
            cell = row.createCell(0);
            cell.setCellValue(rep.activo.ACT_ID);
            cell = row.createCell(1);
            cell.setCellValue(rep.activo.ACT_UBIC1);
            cell = row.createCell(2);
            cell.setCellValue(rep.activo.ACT_UBIC2);
            cell = row.createCell(3);
            cell.setCellValue(rep.activo.ACT_UBIC3);
            cell = row.createCell(4);
            cell.setCellValue(rep.activo.ACT_UBIC4);
            cell = row.createCell(5);
            cell.setCellValue(rep.activo.ACT_PISO);
            cell = row.createCell(6);
            cell.setCellValue(rep.activo.ACT_UOR1);
            cell = row.createCell(7);
            cell.setCellValue(rep.activo.ACT_UOR2);
            cell = row.createCell(8);
            cell.setCellValue(rep.UBICACION);
            cell = row.createCell(9);
            cell.setCellValue(rep.activo.ACT_UORCOD);
            cell = row.createCell(10);
            cell.setCellValue(rep.activo.ACT_CUSCOD);
            cell = row.createCell(11);
            cell.setCellValue(rep.CUSTODIO);
            cell = row.createCell(12);
            cell.setCellValue(rep.activo.ACT_FFACTURA);
            cell = row.createCell(13);
            cell.setCellValue(rep.activo.ACT_FNUM);
            cell = row.createCell(14);
            cell.setCellValue(rep.activo.ACT_PROVEEDOR);
            cell = row.createCell(15);
            cell.setCellValue(rep.activo.ACT_CODIGO1);
            cell = row.createCell(16);
            cell.setCellValue(rep.activo.ACT_CODANTCLI2);
            cell = row.createCell(17);
            cell.setCellValue(rep.activo.ACT_CODBARRAS);
            cell = row.createCell(18);
            cell.setCellValue(rep.activo.ACT_CODRFID);
            cell = row.createCell(19);
            cell.setCellValue(rep.activo.ACT_GRUPO);
            cell = row.createCell(20);
            cell.setCellValue(rep.activo.ACT_SUBGRUPO);
            cell = row.createCell(21);
            cell.setCellValue(rep.DESCRIPCION);
            cell = row.createCell(22);
            cell.setCellValue(rep.activo.ACT_DESCRIPCIONLARGA);
            cell = row.createCell(23);
            cell.setCellValue(rep.activo.ACT_MARCA);
            cell = row.createCell(24);
            cell.setCellValue(rep.activo.ACT_MODELO);
            cell = row.createCell(25);
            cell.setCellValue(rep.activo.ACT_SERIE1);
            cell = row.createCell(26);
            cell.setCellValue(rep.activo.ACT_ANIO);
            cell = row.createCell(27);
            cell.setCellValue(rep.activo.ACT_ESTADOAC);
            cell = row.createCell(28);
            cell.setCellValue(rep.activo.ACT_OBSERVACIONES);
            cell = row.createCell(29);
            cell.setCellValue(rep.activo.ACT_COLOR);
            cell = row.createCell(30);
            cell.setCellValue(rep.activo.ACT_ESTRUCTURA);
            cell = row.createCell(31);
            cell.setCellValue(rep.activo.ACT_COMPONENTE);
            cell = row.createCell(32);
            cell.setCellValue(name);
        }
    }

    private void CargaGrupos() {
        JsonWebSt<List<GRUPO>> jsonWebSt = new JsonWebSt<>(getContext(), new TypeToken<List<GRUPO>>() {
        }, "GRUPOes", false, true);
        jsonWebSt.setREQUEST("GET");
        jsonWebSt.setOnComplete(new JsonWebI<List<GRUPO>>() {
            @Override
            public void onComplete(List<GRUPO> grupos) {
                MetodosGenerales.database.grupo_dao().deleteAll();
                MetodosGenerales.database.grupo_dao().insertAll(grupos);
            }

            @Override
            public void onError(String msg) {

            }

            @Override
            public void onAcepted(String msg) {

            }
        });

        jsonWebSt.start();
        try {
            jsonWebSt.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void CargaUorganicas() {
        JsonWebSt<List<UORGANICA>> jsonWebSt = new JsonWebSt<>(getContext(), new TypeToken<List<UORGANICA>>() {
        }, "UORGANICAs", false, true);
        jsonWebSt.setREQUEST("GET");
        jsonWebSt.setOnComplete(new JsonWebI<List<UORGANICA>>() {
            @Override
            public void onComplete(List<UORGANICA> uorganicas) {
                MetodosGenerales.database.uorganica_dao().deleteAll();
                MetodosGenerales.database.uorganica_dao().insertAll(uorganicas);
            }

            @Override
            public void onError(String msg) {

            }

            @Override
            public void onAcepted(String msg) {

            }
        });

        jsonWebSt.start();
        try {
            jsonWebSt.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void CargaCustodios() {
        JsonWebSt<List<CUSTODIO>> jsonWebSt = new JsonWebSt<>(getContext(), new TypeToken<List<CUSTODIO>>() {
        }, "CUSTODIOs", false, true);
        jsonWebSt.setREQUEST("GET");
        jsonWebSt.setOnComplete(new JsonWebI<List<CUSTODIO>>() {
            @Override
            public void onComplete(List<CUSTODIO> custodios) {
                MetodosGenerales.database.custodio_dao().deleteAll();
                MetodosGenerales.database.custodio_dao().insertAll(custodios);
            }

            @Override
            public void onError(String msg) {

            }

            @Override
            public void onAcepted(String msg) {

            }
        });

        jsonWebSt.start();
        try {
            jsonWebSt.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private GRUPO BuscaGrupo(String nombre, int padre, int nivel) {
        GRUPO grupo = MetodosGenerales.database.grupo_dao().GetGrupo(nombre, nivel, padre);
        if (grupo == null) {
            grupo = new GRUPO();
            grupo.GRU_NOMBRE = nombre;
            grupo.GRU_NIVEL = nivel;
            grupo.GRU_PADRE = padre;
            grupo.GRU_ID = Integer.parseInt(MetodosGenerales.database.grupo_dao().insert(grupo).toString());
        }
        return grupo;
    }

    private CUSTODIO BuscaCustodio(String nombre) {
        CUSTODIO custodio = MetodosGenerales.database.custodio_dao().GetCustodio(nombre);
        if (custodio == null) {
            custodio = new CUSTODIO();
            custodio.CUS_APELLIDOS = nombre;
            custodio.CUS_NOMBRES = "";
            custodio.CUS_ID = Integer.parseInt(MetodosGenerales.database.custodio_dao().insert(custodio).toString());
        }
        return custodio;
    }

    private UORGANICA BuscaOrganica(String nombre, int nivel, int padre) {
        UORGANICA uorganica = MetodosGenerales.database.uorganica_dao().GetUorganica(nombre, nivel, padre);
        if (uorganica == null) {
            uorganica = new UORGANICA();
            uorganica.UOR_NOMBRE = nombre;
            uorganica.UOR_NIVEL = nivel;
            uorganica.UOR_PADRE = padre;
            uorganica.UOR_ID = Integer.parseInt(MetodosGenerales.database.uorganica_dao().insert(uorganica).toString());
        }
        return uorganica;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
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
