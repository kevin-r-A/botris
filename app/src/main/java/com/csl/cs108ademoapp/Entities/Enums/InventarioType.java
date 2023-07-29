package com.csl.cs108ademoapp.Entities.Enums;

import com.google.gson.annotations.SerializedName;

public enum InventarioType {
    @SerializedName("0")
    Conciliado,
    @SerializedName("1")
    Faltante,
    @SerializedName("2")
    Sobrante
}
