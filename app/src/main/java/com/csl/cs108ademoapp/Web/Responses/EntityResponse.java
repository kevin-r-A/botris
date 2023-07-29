package com.csl.cs108ademoapp.Web.Responses;

import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class EntityResponse {
    @PrimaryKey(autoGenerate = false)
    @SerializedName("id")
    @NonNull
    public String Id;

    public String getId() {
        return Id;
    }
}
