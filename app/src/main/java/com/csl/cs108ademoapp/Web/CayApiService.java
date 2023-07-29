package com.csl.cs108ademoapp.Web;

import com.csl.cs108ademoapp.Web.Responses.AssignTagsRfidViewModelDto;
import com.csl.cs108ademoapp.Web.Responses.InventarioDto;
import com.csl.cs108ademoapp.Web.Responses.MercaderiaDto;
import com.csl.cs108ademoapp.Web.Responses.MovimientoProductoDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionRfidDto;
import com.csl.cs108ademoapp.Web.Responses.PagedResultDto;
import com.csl.cs108ademoapp.Web.Responses.ProductoDto;
import com.csl.cs108ademoapp.Web.Responses.ResponseToken;
import com.csl.cs108ademoapp.Web.Responses.UbicacionDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CayApiService {
    String subapp = "/BOTRISAPI";

    @POST(subapp + "/connect/token")
    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    Call<ResponseToken> login(@Field("grant_type") String grant_type, @Field("UserName") String username, @Field("Password") String password,
                              @Field("client_id") String client_id, @Field("client_secret") String client_secret,
                              @Field("scopes") String scopes, @Field("response_type") String response_type, @Header("__tenant") String tenant);

    @GET(subapp + "/api/app/trabajador/salas")
    Call<List<String>> trabajadorSala(@Header("Authorization") String auth, @Query("tagsSalas") List<String> tagsSalas);

    @POST(subapp + "/api/app/tags/assign-tags-rfid")
    Call<Boolean> postAsignartags(@Header("Authorization") String auth, @Body AssignTagsRfidViewModelDto assignTagsRfidViewModelDto);

    @GET(subapp + "/api/app/producto/products")
    Call<List<ProductoDto>> productos(@Header("Authorization") String auth);

    @GET(subapp + "/api/app/ubicacion")
    Call<PagedResultDto<UbicacionDto>> ubicaciones(@Header("Authorization") String auth, @Query("MaxResultCount") int count);

    @POST(subapp + "/api/app/movimiento-producto/many")
    Call<List<MovimientoProductoDto>> postManyMovimientoProductos(@Header("Authorization") String auth, @Body List<MovimientoProductoDto> items);

    @GET(subapp + "/api/app/movimiento-producto/{id}/by-ubicacion")
    Call<List<MovimientoProductoDto>> movimientoProductos(@Header("Authorization") String auth, @Path("id") String ubicacionId);

    @DELETE(subapp + "/api/app/movimiento-producto/many")
    Call<Boolean> deleteManyMovimientoProductos(@Header("Authorization") String auth, @Query("inputs") List<String> items);

    @GET(subapp + "/api/app/orden-despacho-recepcion/orden-despacho/{ubicacionId}")
    Call<List<OrdenDespachoRecepcionDto>> ordenesDespacho(@Header("Authorization") String auth, @Path("ubicacionId") String ubicacionId);

    @GET(subapp + "/api/app/orden-despacho-recepcion/orden-recepcion/{ubicacionId}")
    Call<List<OrdenDespachoRecepcionDto>> ordenesRecepcion(@Header("Authorization") String auth, @Path("ubicacionId") String ubicacionId);

    @POST(subapp + "/api/app/orden-despacho-recepcion/{id}/orden-with-rfid")
    Call<OrdenDespachoRecepcionDto> postOrderWithRfid(@Header("Authorization") String auth, @Path("id") String id, @Body List<OrdenDespachoRecepcionRfidDto> items);

    @POST(subapp + "/api/app/orden-despacho-recepcion/new-orden-handheld")
    Call<OrdenDespachoRecepcionDto> postNewOrderWithRfid(@Header("Authorization") String auth, @Body OrdenDespachoRecepcionDto items);

    @GET(subapp + "/api/app/movimiento-producto/by-rfid")
    Call<MovimientoProductoDto> GetByRfid(@Header("Authorization") String auth, @Query("codRfid") String id);

    @POST(subapp + "/api/app/inventario/generate-inventario/{ubicacionId}")
    Call<List<InventarioDto>> GenerateInventario(@Header("Authorization") String auth, @Path("ubicacionId") String ubicacionId, @Body List<InventarioDto> inventarioDtos);

    @POST(subapp + "/api/app/inventario/save-inventario")
    Call<Boolean> SaveInventario(@Header("Authorization") String auth, @Body List<InventarioDto> inventarioDtos);

    @GET(subapp + "/api/app/inventario/inventario-botris/{ubicacionId}")
    Call<List<MercaderiaDto>> getInventario(@Header("Authorization") String auth, @Path("ubicacionId") String ubicacionId);
}
