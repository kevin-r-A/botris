package com.csl.cs108ademoapp.Entities.Repository;

import android.app.Application;

import com.csl.cs108ademoapp.Entities.DAO.OrdenDespachoRecepcionRfid_DAO;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionRfidDto;

import java.util.List;

public class OrdenDespachoRecepcionRfidRepository extends EntityRepository<OrdenDespachoRecepcionRfidDto, OrdenDespachoRecepcionRfid_DAO> {
    public OrdenDespachoRecepcionRfidRepository(Application application) {
        super(application);
        entityDao = db.ordenDespachoRecepcionRfid_dao();
    }

    public List<OrdenDespachoRecepcionRfidDto> GetDetailsRfid(String ordenId, String codBarras) {
        return entityDao.GetDetailsRfif(ordenId, codBarras);
    }
}
