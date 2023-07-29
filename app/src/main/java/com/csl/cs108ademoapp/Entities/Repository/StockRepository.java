package com.csl.cs108ademoapp.Entities.Repository;

import android.app.Application;

import com.csl.cs108ademoapp.Entities.Clases.STOCK;
import com.csl.cs108ademoapp.Entities.DAO.Stock_Dao;

public class StockRepository extends EntityRepository<STOCK, Stock_Dao>{
    public StockRepository(Application application) {
        super(application);
        entityDao = db.stock_dao();
    }

    public void DeleteInvalidosSYN(String ubicacionId){
        entityDao.DeleteInvalidos(ubicacionId);
    }

    public void EnableAllSYN() {
        entityDao.EnableAllSYN();
    }
}
