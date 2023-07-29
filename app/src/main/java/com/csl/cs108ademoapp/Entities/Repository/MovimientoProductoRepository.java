package com.csl.cs108ademoapp.Entities.Repository;

import android.app.Application;

import com.csl.cs108ademoapp.Entities.DAO.MovimientoProducto_DAO;
import com.csl.cs108ademoapp.Entities.ViewModels.InventarioCruceViewModel;
import com.csl.cs108ademoapp.Web.Responses.MovimientoProductoDto;

import java.util.ArrayList;
import java.util.List;

public class MovimientoProductoRepository extends EntityRepository<MovimientoProductoDto, MovimientoProducto_DAO> {
    public List<String> stringList;

    public MovimientoProductoRepository(Application application) {
        super(application);
        entityDao = db.movimientoProducto_dao();
    }

    public void Download(boolean async, String ubicacionId) {
        super.Download("GetMovimientoProductoByUbicacion", async, ubicacionId);
    }

    public List<MovimientoProductoDto> getUnSend() {
        entityList = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                entityList = entityDao.GetUnSendNew();
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

    public List<String> getUnSendDelete() {
        stringList = new ArrayList<>();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                stringList = entityDao.GetUnSendDelete();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return stringList;
    }

    public void UpdateDeleteSend(final List<String> ids) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                entityDao.UpdateDeleteSend(ids);
            }
        }).start();
    }

    public MovimientoProductoDto SearchByRfid(final String rfid) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                entity = entityDao.SearchByRfid(rfid);
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return entity;
    }

    public MovimientoProductoDto SearchByRfid(final String rfid, final String ubicacionId) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                entity = entityDao.SearchByRfid(rfid, ubicacionId);
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return entity;
    }

    public MovimientoProductoDto SearchByRfidSYNC(final String rfid, final String ubicacionId) {
        return entityDao.SearchByRfid(rfid, ubicacionId);
    }

    public List<InventarioCruceViewModel> GetInventorioCruce() {
        return entityDao.getInventarioCruce();
    }
}
