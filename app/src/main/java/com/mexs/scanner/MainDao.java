package com.mexs.scanner;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public abstract class MainDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract void insertRecord(Record record);

    @Update
    abstract void updateRecord(Record record);

    @Delete
    abstract void deleteRecord(Record record);

    @Query("SELECT * FROM record_table ORDER BY swabTimestamp DESC")
    abstract LiveData<List<Record>> readRecords();

    @Query("SELECT COUNT(id) FROM record_table")
    abstract LiveData<Integer> countRecords();

    @Query("DELETE FROM record_table")
    abstract void deleteAllRecords();

}
