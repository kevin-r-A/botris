package com.csl.cs108ademoapp.fragments.Asignacion;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.csl.cs108ademoapp.Entities.Repository.ProductoRepository;
import com.csl.cs108ademoapp.Web.Repository.WebServiceRepository;
import com.csl.cs108ademoapp.Web.Responses.AssignTagsRfidViewModelDto;
import com.csl.cs108ademoapp.Web.Responses.ProductoDto;

import java.util.ArrayList;
import java.util.List;

public class AsignacionViewModel extends AndroidViewModel {
    private final WebServiceRepository webServiceRepository;
    private final ProductoRepository productoRepository;
    private MutableLiveData<List<ProductoDto>> productoMutableLiveData = new MutableLiveData<>();

    public AsignacionViewModel(@NonNull Application application) {
        super(application);
        webServiceRepository = new WebServiceRepository(application);
        productoRepository = new ProductoRepository(application);
    }

    public static AsignacionViewModel getInstance(Application application) {
        return new AsignacionViewModel(application);
    }


    public MutableLiveData<List<ProductoDto>> getProductoMutableLiveData() {
        return productoMutableLiveData;
    }

    public void loadProductos(final String bloqueId) {
        if (bloqueId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    /*List<ProductoDto> result = productoRepository.GetByBloque(bloqueId);
                    productoMutableLiveData.postValue(result);*/
                }
            }).start();
        } else {
            productoMutableLiveData.setValue(new ArrayList<ProductoDto>());
        }

    }

    public boolean asignarTags(final AssignTagsRfidViewModelDto modelDto) {
        final boolean[] result = {false};
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                result[0] = webServiceRepository.PostAssignTags(modelDto);
            }
        });
        t.start();
        try {
            t.join();
            if (result[0]) {
                return true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }
}