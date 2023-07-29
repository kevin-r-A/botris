package com.csl.cs108ademoapp;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.csl.cs108ademoapp.Entities.Repository.MovimientoProductoRepository;
import com.csl.cs108ademoapp.Entities.Repository.OrdenDespachoRecepcionRepository;
import com.csl.cs108ademoapp.Entities.Repository.OrdenDespachoRecepcionRfidRepository;
import com.csl.cs108ademoapp.Entities.Repository.ProductoRepository;
import com.csl.cs108ademoapp.Entities.Repository.UbicacionRepository;
import com.csl.cs108ademoapp.Generales.Sucursal;
import com.csl.cs108ademoapp.Web.Repository.WebServiceRepository;
import com.csl.cs108ademoapp.Web.Responses.MovimientoProductoDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionRfidDto;
import com.csl.cs108ademoapp.Web.Responses.UbicacionDto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainViewModel extends AndroidViewModel {
    private final ProductoRepository productoRepository;
    private final UbicacionRepository ubicacionRepository;
    private final MovimientoProductoRepository movimientoProductoRepository;
    private final WebServiceRepository webServiceRepository;
    private final OrdenDespachoRecepcionRepository ordenDespachoRecepcionRepository;
    private final OrdenDespachoRecepcionRfidRepository ordenDespachoRecepcionRfidRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);
        productoRepository = new ProductoRepository(application);
        productoRepository.Download(true);
        ubicacionRepository = new UbicacionRepository(application);
        ubicacionRepository.Download(true);
        movimientoProductoRepository = new MovimientoProductoRepository(application);
        webServiceRepository = new WebServiceRepository(application);
        ordenDespachoRecepcionRepository = new OrdenDespachoRecepcionRepository(application);
        ordenDespachoRecepcionRfidRepository = new OrdenDespachoRecepcionRfidRepository(application);
    }

    public static MainViewModel getInstance(Application application) {
        return new MainViewModel(application);
    }

    public boolean updateServer() {
        productoRepository.Download(false);
        return true;
    }

    public LiveData<List<Sucursal>> getSucursales() {
        return ubicacionRepository.GetSucursales();
    }

    public List<UbicacionDto> getBodegas(String codigoSucursal) {
        return ubicacionRepository.GetBodegas(codigoSucursal);
    }

    public void sendMovimientoProducto() throws Exception {
        try {
            List<MovimientoProductoDto> unSend = movimientoProductoRepository.getUnSend();
            if (unSend.size() > 0) {
                unSend = webServiceRepository.PostManyMovimientoProducto(unSend);
                for (MovimientoProductoDto productoDto : unSend) {
                    productoDto.Enviado = false;
                }
                movimientoProductoRepository.UpdateAllAsync(unSend);
            }

            List<String> unSendDelete = movimientoProductoRepository.getUnSendDelete();
            if (unSendDelete.size() > 0) {
                webServiceRepository.DeleteManyMovimientoProducto(unSendDelete);
                movimientoProductoRepository.UpdateDeleteSend(unSendDelete);
            }

        } catch (Exception exception) {
            throw exception;
        }
    }

    public void sendDespachos() throws Exception {
        try {
            List<OrdenDespachoRecepcionDto> ordenDespachoRecepcionDtos = ordenDespachoRecepcionRepository.GetAllUnSend();
            List<OrdenDespachoRecepcionRfidDto> collect = new ArrayList<>();
            if (ordenDespachoRecepcionDtos.size() > 0) {
                for (OrdenDespachoRecepcionDto ordenDespachoRecepcionDto : ordenDespachoRecepcionDtos) {
                    if (ordenDespachoRecepcionDto.Secuencial != null && !ordenDespachoRecepcionDto.Secuencial.equals("")) {
                        if (webServiceRepository.PostOrderWithRfid(ordenDespachoRecepcionDto) != null) {
                            ordenDespachoRecepcionDto.OrdenDespachoRecepcionRfids.forEach(x -> x.Enviar = false);
                            collect.addAll(ordenDespachoRecepcionDto.OrdenDespachoRecepcionRfids);
                        }
                    } else if (ordenDespachoRecepcionDto.Secuencial == null || ordenDespachoRecepcionDto.Secuencial.equals("")) {
                        if (webServiceRepository.PostNewOrderWithRfid(ordenDespachoRecepcionDto) != null) {
                            ordenDespachoRecepcionDto.OrdenDespachoRecepcionRfids.forEach(x -> x.Enviar = false);
                            collect.addAll(ordenDespachoRecepcionDto.OrdenDespachoRecepcionRfids);
                        }
                    }
                }
                ordenDespachoRecepcionRfidRepository.UpdateAllSync(collect);
            }
        } catch (Exception exception) {
            throw exception;
        }
    }

    public void getDespachosRecepcion(String ubicacionId) {
        ordenDespachoRecepcionRepository.Download(true, ubicacionId);
    }

    public void getMovimientoProductos(String ubicacionId) {
        movimientoProductoRepository.Download(true, ubicacionId);
    }
}
