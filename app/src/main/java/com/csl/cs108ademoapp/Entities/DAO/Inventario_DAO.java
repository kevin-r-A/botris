package com.csl.cs108ademoapp.Entities.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.csl.cs108ademoapp.Entities.Enums.InventarioType;
import com.csl.cs108ademoapp.Web.Responses.InventarioDetalleRfidDto;
import com.csl.cs108ademoapp.Web.Responses.InventarioDto;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

@Dao
public abstract class Inventario_DAO implements EntityDao<InventarioDto> {
    @Override
    public void InsertAllWithChild(List<InventarioDto> entity) {
        String identificador = java.util.UUID.randomUUID().toString();
        entity.forEach(x -> {
            x.Id = java.util.UUID.randomUUID().toString();
            x.Identificador = identificador;
            x.Fecha = Calendar.getInstance().getTime();
            if (x.Type == null) {
                x.Type = InventarioType.Faltante;
            }

            x.InventarioDetalleRfids.forEach(y -> {
                y.InventarioId = x.Id;
                y.Id = java.util.UUID.randomUUID().toString();
            });
        });
        List<InventarioDetalleRfidDto> collect = entity.stream().map(x -> x.InventarioDetalleRfids).flatMap(List::stream).collect(Collectors.toList());
        InsertAll(entity);
        InsertAllDetalleRfid(collect);
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void InsertAllDetalleRfid(List<InventarioDetalleRfidDto> rfidDtos);

    @Override
    @Query("Select * from InventarioDto")
    public abstract List<InventarioDto> GetAll();
}
