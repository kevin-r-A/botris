package com.csl.cs108ademoapp.Web.Responses;

import android.arch.persistence.room.Entity;

import com.google.gson.annotations.SerializedName;

@Entity
public class MovimientoProductoDto extends EntityResponse{
    @SerializedName("codBarra")
    public String CodBarra;
    @SerializedName("codRfid")
    public String CodRfid;
    @SerializedName("ubicacionId")
    public String UbicacionId;
    @SerializedName("ubicacionNombre")
    public String UbicacionNombre;
    @SerializedName("estado")
    public int Estado;
    @SerializedName("productoId")
    public String ProductoId;
    @SerializedName("productoNombre")
    public String ProductoNombre;
    @SerializedName("marca")
    public String Marca;

    public boolean Eliminado;
    public boolean Enviado;
}
