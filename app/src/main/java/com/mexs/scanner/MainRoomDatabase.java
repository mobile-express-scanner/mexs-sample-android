package com.mexs.scanner;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = Record.class, version = 7, exportSchema = false)
public abstract class MainRoomDatabase extends RoomDatabase {

    public abstract MainDao mainDao();

    private static volatile MainRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseBackgroundExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static MainRoomDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (MainRoomDatabase.class){
                if (INSTANCE == null)
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MainRoomDatabase.class, "record_database").build();
            }
        }

        return INSTANCE;
    }

}
