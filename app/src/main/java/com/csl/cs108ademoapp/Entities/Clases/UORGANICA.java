package com.csl.cs108ademoapp.Entities.Clases;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class UORGANICA {
    @PrimaryKey(autoGenerate = true)
    public int UOR_ID;
    public String UOR_NOMBRE;
    public int UOR_PADRE;
    public int UOR_NIVEL;
    public Integer UGE_ID;
    public String UOR_CODIGO;
    public String UOR_READERIP;
    public String UOR_READERNOMBRE;
}
