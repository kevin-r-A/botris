package com.csl.cs108ademoapp.Entities.Repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.support.annotation.Nullable;

import com.csl.cs108ademoapp.Entities.DAO.OrdenDespachoRecepcion_DAO;
import com.csl.cs108ademoapp.Entities.Enums.EstadoType;
import com.csl.cs108ademoapp.Entities.POJO.OrdenDespachoRecepcionWithRfid;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionRfidDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OrdenDespachoRecepcionRepository extends EntityRepository<OrdenDespachoRecepcionDto, OrdenDespachoRecepcion_DAO> {
    public OrdenDespachoRecepcionRepository(Application application) {
        super(application);
        entityDao = db.ordenDespachoRecepcion_dao();
    }

    public LiveData<List<OrdenDespachoRecepcionDto>> GetDespachoByUbicacionLiveData(String ubicacionId, @Nullable Date fecha) {
        if (fecha != null) {
            return entityDao.GetDespachoByUbicacionLiveDataByFecha(ubicacionId, EstadoType.Pendiente, EstadoType.DespachoParcial, fecha);
        }
        return entityDao.GetDespachoByUbicacionLiveData(ubicacionId, EstadoType.Pendiente, EstadoType.DespachoParcial);
    }

    public LiveData<List<OrdenDespachoRecepcionDto>> GetRecepcionByUbicacionLiveData(String ubicacionId) {
        return entityDao.GetRecepcionByUbicacionLiveData(ubicacionId, EstadoType.Pendiente, EstadoType.Receptado);
    }

    public void Download(boolean async, String ubicacionId) {
        super.Download1("GetOrdenDespacho", async, false, ubicacionId);
        super.Download1("GetOrdenRecepcion", async, false, ubicacionId);
    }

    public List<OrdenDespachoRecepcionDto> GetAllUnSend() {
        return entityDao.GetAllUnSend();
    }
}
