package com.csl.cs108ademoapp.Entities.Clases;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class GRUPO {
    @PrimaryKey(autoGenerate = true)
    public int GRU_ID;
    public String GRU_NOMBRE;
    public int GRU_PADRE;
    public int GRU_NIVEL;
    public String GRU_CODIGO;
    public String GRU_CTA1;
    public String GRU_CTA2;
    public String GRU_CTA3;
}
