package com.csl.cs108ademoapp.Entities.Repository;

import android.app.Application;

import com.csl.cs108ademoapp.Entities.DAO.Producto_DAO;
import com.csl.cs108ademoapp.Web.Responses.ProductoDto;

import java.util.ArrayList;
import java.util.List;

public class ProductoRepository extends EntityRepository<ProductoDto, Producto_DAO> {

    public ProductoRepository(Application application) {
        super(application);
        entityDao = db.producto_dao();
    }

    public void Download(boolean async) {
        super.Download1("GetProducto", async, false, null);
    }

    public ProductoDto findByCodigoBarras(final String codigoBarras) {
        entity = new ProductoDto();
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                entity = entityDao.findByCodigoBarras(codigoBarras);
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
    public ProductoDto findByCodigoBarrasSYNC(final String codigoBarras) {
        return entityDao.findByCodigoBarras(codigoBarras);
    }

}
