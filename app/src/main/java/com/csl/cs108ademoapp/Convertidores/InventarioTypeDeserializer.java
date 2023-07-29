package com.csl.cs108ademoapp.Convertidores;

import com.csl.cs108ademoapp.Entities.Enums.InventarioType;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class InventarioTypeDeserializer implements
        JsonDeserializer<InventarioType> {
    @Override
    public InventarioType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        int typeInt = json.getAsInt();
        for (InventarioType inventarioType : InventarioType.values()) {
            if (inventarioType.ordinal() == typeInt){
                return inventarioType;
            }
        }
        return null;
    }
}
