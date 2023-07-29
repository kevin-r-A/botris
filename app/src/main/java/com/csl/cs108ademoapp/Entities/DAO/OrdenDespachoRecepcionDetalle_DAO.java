package com.csl.cs108ademoapp.Entities.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.csl.cs108ademoapp.Entities.POJO.OrdenDespachoRecepcionDetalleWithNavigation;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionDetalleDto;

import java.util.List;

@Dao
public interface OrdenDespachoRecepcionDetalle_DAO extends EntityDao<OrdenDespachoRecepcionDetalleDto>{
    @Override
    @Query("Select * from OrdenDespachoRecepcionDetalleDto")
    List<OrdenDespachoRecepcionDetalleDto> GetAll();

    @Query("Select ord.*, pr.Nombre as producto from OrdenDespachoRecepcionDetalleDto ord left join productodto pr on ord.ProductoId = pr.Id where OrdenDespachoRecepcionId=:ordenId")
    List<OrdenDespachoRecepcionDetalleWithNavigation> GetDetails(String ordenId);

    @Query("Select ord.*, pr.Nombre as producto from OrdenDespachoRecepcionDetalleDto ord left join productodto pr on ord.ProductoId = pr.Id where OrdenDespachoRecepcionId=:ordenId and CantidadDespachada>0")
    List<OrdenDespachoRecepcionDetalleWithNavigation> GetDetailsRecepcion(String ordenId);

    @Query("Select ord.*, pr.Nombre as producto from OrdenDespachoRecepcionDetalleDto ord inner join productodto pr on ord.OrdenDespachoRecepcionId = pr.Id")
    List<OrdenDespachoRecepcionDetalleWithNavigation> Test();
}
