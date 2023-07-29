package com.csl.cs108ademoapp.Web.Responses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.TypeConverters;

import com.csl.cs108ademoapp.Convertidores.TimestampConverter;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
public class OrdenDespachoRecepcionDto extends EntityResponse {

    @SerializedName("fecha")
    @TypeConverters({TimestampConverter.class})
    public Date Fecha;
    @SerializedName("secuencial")
    public String Secuencial;
    @SerializedName("ubicacionId")
    public String UbicacionId;
    @SerializedName("ubicacionDestinoId")
    public String UbicacionDestinoId;
    @SerializedName("estado")
    public int Estado;
    @SerializedName("fechaEnvio")
    @TypeConverters({TimestampConverter.class})
    public Date FechaEnvio;
    @SerializedName("fechaRecepcion")
    @TypeConverters({TimestampConverter.class})
    public Date FechaRecepcion;

    @Ignore
    @SerializedName("ordenDespachoRecepcionDetalles")
    public List<OrdenDespachoRecepcionDetalleDto> OrdenDespachoRecepcionDetalles;

    @Ignore
    @SerializedName("ordenDespachoRecepcionRfids")
    public List<OrdenDespachoRecepcionRfidDto> OrdenDespachoRecepcionRfids;

    @Ignore
    @Override
    public String toString() {
        return Secuencial;
    }

    public OrdenDespachoRecepcionDto() {
       OrdenDespachoRecepcionRfids = new ArrayList<>();
       OrdenDespachoRecepcionDetalles = new ArrayList<>();
    }
}
