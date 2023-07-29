package com.csl.cs108ademoapp.Web.Repository;

import android.app.Application;

import com.csl.cs108ademoapp.Generales.MetodosGenerales;
import com.csl.cs108ademoapp.Web.CayApiAdapter;
import com.csl.cs108ademoapp.Web.CayApiService;
import com.csl.cs108ademoapp.Web.Responses.AssignTagsRfidViewModelDto;
import com.csl.cs108ademoapp.Web.Responses.InventarioDto;
import com.csl.cs108ademoapp.Web.Responses.MercaderiaDto;
import com.csl.cs108ademoapp.Web.Responses.MovimientoProductoDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionDto;
import com.csl.cs108ademoapp.Web.Responses.PagedResultDto;
import com.csl.cs108ademoapp.Web.Responses.ProductoDto;
import com.csl.cs108ademoapp.Web.Responses.ResponseToken;
import com.csl.cs108ademoapp.Web.Responses.UbicacionDto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class WebServiceRepository {
    Application application;
    CayApiService cayApiService;
    String auth, tenant;
    ResponseToken responseToken = MetodosGenerales.responseToken;

    public WebServiceRepository(Application application) {
        this.application = application;
        cayApiService = CayApiAdapter.getApiService(CayApiService.class, false);
        if (responseToken != null) {
            this.auth = responseToken.token_type + " " + responseToken.access_token;
            tenant = MetodosGenerales.tenant;
        }
    }

    public ResponseToken Login(String username, String password, String tenant) {
        if (responseToken == null) {
            cayApiService = CayApiAdapter.getApiService(CayApiService.class, true);
            final Call<ResponseToken> responseTokenCall = cayApiService.login("password", username, password, "Botris_App", "1q2w3e*", "opnedid", "code", tenant);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Response<ResponseToken> responseTokenResponse = responseTokenCall.execute();
                        if (responseTokenResponse.isSuccessful()) {
                            responseToken = responseTokenResponse.body();
                        } else {
                            responseToken = null;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
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
        cayApiService = CayApiAdapter.getApiService(CayApiService.class, false);
        return responseToken;
    }

    public List<String> GetSalas(List<String> tagsSalas) {
        final List<String> nombres = new ArrayList<>();
        if (responseToken != null && this.auth != null && !this.auth.equals("")) {
            final Call<List<String>> listCall = cayApiService.trabajadorSala(auth, tagsSalas);
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Response<List<String>> listResponse = listCall.execute();
                        if (listResponse.isSuccessful()) {
                            nombres.addAll(listResponse.body());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
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
        return nombres;
    }

    public boolean PostAssignTags(AssignTagsRfidViewModelDto assignTagsRfidViewModelDto) {
        Boolean aBoolean = MakeCall(cayApiService.postAsignartags(auth, assignTagsRfidViewModelDto));
        if (aBoolean != null) {
            return aBoolean.booleanValue();
        }
        return false;

    }

    public List<ProductoDto> GetProducto() {
        List<ProductoDto> siembraDtoPagedResultDto = MakeCall(cayApiService.productos(auth));
        if (siembraDtoPagedResultDto != null) {
            return siembraDtoPagedResultDto;
        }
        return new ArrayList<>();
    }

    public List<UbicacionDto> GetUbicacion() {
        PagedResultDto<UbicacionDto> siembraDtoPagedResultDto = MakeCall(cayApiService.ubicaciones(auth, 999));
        if (siembraDtoPagedResultDto != null) {
            return siembraDtoPagedResultDto.items;
        }
        return new ArrayList<>();
    }

    public List<MovimientoProductoDto> PostManyMovimientoProducto(List<MovimientoProductoDto> movimientoProductoDtos) {
        List<MovimientoProductoDto> movimientoProductoDtos1 = MakeCall(cayApiService.postManyMovimientoProductos(auth, movimientoProductoDtos));
        return movimientoProductoDtos1;
    }

    public List<OrdenDespachoRecepcionDto> GetOrdenDespacho(String ubicacionId) {
        List<OrdenDespachoRecepcionDto> ordenDespachoRecepcionDtos = MakeCall(cayApiService.ordenesDespacho(auth, ubicacionId));
        return ordenDespachoRecepcionDtos;
    }

    public List<OrdenDespachoRecepcionDto> GetOrdenRecepcion(String ubicacionId) {
        List<OrdenDespachoRecepcionDto> ordenDespachoRecepcionDtos = MakeCall(cayApiService.ordenesRecepcion(auth, ubicacionId));
        return ordenDespachoRecepcionDtos;
    }

    public boolean DeleteManyMovimientoProducto(List<String> movimientoProductoDtos) {
        Boolean result = MakeCall(cayApiService.deleteManyMovimientoProductos(auth, movimientoProductoDtos));
        return result.booleanValue();
    }

    public List<MovimientoProductoDto> GetMovimientoProductoByUbicacion(String ubicacionId) {
        return MakeCall(cayApiService.movimientoProductos(auth, ubicacionId));
    }

    public OrdenDespachoRecepcionDto PostOrderWithRfid(OrdenDespachoRecepcionDto input) {
        return MakeCall(cayApiService.postOrderWithRfid(auth, input.Id, input.OrdenDespachoRecepcionRfids));
    }

    public OrdenDespachoRecepcionDto PostNewOrderWithRfid(OrdenDespachoRecepcionDto input) {
        return MakeCall(cayApiService.postNewOrderWithRfid(auth, input));
    }

    public MovimientoProductoDto GetByRfid(String codRfid) {
        return MakeCall(cayApiService.GetByRfid(auth, codRfid));
    }

    public List<InventarioDto> GenerateInventory(List<InventarioDto> inventarioDtos, String ubicacionId) {
        return MakeCall(cayApiService.GenerateInventario(auth, ubicacionId, inventarioDtos));
    }

    public boolean SaveInventario(List<InventarioDto> inventarioDtos1) {
        Boolean result = MakeCall(cayApiService.SaveInventario(auth, inventarioDtos1));
        if (result != null && result) {
            return result;
        }
        return false;
    }

    public List<MercaderiaDto> GetInventory(String ubicacionId) {
        return MakeCall(cayApiService.getInventario(auth, ubicacionId));
    }

    private <U> U MakeCall(Call<U> requestCall) {
        try {
            Response<U> pagedResultDtoResponse = requestCall.execute();
            if (pagedResultDtoResponse.isSuccessful()) {
                return pagedResultDtoResponse.body();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
