package com.csl.cs108ademoapp.Models;

/**
 * Created by Mauricio on 02/10/2018.
 */

public class Combos {
    public String idc;
    public String nombrec;

    public Combos() {
    }

    public String getIdc() {
        return idc;
    }

    public void setIdc(String idc) {
        this.idc = idc;
    }

    public String getNombrec() {
        return nombrec;
    }

    public void setNombrec(String nombrec) {
        this.nombrec = nombrec;
    }

    public Combos(String idc, String nombrec) {
        this.idc = idc;
        this.nombrec = nombrec;
    }

    @Override
    public String toString() {
        return nombrec;
    }
}
