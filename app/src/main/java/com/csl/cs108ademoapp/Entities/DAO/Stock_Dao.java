package com.csl.cs108ademoapp.Entities.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.csl.cs108ademoapp.Entities.Clases.STOCK;
import com.csl.cs108ademoapp.Web.Responses.MercaderiaDto;

import java.util.List;

@Dao
public interface Stock_Dao extends EntityDao<STOCK> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert (STOCK stock);
    @Query("SELECT STO_CODIGO FROM STOCK")
    List<String> getCodigos();

    @Query("UPDATE STOCK SET STO_ESTADO='C' WHERE STO_CODIGO IN (:sto_codigos)")
    void updateEstadoByCodigos(List<String> sto_codigos);

    @Query("SELECT * FROM STOCK WHERE STO_ESTADO='S'")
    List<STOCK> getSobrantes();

    @Query("DELETE FROM STOCK")
    void deleteAll();

    @Override
    @Query("Select * from STOCK order by STO_CODIGO")
    List<STOCK> GetAll();

    @Query("UPDATE STOCK SET STO_ESTADO='E' where STO_CODIGO not in (select CodRfid from MOVIMIENTOPRODUCTODTO where UbicacionId=:ubicacionId)")
    void DeleteInvalidos(String ubicacionId);

    @Query("UPDATE STOCK SET STO_ESTADO='S'")
    void EnableAllSYN();
}
