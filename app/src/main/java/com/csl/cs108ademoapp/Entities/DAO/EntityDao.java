package com.csl.cs108ademoapp.Entities.DAO;

import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;


import java.util.List;

public interface EntityDao<T> {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void Insert(T entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertAll(List<T> entity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void Update(T entity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void UpdateAll(List<T> entity);

    @Delete
    void Delete(T entity);

    @Delete
    void DeleteAll(List<T> entity);

    List<T> GetAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertAllWithChild(List<T> entity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void InsertWithChild(T entity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void UpdateWithChild(T entity);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void UpdateAllWithChild(List<T> entity);

    @Delete
    void DeleteWithChild(T entity);

    @Delete
    void DeleteAllWithChild(List<T> entity);
}
