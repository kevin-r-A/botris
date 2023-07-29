package com.csl.cs108ademoapp.Entities.Clases;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class STOCK {
    @PrimaryKey(autoGenerate = true)
    public int STO_ID;
    public String STO_CODIGO;
    public String STO_ESTADO;

    @Override
    public String toString() {
        return STO_CODIGO;
    }

}
