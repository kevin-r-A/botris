package com.csl.cs108ademoapp.Entities.POJO;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.csl.cs108ademoapp.Entities.Clases.ACTIVO;
import com.csl.cs108ademoapp.Entities.Clases.GRUPO;

import java.util.List;

public class ACTIVO_POJO {
    public ACTIVO_POJO() {
    }
    @Embedded
    public ACTIVO activo;

    public String DESCRIPCION;
    public String CUSTODIO;
    public String UBICACION;
}
