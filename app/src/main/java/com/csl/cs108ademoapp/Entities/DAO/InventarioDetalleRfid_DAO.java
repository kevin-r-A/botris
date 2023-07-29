package com.csl.cs108ademoapp.Entities.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.csl.cs108ademoapp.Web.Responses.InventarioDetalleRfidDto;

import java.util.List;

@Dao
public interface InventarioDetalleRfid_DAO extends EntityDao<InventarioDetalleRfidDto> {
    @Override
    @Query("Select * from InventarioDetalleRfidDto")
    List<InventarioDetalleRfidDto> GetAll();
}
