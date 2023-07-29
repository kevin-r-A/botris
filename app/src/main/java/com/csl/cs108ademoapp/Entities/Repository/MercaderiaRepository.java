package com.csl.cs108ademoapp.Entities.Repository;

import android.app.Application;

import com.csl.cs108ademoapp.Entities.DAO.Mercaderia_DAO;
import com.csl.cs108ademoapp.Web.Responses.MercaderiaDto;

import java.util.ArrayList;
import java.util.List;

public class MercaderiaRepository extends EntityRepository<MercaderiaDto, Mercaderia_DAO>{
    public MercaderiaRepository(Application application) {
        super(application);
        entityDao = db.mercaderia_dao();
    }

    public List<MercaderiaDto> GetAllFiltered(String data) {
        final List<MercaderiaDto>[] result = new List[]{new ArrayList<>()};
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                result[0] = entityDao.GetAllFiltered(data);
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result[0];
    }
}
