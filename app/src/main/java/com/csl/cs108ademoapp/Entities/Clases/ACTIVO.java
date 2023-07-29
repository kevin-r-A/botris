package com.csl.cs108ademoapp.Entities.Clases;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.csl.cs108ademoapp.Convertidores.TimestampConverter;

import java.util.Date;

@Entity
public class ACTIVO {
    @PrimaryKey(autoGenerate = true)
    public int ACT_ID;
    public String EMP_ID;
    @TypeConverters({TimestampConverter.class})
    public Date ACT_FECHACREACION;
    @TypeConverters({TimestampConverter.class})
    public Date ACT_FECHAINIDEPRE;
    public Boolean ACT_DEPRECIABLESRI;
    public Boolean ACT_DEPRECIABLE;
    public Boolean ACT_DEPRECIADOSRI;
    public Boolean ACT_DEPRECIADONIIF;
    @TypeConverters({TimestampConverter.class})
    public Date ACT_FECHAINIOPER;
    @TypeConverters({TimestampConverter.class})
    public Date ACT_FECHAINIDEPRENIIF;
    @TypeConverters({TimestampConverter.class})
    public Date ACT_FECHABAJA;
    public String USERNAME;
    public String ACT_TIPO;
    public String ACT_CODBARRAS;
    public String ACT_CODBARRASPADRE;
    public String ACT_CODIGO1;
    public int GRU_ID1;
    public int GRU_ID2;
    public int GRU_ID3;
    public String ACT_FOTO1;
    public String ACT_FOTO2;
    public String ACT_DOC1;
    public String ACT_DOC2;
    public int UGE_ID1;
    public int UGE_ID2;
    public int UGE_ID3;
    public int UGE_ID4;
    public int UOR_ID1;
    public int UOR_ID2;
    public int UOR_ID3;
    public int CUS_ID1;
    public int CUS_ID2;
    public int EST_ID1;
    public int EST_ID2;
    public int EST_ID3;
    public int MAR_ID;
    public int MOD_ID;
    public String ACT_SERIE1;
    public int COL_ID;
    public int ECO_ID1;
    public int ECO_ID2;
    public int PRO_ID;
    public String ACT_TIPOING;
    public double ACT_NUMFACT;
    @TypeConverters({TimestampConverter.class})
    public Date ACT_FECHACOMPRA;
    public double ACT_VALORCOMPRA;
    public int ACT_ANIO;
    public int ACT_VIDAUTIL;
    public int ACT_VIDAUTILNIIF;
    public double ACT_VALORRESIDUALNIIF;
    public Boolean ACT_GARANTIA;
    @TypeConverters({TimestampConverter.class})
    public Date ACT_FECHAGARANTIAVENCE;
    public String ACT_OBSERVACIONES;
    public Boolean ACT_TRANSFEROK;
    public int ACT_PPC;
    public String ACT_H1;
    public String ACT_H2;
    public String ACT_PE;
    public String ACT_OBSERVACIONES2;
    public int ACT_TIPOBAJA;
    public String ACT_OBSBAJA;
    public Boolean ACT_BAJAPROCESADA;
    public Boolean ACT_BAJAPROCESADASRI;
    public String ACT_RESUMEN;
    public double ACT_VALORREPO;
    public double ACT_VALORRAZONABLE;
    @TypeConverters({TimestampConverter.class})
    public Date ACT_FECHAINVENTARIO;
    public String ACT_CODRFID;
    public String ACT_STRADICIONAL3;
    public String ACT_RETIQUETAR;
    public int ACT_AUTRFID;
    public int ACT_FILRFID;
    public int ACT_TIPRFID;
    public String ACT_SEGURO;
    public int ACT_TIPOSEGURO;
    public String ACT_ESTADOMANT;
    @TypeConverters({TimestampConverter.class})
    public Date ACT_FECHAENVIOMANT;
    @TypeConverters({TimestampConverter.class})
    public Date ACT_FECHAREGRESOMANT;
    public String ACT_DESCRIPCIONLARGA;
    @TypeConverters({TimestampConverter.class})
    public Date ACT_FECHASEGUROVENCE;
    public int ACT_DIASAVISOGARANTIA;
    public int ACT_DIASAVISOMANTPREVENTIVO;
    public String ACT_ESTADO;


    public String ACT_FFACTURA;
    public String ACT_FNUM;
    public String ACT_PROVEEDOR;
    public String ACT_CODANTCLI2;
    public String ACT_MARCA;
    public String ACT_MODELO;
    public String ACT_ESTADOAC;
    public String ACT_COLOR;
    public String ACT_COMPONENTE;
    public String ACT_ESTRUCTURA;
    public String ACT_UBIC1;
    public String ACT_UBIC2;
    public String ACT_UBIC3;
    public String ACT_UBIC4;
    public String ACT_PISO;
    public String ACT_UORCOD;
    public String ACT_CUSCOD;
    public String ACT_UOR1;
    public String ACT_UOR2;
    public String ACT_GRUPO;
    public String ACT_SUBGRUPO;
}
