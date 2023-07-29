package com.csl.cs108ademoapp.Entities.Clases;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.csl.cs108ademoapp.Convertidores.TimestampConverter;

import java.util.Date;

@Entity
public class REPORTE {
    @PrimaryKey(autoGenerate = true)
    public int REP_CODIGO;
    public String REP_CODBARRAS;
    public Integer CUS_ID;
    @TypeConverters({TimestampConverter.class})
    public Date REP_FECHA;
    public String REP_ESTADO;
    public Integer ACT_ID;
    public String REP_USUARIO;

}
