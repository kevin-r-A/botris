package com.csl.cs108ademoapp.fragments.Despachos;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.csl.cs108ademoapp.Entities.POJO.OrdenDespachoRecepcionDetalleWithNavigation;
import com.csl.cs108ademoapp.Entities.Repository.MovimientoProductoRepository;
import com.csl.cs108ademoapp.Entities.Repository.OrdenDespachoRecepcionDetalleRepository;
import com.csl.cs108ademoapp.Entities.Repository.OrdenDespachoRecepcionRepository;
import com.csl.cs108ademoapp.Entities.Repository.OrdenDespachoRecepcionRfidRepository;
import com.csl.cs108ademoapp.MainActivity;
import com.csl.cs108ademoapp.Web.Repository.WebServiceRepository;
import com.csl.cs108ademoapp.Web.Responses.MovimientoProductoDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionDetalleDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionRfidDto;
import com.csl.cs108ademoapp.fragments.AsignacionBarras.AsignacionBarrasViewModel;

import java.util.Date;
import java.util.List;

public class DespachoViewModel extends AndroidViewModel {
    private final WebServiceRepository webServiceRepository;
    private final OrdenDespachoRecepcionRepository ordenDespachoRecepcionRepository;
    private final OrdenDespachoRecepcionDetalleRepository ordenDespachoRecepcionDetalleRepository;
    private final MovimientoProductoRepository movimientoProductoRepository;
    private final OrdenDespachoRecepcionRfidRepository ordenDespachoRecepcionRfidRepository;
    private MutableLiveData<List<OrdenDespachoRecepcionDetalleWithNavigation>> detalleMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<MovimientoProductoDto> codigoValido = new MutableLiveData<>();
    private MutableLiveData<MovimientoProductoDto> codigoNoValido = new MutableLiveData<>();
    private MutableLiveData<MovimientoProductoDto> codigoDespachado = new MutableLiveData<>();

    public DespachoViewModel(@NonNull Application application) {
        super(application);
        webServiceRepository = new WebServiceRepository(application);
        ordenDespachoRecepcionRepository = new OrdenDespachoRecepcionRepository(application);
        ordenDespachoRecepcionDetalleRepository = new OrdenDespachoRecepcionDetalleRepository(application);
        movimientoProductoRepository = new MovimientoProductoRepository(application);
        ordenDespachoRecepcionRfidRepository = new OrdenDespachoRecepcionRfidRepository(application);
    }

    public MutableLiveData<MovimientoProductoDto> getCodigoValido() {
        return codigoValido;
    }

    public MutableLiveData<MovimientoProductoDto> getCodigoNoValido() {
        return codigoNoValido;
    }

    public MutableLiveData<List<OrdenDespachoRecepcionDetalleWithNavigation>> getDetalleMutableLiveData() {
        return detalleMutableLiveData;
    }

    public static DespachoViewModel getInstance(Application application) {
        return new DespachoViewModel(application);
    }

    public LiveData<List<OrdenDespachoRecepcionDto>> GetDespachoByUbicacionLiveData(String ubicacionId, @Nullable Date fecha) {
        return ordenDespachoRecepcionRepository.GetDespachoByUbicacionLiveData(ubicacionId, fecha);
    }

    public MutableLiveData<MovimientoProductoDto> getCodigoDespachado() {
        return codigoDespachado;
    }

    public void GetDetalle(final String ordenId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<OrdenDespachoRecepcionDetalleWithNavigation> ordenDespachoRecepcionDetalleDtos = ordenDespachoRecepcionDetalleRepository.GetDetails(ordenId);
                ordenDespachoRecepcionDetalleDtos.forEach(x -> {
                    x.ordenDespachoRecepcionRfidDtos = ordenDespachoRecepcionRfidRepository.GetDetailsRfid(ordenId, x.ordenDespachoRecepcionDetalleDto.CodBarras);
                });
                detalleMutableLiveData.postValue(ordenDespachoRecepcionDetalleDtos);
            }
        }).start();
    }

    public void BuscarCodigo(final String rfid) {
        MainActivity.mCs108Library4a.appendToLog("RFID: " + rfid);
        MovimientoProductoDto productoDto = movimientoProductoRepository.SearchByRfidSYNC(rfid, MainActivity.ubicacionDto.Id);
        if (productoDto != null) {
            if (productoDto.Estado == 1) {
                codigoDespachado.postValue(productoDto);
            } else {
                codigoValido.postValue(productoDto);
            }
        } else {
            productoDto = movimientoProductoRepository.SearchByRfid(rfid);
            if (productoDto != null) {
                codigoNoValido.postValue(productoDto);
            }
        }
        /*Thread t = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
    }

    public boolean SaveDespacho(OrdenDespachoRecepcionDto item) {
        return ordenDespachoRecepcionRepository.UpdateSync(item);
    }

}