package com.mexs.scanner;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mexs.idparser.IdData;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private MainRoomDatabase mDatabase;
    private MainDao mMainDao;
    private SharedPreferences mSharedPreferences;

    public MutableLiveData<String> mCurState = new MutableLiveData<>();
    public MutableLiveData<Boolean> mShowSettingsError = new MutableLiveData<>();
    public MutableLiveData<Record> mCurRecord = new MutableLiveData<>();
    public MutableLiveData<IdData> mIdData = new MutableLiveData<>();

    private MutableLiveData<Long[]> mRecordsStats = new MutableLiveData<>();
    public LiveData<List<Record>> mListRecordsFull;

    public MutableLiveData<UserSettings> mSettings = new MutableLiveData<>();

    public MainViewModel(Application application) {
        super(application);

        mDatabase = MainRoomDatabase.getDatabase(application);
        mMainDao = mDatabase.mainDao();
        mSharedPreferences = application.getSharedPreferences(application.getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        mCurRecord.setValue(new Record());
        mIdData.setValue(new IdData());
        readSettings();
        mCurState.setValue("setup");
        mShowSettingsError.setValue(false);
    }

    public void setCurState(String state){
        if (mCurState.getValue() == null || !mCurState.getValue().equals(state))
            mCurState.setValue(state);
    }

    public void insertRecord(Record record){
        MainRoomDatabase.databaseBackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mMainDao.insertRecord(record);
            }
        });
    }

    public void updateRecord(Record record){
        MainRoomDatabase.databaseBackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mMainDao.updateRecord(record);
            }
        });
    }

    public void deleteRecord(Record record){
        MainRoomDatabase.databaseBackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mMainDao.deleteRecord(record);
            }
        });
    }

    public void deleteAllRecords(){
        MainRoomDatabase.databaseBackgroundExecutor.execute(new Runnable() {
            @Override
            public void run() {
                mMainDao.deleteAllRecords();
            }
        });
    }

    public LiveData<List<Record>> readRecords(){
        mListRecordsFull = mMainDao.readRecords();
        return mListRecordsFull;
    }

    public LiveData<Integer> countRecords(){
        return mMainDao.countRecords();
    }

    public UserSettings readSettings(){
        UserSettings userSettings = new UserSettings(
                mSharedPreferences.getString("test_type", ""),
                mSharedPreferences.getString("location_name", ""),
                mSharedPreferences.getString("lab", ""),
                mSharedPreferences.getString("recorder", ""),
                mSharedPreferences.getBoolean("specimen_id_use", false),
                mSharedPreferences.getBoolean("print_labels", false),
                mSharedPreferences.getString("printer_model", "BROTHER QL-820NWB"),
                mSharedPreferences.getInt("print_style", 1),
                mSharedPreferences.getInt("print_qty", 1));

        mSettings.setValue(userSettings);

        return userSettings;
    }

    public void writeSettings(UserSettings userSettings){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString("test_type", userSettings.getTestType());
        editor.putString("location_name", userSettings.getLocationName());
        editor.putString("lab", userSettings.getLab());
        editor.putString("recorder", userSettings.getRecorder());
        editor.putBoolean("specimen_id_use", userSettings.useSpecimenId());
        editor.putBoolean("print_labels", userSettings.isPrintLabels());
        editor.putString("printer_model", userSettings.getPrinterModel());
        editor.putInt("print_style", userSettings.getPrintStyle());
        editor.putInt("print_qty", userSettings.getPrintQty());
        editor.apply();

        mSettings.setValue(userSettings);
    }

}