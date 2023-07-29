package com.csl.cs108ademoapp.Web.Responses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.TypeConverters;

import com.csl.cs108ademoapp.Convertidores.MarcasTypeConverter;
import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity
public class UbicacionDto extends EntityResponse {
    @SerializedName("nombre")
    public String Nombre;
    @SerializedName("ciudad")
    public String Ciudad;
    @SerializedName("tipo")
    public int Tipo;
    @SerializedName("codigo")
    public String Codigo;
    @SerializedName("sucursal")
    public String Sucursal;
    @SerializedName("codigoSucursal")
    public String CodigoSucursal;
    @SerializedName("marcas")
    @TypeConverters({MarcasTypeConverter.class})
    public List<String> Marcas;

    @Override
    public String toString() {
        return Nombre;
    }

    public String getValue() {
        return Codigo;
    }
}
