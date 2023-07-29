package com.csl.cs108ademoapp.Web.Responses;

import android.os.Parcel;

import java.io.Serializable;

public class ResponseToken implements Serializable {
    public String access_token;
    public long expires_in;
    public String token_type;
    public String scope;
    public String tenant;

    protected ResponseToken(Parcel in) {
        access_token = in.readString();
        expires_in = in.readLong();
        token_type = in.readString();
        scope = in.readString();
        tenant = in.readString();
    }
}
