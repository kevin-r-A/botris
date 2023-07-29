package com.csl.cs108ademoapp.Web.Responses;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.google.gson.annotations.SerializedName;

@Entity
public class OrdenDespachoRecepcionDetalleDto extends EntityResponse {
    @SerializedName("ordenDespachoRecepcionId")
    public String OrdenDespachoRecepcionId;
    @SerializedName("cantidad")
    public float Cantidad;
    @SerializedName("cantidadDespachada")
    public float CantidadDespachada;
    @SerializedName("cantidadRecibida")
    public float CantidadRecibida;
    @SerializedName("codBarras")
    public String CodBarras;
    @SerializedName("productoId")
    public String ProductoId;

}
