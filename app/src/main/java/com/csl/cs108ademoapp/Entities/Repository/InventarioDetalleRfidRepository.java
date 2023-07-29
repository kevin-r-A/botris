package com.csl.cs108ademoapp.Entities.Repository;

import android.app.Application;

import com.csl.cs108ademoapp.Entities.DAO.InventarioDetalleRfid_DAO;
import com.csl.cs108ademoapp.Web.Responses.InventarioDetalleRfidDto;

public class InventarioDetalleRfidRepository extends EntityRepository<InventarioDetalleRfidDto, InventarioDetalleRfid_DAO>{
    public InventarioDetalleRfidRepository(Application application) {
        super(application);
        entityDao = db.inventarioDetalleRfid_dao();
    }
}
