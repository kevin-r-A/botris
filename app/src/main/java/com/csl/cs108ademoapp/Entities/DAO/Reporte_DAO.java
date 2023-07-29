package com.csl.cs108ademoapp.Entities.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.csl.cs108ademoapp.Entities.Clases.REPORTE;

import java.util.List;

@Dao
public interface Reporte_DAO {

    @Insert
    void insertAll(List<REPORTE> reporteList);

    @Insert
    void  insert(REPORTE reporte);

    @Query("DELETE FROM REPORTE")
    void deleteAll();

    @Query("SELECT * FROM REPORTE")
    List<REPORTE> getAll();
}
