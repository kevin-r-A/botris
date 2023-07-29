package com.csl.cs108ademoapp.Entities.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.csl.cs108ademoapp.Entities.Clases.ACTIVO;
import com.csl.cs108ademoapp.Entities.POJO.ACTIVO_POJO;

import java.util.List;

@Dao
public interface Activo_DAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ACTIVO activo);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<ACTIVO> activos);

    @Query("SELECT ACT_CODRFID FROM ACTIVO")
    List<String> getCodRFID();

    @Query("update ACTIVO set ACT_ESTADO='C' where ACT_CODRFID IN (:codigos)")
    void updateEstadoByCodigos(List<String> codigos);

    @Query("SELECT ACTIVO.*, GRUPO.GRU_NOMBRE AS DESCRIPCION, UORGANICA.UOR_NOMBRE AS UBICACION, (CUSTODIO.CUS_APELLIDOS || ' ' || CUSTODIO.CUS_NOMBRES) AS CUSTODIO FROM ACTIVO INNER JOIN GRUPO ON ACTIVO.GRU_ID3=GRUPO.GRU_ID " +
           "INNER JOIN UORGANICA ON UORGANICA.UOR_ID=ACTIVO.UOR_ID3 INNER JOIN CUSTODIO ON ACTIVO.CUS_ID1=CUSTODIO.CUS_ID WHERE ACT_ESTADO='C'")
    List<ACTIVO_POJO> getConciliados();

    @Query("SELECT ACTIVO.*, GRUPO.GRU_NOMBRE AS DESCRIPCION, UORGANICA.UOR_NOMBRE AS UBICACION, (CUSTODIO.CUS_APELLIDOS || ' ' || CUSTODIO.CUS_NOMBRES) AS CUSTODIO FROM ACTIVO INNER JOIN GRUPO ON ACTIVO.GRU_ID3=GRUPO.GRU_ID " +
            "INNER JOIN UORGANICA ON UORGANICA.UOR_ID=ACTIVO.UOR_ID3 INNER JOIN CUSTODIO ON ACTIVO.CUS_ID1=CUSTODIO.CUS_ID WHERE ACT_ESTADO = '' OR ACT_ESTADO  ISNULL")
    List<ACTIVO_POJO> getFaltantes();

    @Query("UPDATE ACTIVO SET ACT_ESTADO =''")
    void EliminaEstadoActivo();

    @Query("DELETE FROM ACTIVO")
    void deleteAll();
}
