package com.csl.cs108ademoapp.Convertidores;

import android.arch.persistence.room.TypeConverter;

import com.csl.cs108ademoapp.Entities.Enums.EstadoType;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class MarcasTypeConverter {
    @TypeConverter
    public static List<String> fromMarcasType(String value) {
        Gson gson = new Gson();
        return (List<String>) gson.fromJson(value, ArrayList.class);

    }

    @TypeConverter
    public static String toMarcasType(List<String> value) {
        Gson gson = new Gson();
        return gson.toJson(value);
    }
}
