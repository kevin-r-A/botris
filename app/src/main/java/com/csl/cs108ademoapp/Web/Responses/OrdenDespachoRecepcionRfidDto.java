package com.csl.cs108ademoapp.Web.Responses;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static android.arch.persistence.room.ForeignKey.NO_ACTION;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.TypeConverters;

import com.csl.cs108ademoapp.Convertidores.TimestampConverter;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Entity(foreignKeys = @ForeignKey(entity = OrdenDespachoRecepcionDto.class,
        parentColumns = "Id", childColumns = "OrdenDespachoRecepcionId", onDelete = NO_ACTION))
public class OrdenDespachoRecepcionRfidDto extends EntityResponse {
    @SerializedName("ordenDespachoRecepcionId")
    public String OrdenDespachoRecepcionId;
    @SerializedName("codBarras")
    public String CodBarras;
    @SerializedName("codRfid")
    public String CodRfid;
    @SerializedName("despacho")
    public boolean Despacho;
    @SerializedName("recepcion")
    public boolean Recepcion;
    public boolean Enviar;
    @TypeConverters({TimestampConverter.class})
    @SerializedName("fechaDespacho")
    public Date FechaDespacho;
    @TypeConverters({TimestampConverter.class})
    @SerializedName("fechaRecepcion")
    public Date FechaRecepcion;
    @SerializedName("secuencialDespacho")
    public String SecuencialDespacho;
    @SerializedName("secuencialRecepcion")
    public String SecuencialRecepcion;

}
