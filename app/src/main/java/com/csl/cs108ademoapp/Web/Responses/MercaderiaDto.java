package com.csl.cs108ademoapp.Web.Responses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
@Entity
public class MercaderiaDto  {
    @SerializedName("suC_CODIGO")
    public String SUC_CODIGO;
    @SerializedName("emP_CODIGO")
    public String EMP_CODIGO;
    @SerializedName("boD_CODIGO")
    public String BOD_CODIGO;
    @SerializedName("arT_CODIGO")
    public String ART_CODIGO;
    @SerializedName("arT_CODIGOBARRA")
    public String ART_CODIGOBARRA;
    @SerializedName("arT_NOMBRE")
    public String ART_NOMBRE;
    @SerializedName("suC_NOMBRE")
    public String SUC_NOMBRE;
    @SerializedName("boD_NOMBRE")
    public String BOD_NOMBRE;
    @SerializedName("cantidad")
    public int CANTIDAD;
    @SerializedName("precio")
    public float PRECIO;

    @PrimaryKey(autoGenerate = true)
    public int Id;

    public int getId() {
        return Id;
    }
}
