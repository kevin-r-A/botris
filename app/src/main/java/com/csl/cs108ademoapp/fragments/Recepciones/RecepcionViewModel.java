package com.csl.cs108ademoapp.fragments.Recepciones;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.csl.cs108ademoapp.Entities.POJO.OrdenDespachoRecepcionDetalleWithNavigation;
import com.csl.cs108ademoapp.Entities.Repository.MovimientoProductoRepository;
import com.csl.cs108ademoapp.Entities.Repository.OrdenDespachoRecepcionDetalleRepository;
import com.csl.cs108ademoapp.Entities.Repository.OrdenDespachoRecepcionRepository;
import com.csl.cs108ademoapp.Entities.Repository.OrdenDespachoRecepcionRfidRepository;
import com.csl.cs108ademoapp.Web.Repository.WebServiceRepository;
import com.csl.cs108ademoapp.Web.Responses.MovimientoProductoDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionDto;
import com.csl.cs108ademoapp.fragments.Despachos.DespachoViewModel;

import java.util.List;

public class RecepcionViewModel extends AndroidViewModel {

    private final WebServiceRepository webServiceRepository;
    private final OrdenDespachoRecepcionRepository ordenDespachoRecepcionRepository;
    private final OrdenDespachoRecepcionDetalleRepository ordenDespachoRecepcionDetalleRepository;
    private final MovimientoProductoRepository movimientoProductoRepository;
    private final OrdenDespachoRecepcionRfidRepository ordenDespachoRecepcionRfidRepository;
    private MutableLiveData<List<OrdenDespachoRecepcionDetalleWithNavigation>> detalleMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<MovimientoProductoDto> codigoValido = new MutableLiveData<>();
    private MutableLiveData<MovimientoProductoDto> codigoNoValido = new MutableLiveData<>();
    private MutableLiveData<MovimientoProductoDto> codigoDespachado = new MutableLiveData<>();

    public RecepcionViewModel(@NonNull Application application) {
        super(application);
        webServiceRepository = new WebServiceRepository(application);
        ordenDespachoRecepcionRepository = new OrdenDespachoRecepcionRepository(application);
        ordenDespachoRecepcionDetalleRepository = new OrdenDespachoRecepcionDetalleRepository(application);
        ordenDespachoRecepcionRfidRepository = new OrdenDespachoRecepcionRfidRepository(application);
        movimientoProductoRepository = new MovimientoProductoRepository(application);
    }

    public MutableLiveData<List<OrdenDespachoRecepcionDetalleWithNavigation>> getDetalleMutableLiveData() {
        return detalleMutableLiveData;
    }

    public static RecepcionViewModel getInstance(Application application) {
        return new RecepcionViewModel(application);
    }

    public LiveData<List<OrdenDespachoRecepcionDto>> GetRecepcionByUbicacionLiveData(String ubicacionId) {
        return ordenDespachoRecepcionRepository.GetRecepcionByUbicacionLiveData(ubicacionId);
    }

    public void GetDetalle(final String ordenId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<OrdenDespachoRecepcionDetalleWithNavigation> ordenDespachoRecepcionDetalleDtos = ordenDespachoRecepcionDetalleRepository.GetDetailsRecepcion(ordenId);
                ordenDespachoRecepcionDetalleDtos.forEach(x -> {
                    x.ordenDespachoRecepcionRfidDtos = ordenDespachoRecepcionRfidRepository.GetDetailsRfid(ordenId, x.ordenDespachoRecepcionDetalleDto.CodBarras);
                });
                detalleMutableLiveData.postValue(ordenDespachoRecepcionDetalleDtos);
            }
        }).start();
    }

    public boolean SaveDespacho(OrdenDespachoRecepcionDto item) {
        return ordenDespachoRecepcionRepository.UpdateSync(item);
    }
}