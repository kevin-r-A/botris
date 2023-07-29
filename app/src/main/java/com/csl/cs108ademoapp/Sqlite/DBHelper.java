package com.csl.cs108ademoapp.Sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.format.DateFormat;

import com.csl.cs108ademoapp.Interface.CargarActivos;
import com.csl.cs108ademoapp.Interface.InventarioReporte;
import com.csl.cs108ademoapp.Login;
import com.csl.cs108ademoapp.Models.ActivosC;
import com.csl.cs108ademoapp.Models.Combos;
import com.csl.cs108ademoapp.Models.ListaInv;
import com.csl.cs108ademoapp.Models.Reporte;
import com.csl.cs108ademoapp.Models.Sobrantes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;


/**
 * Created by Mauricio on 05/03/2018.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "db_cayman.db";

    private Context mContext;

    private InventarioReporte invent;
    private CargarActivos cactivos;

    //****///

    String fecha = (DateFormat.format("dd-MM-yyyy", new java.util.Date()).toString());
    String estadoc = "Conciliado", estadof = "Faltante", estados = "Sobrante";
    String usuario = "cayman";

    SQLiteDatabase sl = DBHelper.this.getWritableDatabase();

    public DBHelper(Context context, String install) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;

        if (!install.equals("")) {
            File pathFile = mContext.getDatabasePath(DATABASE_NAME);
            try {
                copyDataBase(pathFile);
                Login log = new Login();
                log.VerificaBase();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void createDataBase() throws IOException {
        File pathFile = mContext.getDatabasePath(DATABASE_NAME);
        boolean dbExist = checkDataBase(pathFile.getAbsolutePath());
        //copyDataBase(pathFile);
        if (!dbExist) {
            this.getReadableDatabase();
            try {
                copyDataBase(pathFile);
            } catch (IOException e) {
                // Error copying database
            }
        }
    }

    private boolean checkDataBase(String path) {
        SQLiteDatabase checkDB = null;
        try {
            checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        } catch (Exception e) {
            // DATABASE doesn't exist
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null;
    }

    private void copyDataBase(File pathFile) throws IOException {
        InputStream myInput = mContext.getAssets().open("db_cayman.db");
        OutputStream myOutput = new FileOutputStream(pathFile);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //* Metodo Interface *//
    public void setOnActivos(CargarActivos cargadatos) {
        cactivos = cargadatos;
    }

    public void setOnInventario(InventarioReporte inventario) {

        invent = inventario;
    }

    //* ELIMINAR *//

    public void EliminarStock() {
        sl.execSQL("DELETE FROM stock;");
        sl.execSQL("DELETE FROM sqlite_sequence WHERE name = 'STOCK';");

        cactivos.DeleteActivos();
    }


    public void ActualizaEstadoActivo() {
        sl.execSQL("update ACTIVO set ACT_ESTADO ='';");
    }
    public void EliminarStockLectura() {
        sl.execSQL("DELETE FROM stock;");
        sl.execSQL("DELETE FROM sqlite_sequence WHERE name = 'STOCK';");
    }

    public void EliminarReporte() {
        sl.execSQL("DELETE FROM REPORTE;");
        sl.execSQL("DELETE FROM sqlite_sequence WHERE name = 'REPORTE';");

        cactivos.DeleteActivos();
    }

    public void EliminarReporteRFID() {
        sl.execSQL("delete from REPORTE;");
        sl.execSQL("DELETE FROM sqlite_sequence WHERE name = 'REPORTE';");

    }

    public void EliminarActivo() {
        sl.execSQL("DELETE FROM ACTIVO;");
        sl.execSQL("DELETE FROM sqlite_sequence WHERE name = 'ACTIVO';");

        cactivos.DeleteActivos();
    }

    public void GuardarStock(String codigo, String estado) {
        sl.execSQL("insert into STOCK(sto_codigo, sto_estado) values('" + codigo + "','" + estado + "');");
    }

    //* Reportes *//

    public void GeneraInventario() {

        Cursor c1 = sl.rawQuery("SELECT sto_codigo FROM STOCK;", null);
        Cursor c2 = sl.rawQuery("SELECT ACT_CODRFID FROM ACTIVO WHERE ACT_ASIGNACION='1';", null);

        if (c1.moveToFirst()) {
            do {
                String codigostock = c1.getString(0);

                if (c2.moveToFirst()) {
                    do {
                        String codigotag = c2.getString(0);

                        if (codigostock.equals(codigotag)) {

                            sl.execSQL("update STOCK set sto_estado='C' where sto_codigo ='" + codigostock + "'");
                            sl.execSQL("update ACTIVO set ACT_ESTADO='C' where ACT_CODRFID ='" + codigostock + "'");
                        }
                    } while (c2.moveToNext());
                }

            } while (c1.moveToNext());
        }
        c1.close();
        c2.close();
        invent.Inventario();
    }


    public void Conciliados() {
        ArrayList<ListaInv> array = new ArrayList<>();
        Integer contador = 0;
        Cursor cursor;
        cursor = sl.rawQuery("select * from ACTIVO where ACT_ESTADO='C' and ACT_ASIGNACION='1';", null);

        if (cursor.moveToFirst()) {
            do {
                array.add(new ListaInv(cursor.getString(8),
                        cursor.getString(17),
                        cursor.getString(11),
                        cursor.getString(4)));

                sl.execSQL("insert into REPORTE values('" + cursor.getString(17).toString().trim() + "'," +
                        "'" + cursor.getString(9).toString().trim() + "','" + cursor.getString(0).toString().trim() + "'," + "'" + fecha + "','" + estadoc + "');");

                contador++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        invent.ListaConciliado(array, contador);
    }

    public void Faltantes() {
        ArrayList<ListaInv> array = new ArrayList<>();
        Integer contador = 0;
        Cursor cursor;
        cursor = sl.rawQuery("select * from ACTIVO where ACT_ASIGNACION='1' and (ACT_ESTADO='' or ACT_ESTADO=null);", null);

        if (cursor.moveToFirst()) {
            do {
                array.add(new ListaInv(cursor.getString(8),
                        cursor.getString(17),
                        cursor.getString(11),
                        cursor.getString(4)));

                sl.execSQL("insert into REPORTE values('" + cursor.getString(17).toString().trim() + "'," +
                        "'" + cursor.getString(9).toString().trim() + "','" + cursor.getString(0).toString().trim() + "'," + "'" + fecha + "','" + estadof + "');");
                contador++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        invent.ListaFaltante(array, contador);
    }

    public void Sobrantes() {
        ArrayList<Sobrantes> array = new ArrayList<>();
        Integer contador = 0;
        String codactivo = "0";
        String codubic = "NE";

        Cursor cursor = sl.rawQuery("select sto_codigo from STOCK where sto_estado='S';", null);

        if (cursor.moveToFirst()) {
            do {
                String rfid = cursor.getString(0).toString().trim();
                Cursor cursor1 = sl.rawQuery("select * from ACTIVO where ACT_CODRFID='" + rfid + "';", null);

                if (cursor1.getCount() > 0) {
                    if (cursor1.moveToFirst()) {
                        do {

                            array.add(new Sobrantes(cursor.getString(0)));
                            sl.execSQL("insert into REPORTE values('" + cursor1.getString(17).toString().trim() + "'," +
                                    "'" + rfid + "','" + cursor1.getString(0).toString().trim() + "'," + "'" + fecha + "','" + estados + "');");
                            contador++;

                        } while (cursor1.moveToNext());
                    }
                } else {
                    array.add(new Sobrantes(cursor.getString(0)));
                    sl.execSQL("insert into REPORTE values('" + codactivo + "'," +
                            "'" + rfid + "','" + codubic + "'," + "'" + fecha + "','" + estados + "');");
                    contador++;
                }
                cursor1.close();
            } while (cursor.moveToNext());
        }
        cursor.close();
        invent.ListaSobrante(array, contador);
    }

    public Integer VerificaActivo() {
        Integer contador = 0;
        Cursor cursor;
        cursor = sl.rawQuery("select * from ACTIVO;", null);
        contador = cursor.getCount();
        cursor.close();
        return contador;
    }

    public Integer VerificaReporte() {
        Integer contador = 0;
        Cursor cursor;
        cursor = sl.rawQuery("select * from REPORTE;", null);
        contador = cursor.getCount();
        cursor.close();
        return contador;
    }

    public ArrayList<Reporte> ReporteInventario() {

        ArrayList<Reporte> array = new ArrayList<>();


        Cursor cl = sl.rawQuery("SELECT * FROM REPORTE;", null);
        if (cl.moveToFirst()) {
            do {

                array.add(new Reporte(cl.getString(0), cl.getString(1), cl.getString(2), cl.getString(3), cl.getString(4)));

            } while (cl.moveToNext());
        }
        cl.close();
        return array;
    }


    //Inventarioo
    //Filtros


    public ArrayList CargarComboUgeo1Filtro() {
        ArrayList<Combos> array = new ArrayList<>();
        Cursor cursor;
        cursor = sl.rawQuery("select ACT_UBIC1, ACT_UBIC1 from ACTIVO group by ACT_UBIC1, ACT_UBIC1 order by ACT_UBIC1;", null);

        array.add(new Combos("0","Seleccione Filtro"));
        if (cursor.moveToFirst()) {
            do {
                array.add(new Combos(cursor.getString(0), cursor.getString(1)));

            } while (cursor.moveToNext());
        }
        return array;
    }


    public ArrayList CargarComboUgeo2Filtro() {
        ArrayList<Combos> array = new ArrayList<>();
        Cursor cursor;
        cursor = sl.rawQuery("select ACT_UBIC2, ACT_UBIC2 from ACTIVO group by ACT_UBIC2, ACT_UBIC2 order by ACT_UBIC2;", null);

        array.add(new Combos("1","Seleccione Filtro"));
        if (cursor.moveToFirst()) {
            do {
                array.add(new Combos(cursor.getString(0), cursor.getString(1)));

            } while (cursor.moveToNext());
        }
        return array;
    }

    public ArrayList CargarComboUgeo3Filtro() {
        ArrayList<Combos> array = new ArrayList<>();
        Cursor cursor;
        cursor = sl.rawQuery("select ACT_UBIC3, ACT_UBIC3 from ACTIVO group by ACT_UBIC3, ACT_UBIC3 order by ACT_UBIC3;", null);

        array.add(new Combos("2","Seleccione Filtro"));
        if (cursor.moveToFirst()) {
            do {
                array.add(new Combos(cursor.getString(0), cursor.getString(1)));

            } while (cursor.moveToNext());
        }
        return array;
    }

    public ArrayList CargarComboUgeo4Filtro() {
        ArrayList<Combos> array = new ArrayList<>();
        Cursor cursor;
        cursor = sl.rawQuery("select ACT_UBIC4, ACT_UBIC4 from ACTIVO group by ACT_UBIC4, ACT_UBIC4 order by ACT_UBIC4;", null);

        array.add(new Combos("3","Seleccione Filtro"));
        if (cursor.moveToFirst()) {
            do {
                array.add(new Combos(cursor.getString(0), cursor.getString(1)));

            } while (cursor.moveToNext());
        }
        return array;
    }

    public ArrayList CargarComboPisoFiltro() {
        ArrayList<Combos> array = new ArrayList<>();
        Cursor cursor;
        cursor = sl.rawQuery("select ACT_PISO, ACT_PISO from ACTIVO group by ACT_PISO, ACT_PISO order by ACT_PISO;", null);

        array.add(new Combos("4","Seleccione Filtro"));
        if (cursor.moveToFirst()) {
            do {
                array.add(new Combos(cursor.getString(0), cursor.getString(1)));

            } while (cursor.moveToNext());
        }
        return array;
    }

    public ArrayList CargarComboUorg1Filtro() {
        ArrayList<Combos> array = new ArrayList<>();
        Cursor cursor;
        cursor = sl.rawQuery("select ACT_UOR1, ACT_UOR1 from ACTIVO group by ACT_UOR1, ACT_UOR1 order by ACT_UOR1;", null);

        array.add(new Combos("5","Seleccione Filtro"));
        if (cursor.moveToFirst()) {
            do {
                array.add(new Combos(cursor.getString(0), cursor.getString(1)));

            } while (cursor.moveToNext());
        }
        return array;
    }

    public ArrayList CargarComboUorg2Filtro() {
        ArrayList<Combos> array = new ArrayList<>();
        Cursor cursor;
        cursor = sl.rawQuery("select ACT_UOR2, ACT_UOR2 from ACTIVO group by ACT_UOR2, ACT_UOR2 order by ACT_UOR2;", null);

        array.add(new Combos("6","Seleccione Filtro"));
        if (cursor.moveToFirst()) {
            do {
                array.add(new Combos(cursor.getString(0), cursor.getString(1)));

            } while (cursor.moveToNext());
        }
        return array;
    }

    public ArrayList CargarComboUorg3Filtro() {
        ArrayList<Combos> array = new ArrayList<>();
        Cursor cursor;
        cursor = sl.rawQuery("select ACT_UOR3, ACT_UOR3 from ACTIVO group by ACT_UOR3, ACT_UOR3 order by ACT_UOR3;", null);

        array.add(new Combos("7","Seleccione Filtro"));
        if (cursor.moveToFirst()) {
            do {
                array.add(new Combos(cursor.getString(0), cursor.getString(1)));

            } while (cursor.moveToNext());
        }
        return array;
    }

    public ArrayList CargarComboCustodioFiltro() {
        ArrayList<Combos> array = new ArrayList<>();
        Cursor cursor;
        cursor = sl.rawQuery("select ACT_CUSNOM, ACT_CUSNOM from ACTIVO group by ACT_CUSNOM, ACT_CUSNOM order by ACT_CUSNOM;", null);

        array.add(new Combos("8","Seleccione Filtro"));
        if (cursor.moveToFirst()) {
            do {
                array.add(new Combos(cursor.getString(0), cursor.getString(1)));

            } while (cursor.moveToNext());
        }
        return array;
    }


///////////Carga Datos con id Filtros

    public ArrayList CargarFiltros(String nugeo1,String nugeo2, String nugeo3, String nugeo4, String npiso, String nuorg1, String nuorg2, String nuorg3, String ncustodio) {

        ArrayList<String> array = new ArrayList<String>();

        Cursor cursor;
        cursor = sl.rawQuery("select * from ACTIVO where ACT_UBIC1=" + "'" + nugeo1 + "'"  + " or ACT_UBIC2= '" + nugeo2 + "'" + " or ACT_UBIC3= '" + nugeo3 + "'" + " or ACT_UBIC4= '" + nugeo4 + "'"
                + " or ACT_PISO= '" + npiso + "'"+ " or ACT_UOR1= '" + nuorg1 + "'" + " or ACT_UOR2= '" + nuorg2 + "'"+ " or ACT_UOR3= '" + nuorg3 + "'"
                + " or ACT_CUSNOM= '" + ncustodio + "'" + " order by ACT_CUSNOM;" , null);

        if (cursor.moveToFirst()) {
            do {
                array.add(cursor.getString(17));
            } while (cursor.moveToNext());
        }

        return array;
    }

    ///////Actualiza Estado Filtrados

    public void ActualizaEstado(String codigo) {
        try {
            sl.execSQL("update ACTIVO SET ACT_ASIGNACION='1' WHERE ACT_CODBARRAS='" + codigo + "';");
        } catch (Exception ex) {

        }
    }

    ///Reiniciar Filtros

    public void ReiniciaEstadoActivos() {
        try {
            sl.execSQL("update ACTIVO SET ACT_ESTADO='', ACT_FILTRO='',ACT_ASIGNACION='" + null + "';");
        } catch (Exception ex) {

        }
    }

    public void EliminarStockFiltro() {
        sl.execSQL("DELETE FROM stock;");
        sl.execSQL("DELETE FROM sqlite_sequence WHERE name = 'STOCK';");
    }

    public void EliminarReporteFiltros() {
        sl.execSQL("DELETE FROM REPORTE;");
        sl.execSQL("DELETE FROM sqlite_sequence WHERE name = 'REPORTE';");
    }



    //* GUARDAR ACTIVOS *//
    public void GuardarActivos(String ACT_CODIGO, String ACT_UBIC1, String ACT_UBIC2, String ACT_UBIC3, String ACT_UBIC4, String ACT_PISO,
                               String ACT_UOR1, String ACT_UOR2, String ACT_UOR3, String ACT_UORCOD, String ACT_CUSCOD, String ACT_CUSNOM,
                               String ACT_FFACTURA, String ACT_FNUM, String ACT_PROVEEDOR, String ACT_CODANTCLI1, String ACT_CODANTCLI2,
                               String ACT_CODBARRAS, String ACT_CODRFID, String ACT_GRUPO, String ACT_SUBGRUPO, String ACT_DESCRIPCION,
                               String ACT_DESCRIPLARGA, String ACT_MARCA, String ACT_MODELO, String ACT_SERIE, String ACT_ANIO, String ACT_ESTADOAC,
                               String ACT_OBSERVACIONES, String ACT_COLOR, String ACT_ESTRUCTURA, String ACT_COMPONENTE, String ACT_ESTADO,
                               String ACT_ASIGNACION, String ACT_FILTRO) {

        sl.execSQL("insert into ACTIVO(ACT_CODIGO, ACT_UBIC1, ACT_UBIC2, ACT_UBIC3, ACT_UBIC4, ACT_PISO, ACT_UOR1, ACT_UOR2, ACT_UOR3, ACT_UORCOD, ACT_CUSCOD, ACT_CUSNOM, ACT_FFACTURA, ACT_FNUM, ACT_PROVEEDOR, ACT_CODANTCLI1, ACT_CODANTCLI2, ACT_CODBARRAS, ACT_CODRFID, ACT_GRUPO, ACT_SUBGRUPO, ACT_DESCRIPCION, ACT_DESCRIPLARGA, ACT_MARCA, ACT_MODELO, ACT_SERIE, ACT_ANIO, ACT_ESTADOAC, ACT_OBSERVACIONES, ACT_COLOR,\n" +
                "ACT_ESTRUCTURA, ACT_COMPONENTE, ACT_ESTADO, ACT_ASIGNACION, ACT_FILTRO) " +
                "values('" + ACT_CODIGO + "','" + ACT_UBIC1 + "','" + ACT_UBIC2 + "','" + ACT_UBIC3 + "','" + ACT_UBIC4 + "','" + ACT_PISO + "'," +
                "'" + ACT_UOR1 + "','" + ACT_UOR2 + "','" + ACT_UOR3 + "','" + ACT_UORCOD + "','" + ACT_CUSCOD + "','" + ACT_CUSNOM + "','" + ACT_FFACTURA + "'," +
                "'" + ACT_FNUM + "','" + ACT_PROVEEDOR + "','" + ACT_CODANTCLI1 + "','" + ACT_CODANTCLI2 + "','" + ACT_CODBARRAS + "','" + ACT_CODRFID + "','" + ACT_GRUPO + "'," +
                "'" + ACT_SUBGRUPO + "','" + ACT_DESCRIPCION + "','" + ACT_DESCRIPLARGA + "','" + ACT_MARCA + "','" + ACT_MODELO + "','" + ACT_SERIE + "','" + ACT_ANIO + "'," +
                "'" + ACT_ESTADOAC + "','" + ACT_OBSERVACIONES + "','" + ACT_COLOR + "','" + ACT_ESTRUCTURA + "','" + ACT_COMPONENTE + "','" + ACT_ESTADO + "','" + ACT_ASIGNACION + "','" + ACT_FILTRO + "');");
    }

    public ArrayList CargaReporte() {
        ArrayList array =new ArrayList();
        Cursor c1;
        c1 = sl.rawQuery("select * from reporte;" , null);

        if (c1.moveToFirst()) {
            do {

                Cursor c2 = sl.rawQuery("select * from ACTIVO where ACT_CODBARRAS='" + c1.getString(0) + "';" , null);

                if (c2.moveToFirst()) {
                    do {

                        array.add(new ActivosC(c2.getString(0),c2.getString(1), c2.getString(2), c2.getString(3),c2.getString(4)
                                ,c2.getString(5),c2.getString(6),c2.getString(7),c2.getString(8),c2.getString(9),c2.getString(10)
                                ,c2.getString(11),c2.getString(12),c2.getString(13),c2.getString(14),c2.getString(15),c2.getString(16)
                                ,c2.getString(17),c2.getString(18),c2.getString(19),c2.getString(20),c2.getString(21),c2.getString(22)
                                ,c2.getString(23),c2.getString(24),c2.getString(25),c2.getString(26),c2.getString(27),c2.getString(28)
                                ,c2.getString(29),c2.getString(30),c2.getString(31),c1.getString(4),c2.getString(33),c2.getString(34)));

                    } while (c2.moveToNext());
                }else{
                    array.add(new ActivosC("","","","","","","",""
                            ,"","","","","","","",""
                            ,"","",c1.getString(1),"","","",""
                            ,"","","","","","","",""
                            ,"",c1.getString(4),"",""));
                }
                c2.close();
            } while (c1.moveToNext());

        }
        c1.close();

        return array;
    }

}