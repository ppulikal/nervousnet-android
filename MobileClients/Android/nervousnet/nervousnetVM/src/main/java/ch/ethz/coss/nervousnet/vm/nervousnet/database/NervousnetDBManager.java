package ch.ethz.coss.nervousnet.vm.nervousnet.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.configuration.GeneralSensorConfiguration;


/**
 * Created by ales on 18/10/16.
 */
public class NervousnetDBManager extends SQLiteOpenHelper implements Runnable {

    private static final String LOG_TAG = NervousnetDBManager.class.getSimpleName();

    ScheduledExecutorService scheduler;

    // Defining a singleton
    private static NervousnetDBManager instance = null;
    private static HashMap<Long, SensorReading> LATEST_SENSORS_DATA = new HashMap();
    private static HashMap<Long, ArrayList<SensorReading>> TEMPORARY_STORAGE = new HashMap();
    private long readingAgeThreshold = 864000;
    private long initialDelayForStoring = 2000; //5sec
    private long storingRate = 5000; // 2 sec

    private NervousnetDBManager(Context context) {
        super(context, ConstantsDB.DATABASE_NAME, null, ConstantsDB.DATABASE_VERSION);
        this.scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this, initialDelayForStoring,
                storingRate, TimeUnit.MILLISECONDS);
    }

    public static NervousnetDBManager getInstance(Context context){
        if (instance == null){
            instance = new NervousnetDBManager(context);
        }
        return instance;
    }

    /////////////////////////////////////////////////////////////
    // QUERY
    /////////////////////////////////////////////////////////////


    public static SensorReading getLatestReading(long sensorID){
        SensorReading reading = LATEST_SENSORS_DATA.get(sensorID);
        return reading;
    }

    public ArrayList<SensorReading> getReadings(GeneralSensorConfiguration config){
        String query = "SELECT * FROM " + getTableName(config.getSensorID());
        return getReadings(config, query);
    }

    public ArrayList<SensorReading> getReadings(GeneralSensorConfiguration config, long start, long stop){
        String query = "SELECT * FROM " + getTableName(config.getSensorID()) + " WHERE " + ConstantsDB.TIMESTAMP + " >= " + start + " AND " + ConstantsDB.TIMESTAMP + " <= " + stop + ";";
        return getReadings(config, query);
    }

    public ArrayList<SensorReading> getReadings(GeneralSensorConfiguration config, long start, long stop,
                                                ArrayList<String> selectColumns){
        String cols = TextUtils.join(", ", selectColumns);
        String query = "SELECT " + ConstantsDB.ID + ", " + ConstantsDB.TIMESTAMP + ", " + cols + " " +
                "FROM " + getTableName(config.getSensorID()) + " WHERE " + ConstantsDB.TIMESTAMP + " >= " + start + " AND " +
                "" + ConstantsDB.TIMESTAMP + " <= " + stop + ";";
        return getReadings(config, query);
    }


    public ArrayList<SensorReading> getLatestReadingUnderRange(GeneralSensorConfiguration config,
                                                               long start, long stop,
                                                               ArrayList<String> selectColumns) {
        String cols = TextUtils.join(", ", selectColumns);
        String query = "SELECT " + ConstantsDB.ID + ", MAX(" + ConstantsDB.TIMESTAMP + ") AS " + ConstantsDB.TIMESTAMP+ ", " + cols + " " +
                "FROM " + getTableName(config.getSensorID()) + " WHERE " + ConstantsDB.TIMESTAMP + " >= " + start + " AND " +
                "" + ConstantsDB.TIMESTAMP + " <= " + stop + ";";

        ArrayList<SensorReading> readings = getReadings(config, query);
        return readings;
    }

    public ArrayList<SensorReading> getReadings(GeneralSensorConfiguration config, String query) {

        ArrayList<String> sensorParamNames = config.getParametersNames();

        SQLiteDatabase DATABASE = getReadableDatabase();

        // 2. get reference to writable DB
        //SQLiteDatabase db = this.getReadableDatabase();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            DATABASE.enableWriteAheadLogging();
        }
        Cursor cursor = DATABASE.rawQuery(query, null);

        ArrayList<SensorReading> returnList = new ArrayList();
        //Log.d("CURSOR", "Count " + cursor.getCount() );
        // 3. go over each row, build sensor value and add it to list
        if (cursor.moveToFirst()) {
            do {
                // Update timestamp and values
                int indexTimestamp = cursor.getColumnIndex(ConstantsDB.TIMESTAMP);
                SensorReading reading = new SensorReading(config.getSensorID(), config.getSensorName(), sensorParamNames);
                reading.setTimestampEpoch(cursor.getLong(indexTimestamp));

                //Log.d("CURSOR", "Timestamp " + reading.getTimestampEpoch());

                for (String columnName : sensorParamNames){
                    int indexParam = cursor.getColumnIndex(columnName);
                    Object value = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        int type = cursor.getType(indexParam);
                        switch (type) {
                            case ConstantsDB.FIELD_TYPE_INTEGER:
                                value = cursor.getInt(indexParam);
                                break;

                            case ConstantsDB.FIELD_TYPE_FLOAT:
                                value = cursor.getFloat(indexParam);
                                break;

                            case ConstantsDB.FIELD_TYPE_STRING:
                                value = cursor.getString(indexParam);
                                break;

                            default:
                                value = null;
                        }
                    }

                    reading.setValue(columnName, value);
                }

                returnList.add(reading);
            } while (cursor.moveToNext());
        }
        cursor.close();
        DATABASE.close();
        return returnList;
    }

    /////////////////////////////////////////////////////////////
    // STORE
    /////////////////////////////////////////////////////////////


    public synchronized void deleteTableIfExists(long sensorID){
        String sql = "DROP TABLE IF EXISTS " + getTableName(sensorID) + ";";
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    public synchronized void createTableIfNotExists(GeneralSensorConfiguration config){
        //Log.d(LOG_TAG, "Create table " + tableName);
        String sql = "CREATE TABLE IF NOT EXISTS " + getTableName(config.getSensorID()) + " ( " +
                ConstantsDB.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ConstantsDB.TIMESTAMP + " INTEGER";

        ArrayList<String> paramNames = config.getParametersNames();
        ArrayList<String> paramTypes = config.getParametersTypes();

        for (int i = 0; i < config.getDimension(); i++){
            String type = "";
            switch (paramTypes.get(i)){
                case "int":
                    type = "INT";
                    break;
                case "double":
                    type = "REAL";
                    break;
                case "String":
                    type = "TEXT";
            }

            sql += ", " + paramNames.get(i) + " " + type;
        }
        sql += " );";
        SQLiteDatabase db = getWritableDatabase();
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            DATABASE.enableWriteAheadLogging();
        }*/
        db.execSQL(sql);
        db.close();
    }


    public synchronized void store(ArrayList<? extends SensorReading> readings){
        //Log.d("NervousnetDBmanager", "" + readings);
        SQLiteDatabase db = this.getWritableDatabase();

        for (SensorReading reading : readings) {

            //Log.d("NervousnetDBmanager", "Store " + reading);

            ContentValues insertList = new ContentValues();

            insertList.put(ConstantsDB.TIMESTAMP, reading.getTimestampEpoch());

            ArrayList<String> paramNames = reading.getParametersNames();
            ArrayList<Object> values = reading.getValues();

            for (int i = 0; i < paramNames.size(); i++) {
                String name = paramNames.get(i);
                Object val = values.get(i);

                if (val.getClass().equals(String.class))
                    insertList.put(name, (String) val);


                else if (val instanceof Integer)
                    insertList.put(name, (Integer) val);


                else if (val instanceof Double)
                    insertList.put(name, (Double) val);


                else if (val instanceof Float)
                    insertList.put(name, (Float) val);


                else if (val instanceof Long)
                    insertList.put(name, (Long) val);

                else
                    insertList.putNull(name);

            }

            db.insert(getTableName(reading.getSensorID()), null, insertList);
        }
        db.close();
    }


    public void store(SensorReading reading){
        //Log.d(LOG_TAG, "Latest reading " + reading);
        LATEST_SENSORS_DATA.put(reading.getSensorID(), reading);
        if (TEMPORARY_STORAGE.containsKey(reading.getSensorID())) {
            ArrayList<SensorReading> readings = TEMPORARY_STORAGE.get(reading.getSensorID());
            readings.add(reading);
        }
        else {
            ArrayList<SensorReading> newArray = new ArrayList();
            newArray.add(reading);
            TEMPORARY_STORAGE.put(reading.getSensorID(), newArray);
        }
    }

    public synchronized void removeOldReadings(long sensorID, long threshold){
        String sql = "DELETE FROM " + String.valueOf(sensorID) + " WHERE " + ConstantsDB.TIMESTAMP + " < " + threshold + ";";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    private static String getTableName(long sensorID){
        return "ID" + String.valueOf(sensorID);
    }

    @Override
    public void run() {

        HashMap<Long, ArrayList<SensorReading>> tmp = TEMPORARY_STORAGE;
        TEMPORARY_STORAGE = new HashMap<>();
        for (ArrayList<SensorReading> readings : tmp.values()){
            store(readings);
            Log.d("STORE", "Store " + readings.get(0).getSensorName() + " size " + readings.size());
        }

    }
}
