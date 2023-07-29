package com.csl.cs108ademoapp.Convertidores;

import android.arch.persistence.room.TypeConverter;

import com.csl.cs108ademoapp.Entities.Enums.EstadoType;

public class EstadoTypeConverter {
    @TypeConverter
    public static EstadoType fromEstadoType(int value) {
        return EstadoType.valueOf(value + "");
    }

    @TypeConverter
    public static int toEstadoType(EstadoType value) {
        return value.ordinal();
    }
}
