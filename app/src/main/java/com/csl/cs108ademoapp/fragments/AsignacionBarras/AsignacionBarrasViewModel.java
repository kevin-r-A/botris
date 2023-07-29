package com.csl.cs108ademoapp.fragments.AsignacionBarras;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.csl.cs108ademoapp.Entities.Repository.MovimientoProductoRepository;
import com.csl.cs108ademoapp.Entities.Repository.ProductoRepository;
import com.csl.cs108ademoapp.Web.Responses.MovimientoProductoDto;
import com.csl.cs108ademoapp.Web.Responses.ProductoDto;


public class AsignacionBarrasViewModel extends AndroidViewModel {
    private final ProductoRepository productoRepository;
    private final MovimientoProductoRepository movimientoProductoRepository;
    private MutableLiveData<ProductoDto> productoDtoMutableLiveData = new MutableLiveData<>();

    public AsignacionBarrasViewModel(@NonNull Application application) {
        super(application);
        productoRepository = new ProductoRepository(application);
        movimientoProductoRepository = new MovimientoProductoRepository(application);
    }

    public static AsignacionBarrasViewModel getInstance(Application application) {
        return new AsignacionBarrasViewModel(application);
    }

    public ProductoDto findProduct(String CodigoBarras) {
        return productoRepository.findByCodigoBarras(CodigoBarras);
    }

    public MutableLiveData<ProductoDto> getProductoDtoMutableLiveData() {
        return productoDtoMutableLiveData;
    }

    public void insertMovimientoProducto(MovimientoProductoDto movimientoProductoDto) {
        movimientoProductoRepository.InsertAsync(movimientoProductoDto);
    }

    public void updateMovimientoProductoDto(MovimientoProductoDto movimientoProductoDto) {
        movimientoProductoRepository.UpdateAsync(movimientoProductoDto);
    }

    public MovimientoProductoDto searchRfid(String rfid) {
        return movimientoProductoRepository.SearchByRfid(rfid);
    }
}