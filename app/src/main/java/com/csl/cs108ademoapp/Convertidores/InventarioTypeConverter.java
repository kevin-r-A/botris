package com.csl.cs108ademoapp.Convertidores;

import android.arch.persistence.room.TypeConverter;

import com.csl.cs108ademoapp.Entities.Enums.EstadoType;
import com.csl.cs108ademoapp.Entities.Enums.InventarioType;

public class InventarioTypeConverter {
    @TypeConverter
    public static InventarioType fromInventarioType(int value) {
        for (InventarioType inventarioType : InventarioType.values()) {
            if (inventarioType.ordinal() == value){
                return inventarioType;
            }
        }
        return null;
    }

    @TypeConverter
    public static int toInventarioType(InventarioType value) {
        return value.ordinal();
    }
}
