package com.csl.cs108ademoapp.Entities.DAO;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.TypeConverters;
import android.arch.persistence.room.Update;

import com.csl.cs108ademoapp.Convertidores.EstadoTypeConverter;
import com.csl.cs108ademoapp.Convertidores.TimestampConverter;
import com.csl.cs108ademoapp.Entities.Enums.EstadoType;
import com.csl.cs108ademoapp.Entities.POJO.OrdenDespachoRecepcionWithRfid;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionDetalleDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionDto;
import com.csl.cs108ademoapp.Web.Responses.OrdenDespachoRecepcionRfidDto;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Dao
public abstract class OrdenDespachoRecepcion_DAO implements EntityDao<OrdenDespachoRecepcionDto> {

    @Override
    @Query("Select * from OrdenDespachoRecepcionDto")
    public abstract List<OrdenDespachoRecepcionDto> GetAll();

    @TypeConverters({EstadoTypeConverter.class})
    @Query("Select * from OrdenDespachoRecepcionDto where UbicacionId=:ubicacionId and Estado=:estadoType or Estado=:estadoType2")
    public abstract LiveData<List<OrdenDespachoRecepcionDto>> GetDespachoByUbicacionLiveData(String ubicacionId, EstadoType estadoType, EstadoType estadoType2);

    @TypeConverters({EstadoTypeConverter.class, TimestampConverter.class})
    @Query("Select * from OrdenDespachoRecepcionDto where UbicacionId=:ubicacionId and date(Fecha)=date(:fecha) and (Estado=:estadoType or Estado=:estadoType2)")
    public abstract LiveData<List<OrdenDespachoRecepcionDto>> GetDespachoByUbicacionLiveDataByFecha(String ubicacionId, EstadoType estadoType, EstadoType estadoType2, Date fecha);

    @TypeConverters({EstadoTypeConverter.class})
    @Query("Select * from OrdenDespachoRecepcionDto where UbicacionDestinoId=:ubicacionId and Estado!=:estadoType and Estado!=:estadoType2")
    public abstract LiveData<List<OrdenDespachoRecepcionDto>> GetRecepcionByUbicacionLiveData(String ubicacionId, EstadoType estadoType, EstadoType estadoType2);

    @Query("Select * from OrdenDespachoRecepcionRfidDto where Enviar=1")
    public abstract List<OrdenDespachoRecepcionRfidDto> GetRfidUnSendDetails();

    @Query("Select * from ordendespachorecepciondto where Id in (:ids)")
    public abstract List<OrdenDespachoRecepcionWithRfid> GetOrdenWithRfidByIds(List<String> ids);

    @Query("Select * from ordendespachorecepciondto where Id in (:ids)")
    public abstract List<OrdenDespachoRecepcionDto> GetOrdenByIds(List<String> ids);

    public List<OrdenDespachoRecepcionDto> GetAllUnSend() {
        List<OrdenDespachoRecepcionRfidDto> ordenDespachoRecepcionRfidDtos = GetRfidUnSendDetails();
        List<String> stringList = ordenDespachoRecepcionRfidDtos.stream().map(x -> x.OrdenDespachoRecepcionId).distinct().collect(Collectors.toList());
        List<OrdenDespachoRecepcionDto> ordenDespachoRecepcionDtoList = GetOrdenByIds(stringList);
        List<OrdenDespachoRecepcionDetalleDto> ordenDespachoRecepcionDetalleDtos = GetDetalleByOrdenIds(stringList);
        ordenDespachoRecepcionDtoList.forEach(x -> {
            x.OrdenDespachoRecepcionRfids = ordenDespachoRecepcionRfidDtos.stream().filter(y -> y.OrdenDespachoRecepcionId.equals(x.Id)).collect(Collectors.toList());
            x.OrdenDespachoRecepcionDetalles = ordenDespachoRecepcionDetalleDtos.stream().filter(y -> y.OrdenDespachoRecepcionId.equals(x.Id)).collect(Collectors.toList());
        });
        return ordenDespachoRecepcionDtoList;
    }

    @Query("SELECT * FROM OrdenDespachoRecepcionDetalleDto where OrdenDespachoRecepcionId in (:ids)")
    public abstract List<OrdenDespachoRecepcionDetalleDto> GetDetalleByOrdenIds(List<String> ids);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public void InsertAllWithChild(List<OrdenDespachoRecepcionDto> entity) {
        InsertAll(entity);
        InsertDetalle(entity.stream().map((x) -> x.OrdenDespachoRecepcionDetalles).flatMap(List::stream).collect(Collectors.toList()));
        entity.stream().map((x) -> x.OrdenDespachoRecepcionRfids).flatMap(List::stream).filter(x -> x.Id == null || x.Id.equals("")).collect(Collectors.toList()).forEach(x -> x.Id = java.util.UUID.randomUUID().toString());
        InsertDetalleRFID(entity.stream().map((x) -> x.OrdenDespachoRecepcionRfids).flatMap(List::stream).collect(Collectors.toList()));
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void InsertDetalle(List<OrdenDespachoRecepcionDetalleDto> entities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void InsertDetalleRFID(List<OrdenDespachoRecepcionRfidDto> entities);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void UpdateDetalle(List<OrdenDespachoRecepcionDetalleDto> entities);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    public abstract void UpdateDetalleRFID(List<OrdenDespachoRecepcionRfidDto> entities);

    @Query("Delete from OrdenDespachoRecepcionDetalleDto where OrdenDespachoRecepcionId=:parentId")
    public abstract void DeleteDetalle(String parentId);

    @Override
    public void InsertWithChild(OrdenDespachoRecepcionDto entity) {
        Insert(entity);
        InsertDetalle(entity.OrdenDespachoRecepcionDetalles);
        InsertDetalleRFID(entity.OrdenDespachoRecepcionRfids);
    }

    @Override
    public void UpdateWithChild(OrdenDespachoRecepcionDto entity) {
        Update(entity);
        UpdateDetalle(entity.OrdenDespachoRecepcionDetalles);
        InsertDetalleRFID(entity.OrdenDespachoRecepcionRfids);
    }

    @Override
    public void UpdateAllWithChild(List<OrdenDespachoRecepcionDto> entity) {
        UpdateAll(entity);
        for (OrdenDespachoRecepcionDto ordenDespachoRecepcionDto : entity) {
            UpdateDetalle(ordenDespachoRecepcionDto.OrdenDespachoRecepcionDetalles);
        }
    }

    @Override
    public void DeleteWithChild(OrdenDespachoRecepcionDto entity) {
        Delete(entity);
        DeleteDetalle(entity.Id);
    }

    @Override
    public void DeleteAllWithChild(List<OrdenDespachoRecepcionDto> entity) {
        DeleteAll(entity);
        for (OrdenDespachoRecepcionDto ordenDespachoRecepcionDto : entity) {
            DeleteDetalle(ordenDespachoRecepcionDto.Id);
        }
    }
}
