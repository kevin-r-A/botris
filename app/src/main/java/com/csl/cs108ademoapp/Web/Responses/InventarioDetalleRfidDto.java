package com.csl.cs108ademoapp.Web.Responses;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static android.arch.persistence.room.ForeignKey.NO_ACTION;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.TypeConverters;

import com.csl.cs108ademoapp.Convertidores.TimestampConverter;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

@Entity(foreignKeys = @ForeignKey(entity = InventarioDto.class,
        parentColumns = "Id", childColumns = "InventarioId", onDelete = CASCADE))
public class InventarioDetalleRfidDto extends EntityResponse {
    @SerializedName("inventarioId")
    public String InventarioId;
    @SerializedName("codRfid")
    public String CodRfid;
    @SerializedName("fecha")
    @TypeConverters({TimestampConverter.class})
    public Date Fecha;
    @SerializedName("ubicacionSistemaId")
    public String UbicacionSistemaId;
}
