package com.csl.cs108ademoapp.Entities.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.csl.cs108ademoapp.Entities.ViewModels.InventarioCruceViewModel;
import com.csl.cs108ademoapp.Web.Responses.MovimientoProductoDto;

import java.util.List;

@Dao
public interface MovimientoProducto_DAO extends EntityDao<MovimientoProductoDto> {

    @Override
    @Query("Select * from MovimientoProductoDto")
    List<MovimientoProductoDto> GetAll();

    @Query("Select * from MovimientoProductoDto where Enviado=1 and Eliminado=0")
    List<MovimientoProductoDto> GetUnSendNew();

    @Query("Select Id from MovimientoProductoDto where Enviado=1 and Eliminado=1")
    List<String> GetUnSendDelete();

    @Query("Update MovimientoProductoDto set Enviado = 0 where Id in (:ids)")
    void UpdateDeleteSend(List<String> ids);

    @Query("Select * from MovimientoProductoDto where CodRfid=:rfid limit 1")
    MovimientoProductoDto SearchByRfid(String rfid);

    @Query("Select * from MovimientoProductoDto where CodRfid=:rfid and UbicacionId=:ubicacionId limit 1")
    MovimientoProductoDto SearchByRfid(String rfid, String ubicacionId);

    @Query("select CodBarra, max(ProductoId) as ProductoId, max(ProductoNombre) as ProductoNombre, count(*) as Cantidad, 'S' as Estado from MovimientoProductoDto where CodRfid in (select STO_CODIGO from STOCK WHERE STO_ESTADO<>'E') group by CodBarra")
    List<InventarioCruceViewModel> getInventarioCruce();
}
