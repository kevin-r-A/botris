package com.csl.cs108ademoapp.Web.Responses;

import android.arch.persistence.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity
public class ProductoDto extends EntityResponse {

    @SerializedName("nombre")
    public String Nombre;
    @SerializedName("codBarras")
    public String CodBarras;
    @SerializedName("codigo")
    public String Codigo;
    @SerializedName("marca")
    public String Marca;


    @Override
    public String toString() {
        return Nombre;
    }

    public String getValue() {
        return Nombre;
    }
}
