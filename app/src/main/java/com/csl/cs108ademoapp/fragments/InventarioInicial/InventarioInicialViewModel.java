package com.csl.cs108ademoapp.fragments.InventarioInicial;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.csl.cs108ademoapp.Entities.Repository.MercaderiaRepository;
import com.csl.cs108ademoapp.MainActivity;
import com.csl.cs108ademoapp.Web.Repository.WebServiceRepository;
import com.csl.cs108ademoapp.Web.Responses.MercaderiaDto;

import java.util.List;

public class InventarioInicialViewModel extends AndroidViewModel {

    private final WebServiceRepository webServiceRepository;
    private final MercaderiaRepository mercaderiaRepository;

    public static InventarioInicialViewModel getInstance(Application application) {
        return new InventarioInicialViewModel(application);
    }

    public InventarioInicialViewModel(@NonNull Application application) {
        super(application);
        webServiceRepository = new WebServiceRepository(application);
        mercaderiaRepository = new MercaderiaRepository(application);
    }

    public List<MercaderiaDto> DownloadInventarioInicial() {
        List<MercaderiaDto> inventario = webServiceRepository.GetInventory(MainActivity.ubicacionDto.Id);
        List<MercaderiaDto> inventarioLocal = mercaderiaRepository.GetAll();
        if (inventarioLocal.size() > 0) {
            mercaderiaRepository.DeleteAllSync(inventarioLocal);

        }
        mercaderiaRepository.InsertAllSync(inventario);
        return inventario;
    }

    public List<MercaderiaDto> LoadInventarioInicial() {
        return mercaderiaRepository.GetAll();
    }

    public List<MercaderiaDto> LoadInventarioInicialFiltered(String data) {
        return mercaderiaRepository.GetAllFiltered(data);
    }

    public void DeleteInventarioInicial() {
        List<MercaderiaDto> inventarioLocal = mercaderiaRepository.GetAll();
        if (inventarioLocal.size() > 0) {
            mercaderiaRepository.DeleteAllSync(inventarioLocal);
        }
    }
}