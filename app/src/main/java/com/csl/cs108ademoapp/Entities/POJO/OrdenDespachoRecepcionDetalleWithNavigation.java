package com.csl.cs108ademoapp.Entities.POJO;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;

import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionDetalleDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionRfidDto;
import com.csl.cs108ademoapp.Web.Responses.ProductoDto;

import java.util.ArrayList;
import java.util.List;

public class OrdenDespachoRecepcionDetalleWithNavigation {
    public OrdenDespachoRecepcionDetalleWithNavigation() {
        ordenDespachoRecepcionRfidDtos = new ArrayList<>();
    }

    @Embedded
    public OrdenDespachoRecepcionDetalleDto ordenDespachoRecepcionDetalleDto;

    @Ignore
    public List<OrdenDespachoRecepcionRfidDto> ordenDespachoRecepcionRfidDtos;

    public String producto;
}
