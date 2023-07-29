package com.csl.cs108ademoapp.Entities.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.csl.cs108ademoapp.Entities.Clases.GRUPO;

import java.util.List;

@Dao
public interface Grupo_DAO {
    @Insert
    void insertAll(List<GRUPO> grupos);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(GRUPO grupo);

    @Query("SELECT * FROM GRUPO WHERE GRU_NOMBRE=:nombre and GRU_NIVEL=:nivel and GRU_PADRE=:padre")
    GRUPO GetGrupo(String nombre, int nivel, int padre);

    @Query("DELETE FROM GRUPO")
    void deleteAll();
}
