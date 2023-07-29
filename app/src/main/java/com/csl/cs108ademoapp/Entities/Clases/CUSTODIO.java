package com.csl.cs108ademoapp.Entities.Clases;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class CUSTODIO {
    @PrimaryKey(autoGenerate = true)
    public int CUS_ID;
    public String CUS_CODIGO;
    public String CUS_APELLIDOS;
    public String CUS_NOMBRES;
    public String CUS_CEDULA;
    public String CUS_TELEFONOFIJO;
    public String CUS_EXT;
    public String CUS_CELULAR;
    public String CUS_FOTO;
    public String CUS_EMAIL;
    public Integer CGO_ID;
    public String CUS_ESTADO;
}
