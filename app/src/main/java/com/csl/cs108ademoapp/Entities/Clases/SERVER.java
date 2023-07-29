package com.csl.cs108ademoapp.Entities.Clases;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class SERVER {
    @PrimaryKey
    public int SER_ID;
    public String URL;
    public String PPC_ID;
}
