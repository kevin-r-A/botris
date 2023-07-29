package com.csl.cs108ademoapp.Models;

/**
 * Created by Mauricio on 11/09/2018.
 */

public class ListaInv {

    public String descripcion;
    public String codbarras;
    public String ubicacion;
    public String Bg;

    public ListaInv() {
    }

    public ListaInv(String descripcion, String codbarras, String ubicacion, String bg) {
        this.descripcion = descripcion;
        this.codbarras = codbarras;
        this.ubicacion = ubicacion;
        Bg = bg;
    }

    public String getBg() {
        return Bg;
    }

    public void setBg(String bg) {
        Bg = bg;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCodbarras() {
        return codbarras;
    }

    public void setCodbarras(String codbarras) {
        this.codbarras = codbarras;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }
}
