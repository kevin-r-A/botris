package com.csl.cs108ademoapp.Entities.POJO;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionRfidDto;

import java.util.List;

public class OrdenDespachoRecepcionWithRfid {
    @Embedded
    public OrdenDespachoRecepcionDto ordenDespachoRecepcionDto;

    @Relation(parentColumn = "Id", entityColumn = "OrdenDespachoRecepcionId")
    public List<OrdenDespachoRecepcionRfidDto> ordenDespachoRecepcionRfidDtos;
}
