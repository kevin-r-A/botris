package com.csl.cs108ademoapp.Entities.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.csl.cs108ademoapp.Web.Responses.ProductoDto;

import java.util.List;

@Dao
public interface Producto_DAO extends EntityDao<ProductoDto> {

    @Override
    @Query("Select * from ProductoDto")
    List<ProductoDto> GetAll();

    @Query("Select * from ProductoDto where CodBarras=:codigoBarras LIMIT 1")
    ProductoDto findByCodigoBarras(String codigoBarras);
}
