package com.csl.cs108ademoapp.Entities.DAO;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.csl.cs108ademoapp.Generales.Sucursal;
import com.csl.cs108ademoapp.Web.Responses.ProductoDto;
import com.csl.cs108ademoapp.Web.Responses.UbicacionDto;

import java.util.List;

@Dao
public interface Ubicacion_DAO extends EntityDao<UbicacionDto> {

    @Query("Select CodigoSucursal, Sucursal from UbicacionDto group by CodigoSucursal, Sucursal")
    LiveData<List<Sucursal>> GetSucursales();

    @Query("Select * from UbicacionDto where CodigoSucursal =:codigoSucursal")
    List<UbicacionDto> GetBodegasBySucursal(String codigoSucursal);

    @Override
    @Query("Select * from UbicacionDto")
    List<UbicacionDto> GetAll();
}
