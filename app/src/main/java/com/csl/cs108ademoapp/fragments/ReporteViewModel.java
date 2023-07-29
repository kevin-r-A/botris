package com.csl.cs108ademoapp.fragments;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.csl.cs108ademoapp.Entities.Clases.STOCK;
import com.csl.cs108ademoapp.Entities.Enums.InventarioType;
import com.csl.cs108ademoapp.Entities.Repository.InventarioDetalleRfidRepository;
import com.csl.cs108ademoapp.Entities.Repository.InventarioRepository;
import com.csl.cs108ademoapp.Entities.Repository.MercaderiaRepository;
import com.csl.cs108ademoapp.Entities.Repository.MovimientoProductoRepository;
import com.csl.cs108ademoapp.Entities.Repository.ProductoRepository;
import com.csl.cs108ademoapp.Entities.Repository.StockRepository;
import com.csl.cs108ademoapp.Entities.ViewModels.InventarioCruceViewModel;
import com.csl.cs108ademoapp.Generales.MetodosGenerales;
import com.csl.cs108ademoapp.MainActivity;
import com.csl.cs108ademoapp.Web.Repository.WebServiceRepository;
import com.csl.cs108ademoapp.Web.Responses.InventarioDetalleRfidDto;
import com.csl.cs108ademoapp.Web.Responses.InventarioDto;
import com.csl.cs108ademoapp.Web.Responses.MercaderiaDto;
import com.csl.cs108ademoapp.Web.Responses.MovimientoProductoDto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReporteViewModel extends AndroidViewModel {
    private final InventarioRepository inventarioRepository;
    private final InventarioDetalleRfidRepository inventarioDetalleRfidRepository;
    private final WebServiceRepository webServiceRepository;
    private final ProductoRepository productoRepository;
    private MutableLiveData<List<InventarioDto>> faltantesLiveData = new MutableLiveData<>();
    private MutableLiveData<List<InventarioDto>> sobrantesLiveData = new MutableLiveData<>();
    private MutableLiveData<List<InventarioDto>> conciliadosLiveData = new MutableLiveData<>();
    private List<InventarioDto> inventarioDtos1;
    private final StockRepository stockRepository;
    private final MovimientoProductoRepository movimientoProductoRepository;
    private final MercaderiaRepository mercaderiaRepository;

    public boolean hasInventory() {
        return inventarioDtos1 != null && inventarioDtos1.size() > 0;
    }

    public ReporteViewModel(@NonNull Application application) {
        super(application);
        inventarioRepository = new InventarioRepository(application);
        inventarioDetalleRfidRepository = new InventarioDetalleRfidRepository(application);
        webServiceRepository = new WebServiceRepository(application);
        productoRepository = new ProductoRepository(application);
        stockRepository = new StockRepository(application);
        movimientoProductoRepository = new MovimientoProductoRepository(application);
        mercaderiaRepository = new MercaderiaRepository(application);
    }

    public static ReporteViewModel getInstance(Application application) {
        return new ReporteViewModel(application);
    }

    public List<InventarioDto> GetInventario() {
        List<InventarioDto> inventarioDtos = inventarioRepository.GetAll();
        List<InventarioDetalleRfidDto> inventarioDetalleRfidDtos = inventarioDetalleRfidRepository.GetAll();
        inventarioDtos.forEach(x -> {
            x.InventarioDetalleRfids = inventarioDetalleRfidDtos.stream().filter(y -> y.InventarioId.equals(x.Id)).collect(Collectors.toList());
        });
        return inventarioDtos;
    }

    public MutableLiveData<List<InventarioDto>> getFaltantesLiveData() {
        return faltantesLiveData;
    }

    public MutableLiveData<List<InventarioDto>> getSobrantesLiveData() {
        return sobrantesLiveData;
    }

    public MutableLiveData<List<InventarioDto>> getConciliadosLiveData() {
        return conciliadosLiveData;
    }

    public void GenerateInventory() throws Exception {


       /* List<InventarioDto> inventarioDtos = GetInventario();
        inventarioDtos1 = webServiceRepository.GenerateInventory(inventarioDtos, MainActivity.ubicacionDto.Id);*/
        try {
            //List<InventarioDto> collectFaltantes = inventarioDtos1.stream().filter(x -> x.Type == InventarioType.Faltante).collect(Collectors.toList());

           /* for (InventarioDto inventarioDto : inventarioDtos1) {
                inventarioDto.Producto = productoRepository.findByCodigoBarrasSYNC(inventarioDto.CodBarra);
                if (inventarioDto.Type == InventarioType.Faltante) {
                    collectFaltantes.add(inventarioDto);
                } else if (inventarioDto.Type == InventarioType.Sobrante) {
                    collectSobrantes.add(inventarioDto);
                } else {
                    collectConciliado.add(inventarioDto);
                }
            }*/
            stockRepository.EnableAllSYN();
            stockRepository.DeleteInvalidosSYN(MainActivity.ubicacionDto.Id);
            List<InventarioCruceViewModel> cruceViewModels = movimientoProductoRepository.GetInventorioCruce();
            List<MercaderiaDto> mercaderiaDtoList = mercaderiaRepository.GetAll();
            List<InventarioDto> collectFaltantes = new ArrayList<>();
            List<InventarioDto> collectSobrantes = new ArrayList<>();
            List<InventarioDto> collectConciliado = new ArrayList<>();
            String identificador = java.util.UUID.randomUUID().toString();
            Date fecha =  Calendar.getInstance().getTime();
            for (InventarioCruceViewModel cruceViewModel : cruceViewModels) {
                MercaderiaDto mercaderiaDto1 = mercaderiaDtoList.stream().filter(mercaderiaDto -> mercaderiaDto.ART_CODIGOBARRA.equals(cruceViewModel.CodBarra)).findFirst().orElse(null);
                if (mercaderiaDto1 != null) {
                    InventarioDto inventarioDto = new InventarioDto();
                    inventarioDto.Id = java.util.UUID.randomUUID().toString();
                    inventarioDto.CodBarra = mercaderiaDto1.ART_CODIGOBARRA;
                    inventarioDto.CantidadFisica = cruceViewModel.Cantidad;
                    inventarioDto.CantidadSistema = mercaderiaDto1.CANTIDAD;
                    inventarioDto.Producto = productoRepository.findByCodigoBarrasSYNC(inventarioDto.CodBarra);
                    inventarioDto.ProductoId = inventarioDto.Producto.Id;
                    inventarioDto.Fecha = fecha;
                    inventarioDto.UbicacionId = MainActivity.ubicacionDto.Id;
                    inventarioDto.Sku = mercaderiaDto1.BOD_CODIGO;
                    inventarioDto.Identificador = identificador;
                    if (mercaderiaDto1.CANTIDAD == cruceViewModel.Cantidad) {
                        inventarioDto.Type = InventarioType.Conciliado;
                        collectConciliado.add(inventarioDto);
                    } else if (mercaderiaDto1.CANTIDAD > cruceViewModel.Cantidad) {
                        inventarioDto.Type = InventarioType.Faltante;
                        collectFaltantes.add(inventarioDto);
                    } else {
                        inventarioDto.Type = InventarioType.Sobrante;
                        collectSobrantes.add(inventarioDto);
                    }
                }
            }

            for (MercaderiaDto mercaderiaDto : mercaderiaDtoList) {
                if (cruceViewModels.stream().noneMatch(inventarioCruceViewModel -> Objects.equals(inventarioCruceViewModel.CodBarra, mercaderiaDto.ART_CODIGOBARRA))) {
                    InventarioDto inventarioDto = new InventarioDto();
                    inventarioDto.Id = java.util.UUID.randomUUID().toString();
                    inventarioDto.CodBarra = mercaderiaDto.ART_CODIGOBARRA;
                    inventarioDto.CantidadFisica = 0;
                    inventarioDto.CantidadSistema = mercaderiaDto.CANTIDAD;
                    inventarioDto.Producto = productoRepository.findByCodigoBarrasSYNC(inventarioDto.CodBarra);
                    inventarioDto.ProductoId = inventarioDto.Producto.Id;
                    inventarioDto.Fecha = fecha;
                    inventarioDto.UbicacionId = MainActivity.ubicacionDto.Id;
                    inventarioDto.Sku = mercaderiaDto.BOD_CODIGO;
                    inventarioDto.Type = InventarioType.Faltante;
                    inventarioDto.Identificador = identificador;
                    collectFaltantes.add(inventarioDto);
                }
            }
            inventarioDtos1 = new ArrayList<>();
            inventarioDtos1.addAll(collectConciliado);
            inventarioDtos1.addAll(collectSobrantes);
            inventarioDtos1.addAll(collectFaltantes);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    faltantesLiveData.postValue(collectFaltantes);
                }
            }).start();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    sobrantesLiveData.postValue(collectSobrantes);
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    conciliadosLiveData.postValue(collectConciliado);
                }
            }).start();
        } catch (Exception ex) {
            throw ex;
        }

    }

    public boolean SaveInventory() {
        if (inventarioDtos1 != null && inventarioDtos1.size() > 0) {
            return webServiceRepository.SaveInventario(inventarioDtos1);
        }
        return true;
    }

}
