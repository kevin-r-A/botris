package com.csl.cs108ademoapp.Entities.DAO;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.csl.cs108ademoapp.Entities.Clases.SERVER;

@Dao
public interface Server_DAO {
    @Insert
    void insert(SERVER server);

    @Query("SELECT * FROM SERVER LIMIT 1")
    SERVER getServer();

    @Query("DELETE FROM SERVER")
    void deleteAll();
}
