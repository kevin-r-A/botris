package com.csl.cs108ademoapp.Entities.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.csl.cs108ademoapp.Entities.Clases.GRUPO;
import com.csl.cs108ademoapp.Entities.Clases.UORGANICA;

import java.util.List;

@Dao
public interface Uorganica_DAO {

    @Insert
    void insertAll(List<UORGANICA> uorganicas);

    @Query("DELETE FROM UORGANICA")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(UORGANICA grupo);

    @Query("SELECT * FROM UORGANICA WHERE UOR_NOMBRE=:nombre and UOR_NIVEL=:nivel and UOR_PADRE=:padre")
    UORGANICA GetUorganica(String nombre, int nivel, int padre);
}
