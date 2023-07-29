package com.csl.cs108ademoapp.Entities.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.csl.cs108ademoapp.Web.Responses.MercaderiaDto;
import com.csl.cs108ademoapp.Web.Responses.MovimientoProductoDto;

import java.util.List;

@Dao
public interface Mercaderia_DAO extends EntityDao<MercaderiaDto>{

    @Override
    @Query("Select * from MercaderiaDto order by ART_CODIGO")
    List<MercaderiaDto> GetAll();

    @Query("Select * from MercaderiaDto where (ART_CODIGO like '%' || :data || '%') OR (ART_NOMBRE like '%' || :data || '%') order by ART_CODIGO")
    List<MercaderiaDto> GetAllFiltered(String data);

}
