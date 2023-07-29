package com.csl.cs108ademoapp.Entities.Repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.csl.cs108ademoapp.Entities.DAO.Ubicacion_DAO;
import com.csl.cs108ademoapp.Generales.Sucursal;
import com.csl.cs108ademoapp.Web.Responses.UbicacionDto;

import java.util.ArrayList;
import java.util.List;

public class UbicacionRepository extends EntityRepository<UbicacionDto, Ubicacion_DAO>{
    List<Sucursal> sucursalList = new ArrayList<>();
    public UbicacionRepository(Application application) {
        super(application);
        entityDao= db.ubicacion_dao();
    }

    public void Download(boolean async) {
        super.Download("GetUbicacion", async);
    }

    public LiveData<List<Sucursal>> GetSucursales(){
        return entityDao.GetSucursales();
    }

    public List<UbicacionDto> GetBodegas(final String codigoSucursal){
        entityList = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                entityList = entityDao.GetBodegasBySucursal(codigoSucursal);
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return entityList;
    }
}
