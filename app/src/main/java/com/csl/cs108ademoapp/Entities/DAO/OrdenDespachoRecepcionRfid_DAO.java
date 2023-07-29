package com.csl.cs108ademoapp.Entities.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionRfidDto;

import java.util.List;

@Dao
public interface OrdenDespachoRecepcionRfid_DAO extends EntityDao<OrdenDespachoRecepcionRfidDto> {
    @Override
    @Query("Select * from OrdenDespachoRecepcionRfidDto")
    List<OrdenDespachoRecepcionRfidDto> GetAll();

    @Query("select * from ordendespachorecepcionrfiddto where OrdenDespachoRecepcionId =:ordenId and CodBarras=:codBarras")
    List<OrdenDespachoRecepcionRfidDto> GetDetailsRfif(String ordenId, String codBarras);
}
