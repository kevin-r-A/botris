package com.csl.cs108ademoapp.Entities.Repository;

import android.app.Application;

import com.csl.cs108ademoapp.Entities.DAO.Inventario_DAO;
import com.csl.cs108ademoapp.Web.Responses.InventarioDto;

public class InventarioRepository extends EntityRepository<InventarioDto, Inventario_DAO>{
    public InventarioRepository(Application application) {
        super(application);
        entityDao = db.inventario_dao();
    }
}
