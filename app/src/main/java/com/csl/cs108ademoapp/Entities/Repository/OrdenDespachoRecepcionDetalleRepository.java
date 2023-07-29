package com.csl.cs108ademoapp.Entities.Repository;

import android.app.Application;

import com.csl.cs108ademoapp.Entities.DAO.OrdenDespachoRecepcionDetalle_DAO;
import com.csl.cs108ademoapp.Entities.POJO.OrdenDespachoRecepcionDetalleWithNavigation;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionDetalleDto;

import java.util.List;

public class OrdenDespachoRecepcionDetalleRepository extends EntityRepository<OrdenDespachoRecepcionDetalleDto, OrdenDespachoRecepcionDetalle_DAO> {
    public OrdenDespachoRecepcionDetalleRepository(Application application) {
        super(application);
        entityDao = db.ordenDespachoRecepcionDetalle_dao();
    }

    public List<OrdenDespachoRecepcionDetalleWithNavigation> GetDetails(String ordenId ){
        return entityDao.GetDetails(ordenId);
    }

    public List<OrdenDespachoRecepcionDetalleWithNavigation> GetDetailsRecepcion(String ordenId ){
        return entityDao.GetDetailsRecepcion(ordenId);
    }
}
