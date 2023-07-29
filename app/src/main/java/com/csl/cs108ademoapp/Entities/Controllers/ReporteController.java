package com.csl.cs108ademoapp.Entities.Controllers;

import android.app.Application;
import android.text.format.DateFormat;

import com.csl.cs108ademoapp.Entities.Clases.ACTIVO;
import com.csl.cs108ademoapp.Entities.Clases.REPORTE;
import com.csl.cs108ademoapp.Entities.Clases.STOCK;
import com.csl.cs108ademoapp.Entities.POJO.ACTIVO_POJO;
import com.csl.cs108ademoapp.Generales.MetodosGenerales;
import com.csl.cs108ademoapp.Interface.InventarioReporte;
import com.csl.cs108ademoapp.Models.ListaInv;
import com.csl.cs108ademoapp.Models.Sobrantes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ReporteController {


    private InventarioReporte inventarioReporte;

    public void setInventarioReporte(InventarioReporte inventarioReporte) {
        this.inventarioReporte = inventarioReporte;
    }

    public void GeneraInventario() {
        try {
            List<String> stock_codigos = MetodosGenerales.database.stock_dao().getCodigos();
            List<String> activo_codigos = MetodosGenerales.database.activo_dao().getCodRFID();
            int size_paquete = ((int) activo_codigos.size() / 800) + 1;
            for (int i = 0; i < size_paquete; i++) {
                if ((i + 1) * 800 > activo_codigos.size()) {
                    MetodosGenerales.database.stock_dao().updateEstadoByCodigos(activo_codigos.subList(i * 800, activo_codigos.size()));
                } else {
                    MetodosGenerales.database.stock_dao().updateEstadoByCodigos(activo_codigos.subList(i * 800, (i + 1) * 800));
                }
            }
            int size_paquete1 = ((int) stock_codigos.size() / 800) + 1;
            for (int i = 0; i < size_paquete1; i++) {
                if ((i + 1) * 800 > stock_codigos.size()) {
                    MetodosGenerales.database.activo_dao().updateEstadoByCodigos(stock_codigos.subList(i * 800, stock_codigos.size()));
                } else {
                    MetodosGenerales.database.activo_dao().updateEstadoByCodigos(stock_codigos.subList(i * 800, (i + 1) * 800));
                }
            }
            MetodosGenerales.database.reporte_dao().deleteAll();
            Conciliados();
            Faltantes();
            Sobrantes();
        } catch (Exception ex) {
            int i = 0;
            i = 1;
        }

    }

    public void Conciliados() {
        List<ACTIVO_POJO> conciliados = MetodosGenerales.database.activo_dao().getConciliados();
        ArrayList<ListaInv> array = new ArrayList<>();
        List<REPORTE> reporteList = new ArrayList<>();
        Date fecha = new Date();
        for (ACTIVO_POJO activo : conciliados) {
            array.add(new ListaInv(String.valueOf(activo.UBICACION),
                    activo.activo.ACT_CODBARRAS,
                    String.valueOf(activo.CUSTODIO),
                    String.valueOf(activo.DESCRIPCION)));
            REPORTE reporte = new REPORTE();
            reporte.ACT_ID = activo.activo.ACT_ID;
            reporte.CUS_ID = activo.activo.CUS_ID1;
            reporte.REP_CODBARRAS = activo.activo.ACT_CODBARRAS;
            reporte.REP_ESTADO = "M";
            reporte.REP_FECHA = fecha;
            reporte.REP_USUARIO = "cayman";
            reporteList.add(reporte);
        }

        MetodosGenerales.database.reporte_dao().insertAll(reporteList);
        this.inventarioReporte.ListaConciliado(array, array.size());
    }

    public void Faltantes() {
        List<ACTIVO_POJO> faltantes = MetodosGenerales.database.activo_dao().getFaltantes();
        ArrayList<ListaInv> array = new ArrayList<>();
        List<REPORTE> reporteList = new ArrayList<>();
        Date fecha = new Date();
        for (ACTIVO_POJO activo : faltantes) {
            array.add(new ListaInv(String.valueOf(activo.UBICACION),
                    activo.activo.ACT_CODBARRAS,
                    String.valueOf(activo.CUSTODIO),
                    String.valueOf(activo.DESCRIPCION)));
            REPORTE reporte = new REPORTE();
            reporte.ACT_ID = activo.activo.ACT_ID;
            reporte.CUS_ID = activo.activo.CUS_ID1;
            reporte.REP_CODBARRAS = activo.activo.ACT_CODBARRAS;
            reporte.REP_ESTADO = "O";
            reporte.REP_FECHA = fecha;
            reporte.REP_USUARIO = "cayman";
            reporteList.add(reporte);
        }
        MetodosGenerales.database.reporte_dao().insertAll(reporteList);
        inventarioReporte.ListaFaltante(array, array.size());
    }

    public void Sobrantes() {
        List<STOCK> sobrantes = MetodosGenerales.database.stock_dao().getSobrantes();
        ArrayList<Sobrantes> array = new ArrayList<>();
        List<REPORTE> reporteList = new ArrayList<>();
        Date fecha = new Date();
        for (STOCK stock : sobrantes) {
            Sobrantes sobrantes1 = new Sobrantes(stock.STO_CODIGO);
            array.add(sobrantes1);
            REPORTE reporte = new REPORTE();
            reporte.ACT_ID = 0;
            reporte.CUS_ID = 0;
            reporte.REP_CODBARRAS = stock.STO_CODIGO;
            reporte.REP_ESTADO = "N";
            reporte.REP_FECHA = fecha;
            reporte.REP_USUARIO = "cayman";
            reporteList.add(reporte);
        }
        MetodosGenerales.database.reporte_dao().insertAll(reporteList);
        inventarioReporte.ListaSobrante(array, array.size());
    }
}
