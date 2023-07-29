package com.csl.cs108ademoapp.Web.Responses;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.TypeConverters;

import com.csl.cs108ademoapp.Convertidores.InventarioTypeConverter;
import com.csl.cs108ademoapp.Convertidores.TimestampConverter;
import com.csl.cs108ademoapp.Entities.Enums.InventarioType;
import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

@Entity
public class InventarioDto extends EntityResponse {
    @SerializedName("fecha")
    @TypeConverters({TimestampConverter.class})
    public Date Fecha;
    @SerializedName("ubicacionId")
    public String UbicacionId;
    @SerializedName("codBarra")
    public String CodBarra;
    @SerializedName("cantidadSistema")
    public float CantidadSistema;
    @SerializedName("cantidadFisica")
    public float CantidadFisica;
    @SerializedName("estado")
    public int Estado;
    @SerializedName("identificador")
    public String Identificador;
    @SerializedName("productoId")
    public String ProductoId;
    @SerializedName("type")
    @TypeConverters({InventarioTypeConverter.class})
    public InventarioType Type;

    @SerializedName("sku")
    @Ignore
    public String Sku;

    @Ignore
    public ProductoDto Producto;
    @SerializedName("inventarioDetalleRfids")
    @Ignore
    public List<InventarioDetalleRfidDto> InventarioDetalleRfids;
}
