package com.csl.cs108ademoapp.Models;

/**
 * Created by Mauricio on 10/09/2018.
 */

public class Reporte {
    public String REP_CODACTIVO;
    public String REP_CODRFID;
    public String REP_CODUBIC;
    public String REP_FECHA;
    public String REP_ESTADO;


    public Reporte() {
    }

    public Reporte(String REP_CODACTIVO, String REP_CODRFID, String REP_CODUBIC, String REP_FECHA, String REP_ESTADO) {
        this.REP_CODACTIVO = REP_CODACTIVO;
        this.REP_CODRFID = REP_CODRFID;
        this.REP_CODUBIC = REP_CODUBIC;
        this.REP_FECHA = REP_FECHA;
        this.REP_ESTADO = REP_ESTADO;
    }

    public String getREP_CODACTIVO() {
        return REP_CODACTIVO;
    }

    public void setREP_CODACTIVO(String REP_CODACTIVO) {
        this.REP_CODACTIVO = REP_CODACTIVO;
    }

    public String getREP_CODRFID() {
        return REP_CODRFID;
    }

    public void setREP_CODRFID(String REP_CODRFID) {
        this.REP_CODRFID = REP_CODRFID;
    }

    public String getREP_CODUBIC() {
        return REP_CODUBIC;
    }

    public void setREP_CODUBIC(String REP_CODUBIC) {
        this.REP_CODUBIC = REP_CODUBIC;
    }

    public String getREP_FECHA() {
        return REP_FECHA;
    }

    public void setREP_FECHA(String REP_FECHA) {
        this.REP_FECHA = REP_FECHA;
    }

    public String getREP_ESTADO() {
        return REP_ESTADO;
    }

    public void setREP_ESTADO(String REP_ESTADO) {
        this.REP_ESTADO = REP_ESTADO;
    }
}
