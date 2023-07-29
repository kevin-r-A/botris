package com.csl.cs108ademoapp.fragments.DespachosNuevos;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.csl.cs108ademoapp.Entities.Repository.MovimientoProductoRepository;
import com.csl.cs108ademoapp.Entities.Repository.OrdenDespachoRecepcionRepository;
import com.csl.cs108ademoapp.Entities.Repository.UbicacionRepository;
import com.csl.cs108ademoapp.Generales.Sucursal;
import com.csl.cs108ademoapp.MainActivity;
import com.csl.cs108ademoapp.Web.Responses.MovimientoProductoDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionDto;
import com.csl.cs108ademoapp.Web.Responses.UbicacionDto;
import com.csl.cs108ademoapp.fragments.Despachos.DespachoViewModel;

import java.util.List;

public class DespachosNuevosViewModel extends AndroidViewModel {
    private final UbicacionRepository ubicacionRepository;
    private final MovimientoProductoRepository movimientoProductoRepository;
    private final OrdenDespachoRecepcionRepository ordenDespachoRecepcionRepository;
    private final MutableLiveData<MovimientoProductoDto> codigoValido = new MutableLiveData<>();
    private final MutableLiveData<MovimientoProductoDto> codigoNoValido = new MutableLiveData<>();
    private final MutableLiveData<MovimientoProductoDto> codigoDespachado = new MutableLiveData<>();

    public DespachosNuevosViewModel(@NonNull Application application) {
        super(application);
        ubicacionRepository = new UbicacionRepository(application);
        movimientoProductoRepository = new MovimientoProductoRepository(application);
        ordenDespachoRecepcionRepository = new OrdenDespachoRecepcionRepository(application);
    }

    public MutableLiveData<MovimientoProductoDto> getCodigoValido() {
        return codigoValido;
    }

    public MutableLiveData<MovimientoProductoDto> getCodigoNoValido() {
        return codigoNoValido;
    }

    public MutableLiveData<MovimientoProductoDto> getCodigoDespachado() {
        return codigoDespachado;
    }

    public static DespachosNuevosViewModel getInstance(Application application) {
        return new DespachosNuevosViewModel(application);
    }

    public LiveData<List<Sucursal>> getSucursales() {
        return ubicacionRepository.GetSucursales();
    }

    public List<UbicacionDto> getBodegas(String codigoSucursal) {
        return ubicacionRepository.GetBodegas(codigoSucursal);
    }

    public void BuscarCodigo(final String rfid) {
        //
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
            MainActivity.mCs108Library4a.appendToLog("RFID: No Encontrado");
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
        return ordenDespachoRecepcionRepository.InsertSync(item);
    }
}