package com.csl.cs108ademoapp.Entities.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.csl.cs108ademoapp.Entities.Clases.CUSTODIO;
import com.csl.cs108ademoapp.Entities.Clases.GRUPO;

import java.util.List;

@Dao
public interface Custodio_DAO {

    @Insert
    void insertAll(List<CUSTODIO> custodios);

    @Query("DELETE FROM CUSTODIO")
    void deleteAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(CUSTODIO custodio);

    @Query("SELECT * FROM CUSTODIO WHERE CUS_APELLIDOS=:nombre")
    CUSTODIO GetCustodio(String nombre);
}
