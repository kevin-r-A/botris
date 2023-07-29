package com.csl.cs108ademoapp.fragments.Inventario;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.csl.cs108ademoapp.Entities.Clases.STOCK;
import com.csl.cs108ademoapp.Entities.Repository.InventarioRepository;
import com.csl.cs108ademoapp.Entities.Repository.MovimientoProductoRepository;
import com.csl.cs108ademoapp.Entities.Repository.OrdenDespachoRecepcionRfidRepository;
import com.csl.cs108ademoapp.Entities.Repository.ProductoRepository;
import com.csl.cs108ademoapp.Entities.Repository.StockRepository;
import com.csl.cs108ademoapp.MainActivity;
import com.csl.cs108ademoapp.Web.Repository.WebServiceRepository;
import com.csl.cs108ademoapp.Web.Responses.InventarioDetalleRfidDto;
import com.csl.cs108ademoapp.Web.Responses.InventarioDto;
import com.csl.cs108ademoapp.Web.Responses.MovimientoProductoDto;
import com.csl.cs108ademoapp.Web.Responses.ProductoDto;
import com.csl.cs108ademoapp.fragments.Despachos.DespachoViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InventarioViewModel extends AndroidViewModel {
    private final MovimientoProductoRepository movimientoProductoRepository;
    private final ProductoRepository productoRepository;
    private MutableLiveData<InventarioDto> inventarioDtoMutableLiveData = new MutableLiveData<>();
    private MutableLiveData<STOCK> stockMutableLiveData = new MutableLiveData<>();
    private final WebServiceRepository webServiceRepository;
    private final InventarioRepository inventarioRepository;
    private final StockRepository stockRepository;

    public InventarioViewModel(@NonNull Application application) {
        super(application);
        movimientoProductoRepository = new MovimientoProductoRepository(application);
        productoRepository = new ProductoRepository(application);
        webServiceRepository = new WebServiceRepository(application);
        inventarioRepository = new InventarioRepository(application);
        stockRepository = new StockRepository(application);
    }

    public static InventarioViewModel getInstance(Application application) {
        return new InventarioViewModel(application);
    }

    public MutableLiveData<InventarioDto> getInventarioDtoMutableLiveData() {
        return inventarioDtoMutableLiveData;
    }

    public MutableLiveData<STOCK> getStockMutableLiveData() {
        return stockMutableLiveData;
    }

    public void BuscarCodigo(String rfid, boolean repeat) {
        MovimientoProductoDto productoDto = movimientoProductoRepository.SearchByRfid(rfid);
        if (productoDto != null) {
            if(MainActivity.ubicacionDto.Marcas!=null && MainActivity.ubicacionDto.Marcas.size()>0){
                if(MainActivity.ubicacionDto.Marcas.stream().noneMatch(x->x.equals(productoDto.Marca))){
                    return;
                }
            }
            InventarioDto inventarioDto = new InventarioDto();
            inventarioDto.ProductoId = productoDto.ProductoId;
            inventarioDto.CodBarra = productoDto.CodBarra;
            inventarioDto.InventarioDetalleRfids = new ArrayList<>();
            InventarioDetalleRfidDto inventarioDetalleRfidDto = new InventarioDetalleRfidDto();
            inventarioDetalleRfidDto.CodRfid = rfid;
            inventarioDetalleRfidDto.Fecha = Calendar.getInstance().getTime();
            inventarioDetalleRfidDto.UbicacionSistemaId = productoDto.UbicacionId;
            inventarioDto.InventarioDetalleRfids.add(inventarioDetalleRfidDto);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    inventarioDto.Producto = productoRepository.findByCodigoBarrasSYNC(inventarioDto.CodBarra);
                    inventarioDtoMutableLiveData.postValue(inventarioDto);
                }
            });
            t.start();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } /*else {
            if (repeat){
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        MovimientoProductoDto productoDto1 = webServiceRepository.GetByRfid(rfid);
                        if (productoDto1 != null) {
                            if(MainActivity.ubicacionDto.Marcas!=null && MainActivity.ubicacionDto.Marcas.size()>0){
                                if(MainActivity.ubicacionDto.Marcas.stream().noneMatch(x->x.equals(productoDto1.Marca))){
                                    return;
                                }
                            }
                            movimientoProductoRepository.InsertSync(productoDto1);
                            InventarioDto inventarioDto = new InventarioDto();
                            inventarioDto.ProductoId = productoDto1.ProductoId;
                            inventarioDto.CodBarra = productoDto1.CodBarra;
                            inventarioDto.InventarioDetalleRfids = new ArrayList<>();
                            InventarioDetalleRfidDto inventarioDetalleRfidDto = new InventarioDetalleRfidDto();
                            inventarioDetalleRfidDto.CodRfid = rfid;
                            inventarioDetalleRfidDto.Fecha = Calendar.getInstance().getTime();
                            inventarioDetalleRfidDto.UbicacionSistemaId = productoDto1.UbicacionId;
                            inventarioDto.InventarioDetalleRfids.add(inventarioDetalleRfidDto);
                            Thread t = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    inventarioDto.Producto = productoRepository.findByCodigoBarrasSYNC(inventarioDto.CodBarra);
                                    inventarioDtoMutableLiveData.postValue(inventarioDto);
                                }
                            });

                            t.start();
                            try {
                                t.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            BuscarCodigo(rfid, false);
                        }
                    }
                });
                t.start();
                try {
                    t.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }*/
    }

    public void ProcesarCodigo(String rfid){
        STOCK stock = new STOCK();
        stock.STO_CODIGO = rfid;
        stock.STO_ESTADO = "S";
        stockMutableLiveData.postValue(stock);
    }

    public Boolean SaveInventario(List<InventarioDto> objects) {
        List<InventarioDto> inventarioDtos = inventarioRepository.GetAll();
        if (inventarioDtos != null && inventarioDtos.size()>0){
            inventarioRepository.DeleteAllSync(inventarioDtos);
        }
        return inventarioRepository.InsertAllSync(objects);
    }

    public Boolean SaveStock(List<STOCK> objects) {
        List<STOCK> inventarioDtos = stockRepository.GetAll();
        if (inventarioDtos != null && inventarioDtos.size()>0){
            stockRepository.DeleteAllSync(inventarioDtos);
        }
        return stockRepository.InsertAllSync(objects);
    }

    public List<STOCK> GetStock() {
        return stockRepository.GetAll();
    }
}