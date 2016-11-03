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

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationMap;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationGeneralSensor;


/**
 * Created by ales on 18/10/16.
 */
public class NervousnetManagerDB extends SQLiteOpenHelper {

    private int cacheSize = 5; // TODO: this is just temporary
    SQLiteDatabase DATABASE = this.getWritableDatabase();

    // Defining a singleton
    private static NervousnetManagerDB instance = null;
    public static NervousnetManagerDB getInstance(Context context){
        if (instance == null){
            instance = new NervousnetManagerDB(context);
        }
        return instance;
    }

    public static NervousnetManagerDB getInstance(Context context, long readingAgeThreshold){
        if (instance == null){
            instance = new NervousnetManagerDB(context);
        }
        instance.readingAgeThreshold = readingAgeThreshold;
        return instance;
    }

    private static HashMap<String, SensorReading> LATEST_SENSORS_DATA = new HashMap<>();
    private HashMap<String, ArrayList<SensorReading>> TEMPORARY_STORAGE = new HashMap<>();

    private long readingAgeThreshold = 864000;

    private NervousnetManagerDB(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    /////////////////////////////////////////////////////////////
    // QUERY
    /////////////////////////////////////////////////////////////


    public static SensorReading getLatestReading(String uniqueName){
        SensorReading reading = LATEST_SENSORS_DATA.get(uniqueName);
        return reading;
    }

    public ArrayList<SensorReading> getReadings(String sensorName){
        String query = "SELECT * FROM " + sensorName;
        return getReadings(sensorName, query);
    }

    public ArrayList<SensorReading> getReadings(String sensorName, long start, long stop){
        String query = "SELECT * FROM " + sensorName + " WHERE " + Constants.TIMESTAMP + " >= " + start + " AND " + Constants.TIMESTAMP + " <= " + stop + ";";
        return getReadings(sensorName, query);
    }

    public ArrayList<SensorReading> getReadings(String sensorName, long start, long stop,
                                                ArrayList<String> selectColumns){
        String cols = TextUtils.join(", ", selectColumns);
        String query = "SELECT " + Constants.ID + ", " + Constants.TIMESTAMP + ", " + cols + " " +
                "FROM " + sensorName + " WHERE " + Constants.TIMESTAMP + " >= " + start + " AND " +
                "" + Constants.TIMESTAMP + " <= " + stop + ";";
        return getReadings(sensorName, query);
    }


    public ArrayList<SensorReading> getLatestReadingUnderRange(String sensorName,
                                                               long start, long stop,
                                                               ArrayList<String> selectColumns) {
        String cols = TextUtils.join(", ", selectColumns);
        String query = "SELECT " + Constants.ID + ", MAX(" + Constants.TIMESTAMP + ") AS " + Constants.TIMESTAMP+ ", " + cols + " " +
                "FROM " + sensorName + " WHERE " + Constants.TIMESTAMP + " >= " + start + " AND " +
                "" + Constants.TIMESTAMP + " <= " + stop + ";";

        ArrayList<SensorReading> readings = getReadings(sensorName, query);
        return readings;
    }

    public ArrayList<SensorReading> getReadings(String sensorName, String query) {

        ArrayList<String> sensorParamNames = ConfigurationMap.getSensorConfig(sensorName).getParametersNames();

        // 2. get reference to writable DB
        //SQLiteDatabase db = this.getReadableDatabase();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            DATABASE.enableWriteAheadLogging();
        }
        Cursor cursor = DATABASE.rawQuery(query, null);

        ArrayList<SensorReading> returnList = new ArrayList<>();
        //Log.d("CURSOR", "Count " + cursor.getCount() );
        // 3. go over each row, build sensor value and add it to list
        if (cursor.moveToFirst()) {
            do {
                // Update timestamp and values
                int indexTimestamp = cursor.getColumnIndex(Constants.TIMESTAMP);
                SensorReading reading = new SensorReading(sensorName, sensorParamNames);
                reading.setTimestampEpoch(cursor.getLong(indexTimestamp));

                //Log.d("CURSOR", "Timestamp " + reading.getTimestampEpoch());

                for (String columnName : sensorParamNames){
                    int indexParam = cursor.getColumnIndex(columnName);
                    Object value = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                        int type = cursor.getType(indexParam);
                        switch (type) {
                            case Constants.FIELD_TYPE_INTEGER:
                                value = cursor.getInt(indexParam);
                                break;

                            case Constants.FIELD_TYPE_FLOAT:
                                value = cursor.getFloat(indexParam);
                                break;

                            case Constants.FIELD_TYPE_STRING:
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
        //DATABASE.close();
        return returnList;
    }

    /////////////////////////////////////////////////////////////
    // STORE
    /////////////////////////////////////////////////////////////


    public synchronized void deleteTableIfExists(String sensorName){
        String sql = "DROP TABLE IF EXISTS " + sensorName + ";";
        //SQLiteDatabase db = getWritableDatabase();
        DATABASE.execSQL(sql);
        //db.close();
    }

    public synchronized void createTableIfNotExists(String sensorName){
        //Log.d(LOG_TAG, "Create table " + tableName);
        String sql = "CREATE TABLE IF NOT EXISTS " + sensorName + " ( " +
                Constants.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Constants.TIMESTAMP + " INTEGER";

        ConfigurationGeneralSensor confSensor = ConfigurationMap.getSensorConfig(sensorName);
        ArrayList<String> paramNames = confSensor.getParametersNames();
        ArrayList<String> paramTypes = confSensor.getParametersTypes();

        for (int i = 0; i < confSensor.getDimension(); i++){
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
        //SQLiteDatabase db = getWritableDatabase();
/*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            DATABASE.enableWriteAheadLogging();
        }*/
        DATABASE.execSQL(sql);
        //DATABASE.close();
    }


    public synchronized void store(ArrayList<? extends SensorReading> readings){
        //Log.d("NervousnetDBmanager", "" + readings);
        //SQLiteDatabase db = this.getWritableDatabase();

        SensorReading latest = null;
        for (SensorReading reading : readings) {

            //Log.d("NervousnetDBmanager", "Store " + reading);

            ContentValues insertList = new ContentValues();

            insertList.put(Constants.TIMESTAMP, reading.getTimestampEpoch());

            ConfigurationGeneralSensor conf = ConfigurationMap.getSensorConfig(reading.getSensorName());
            ArrayList<String> paramNames = conf.getParametersNames();
            ArrayList<Object> values = reading.getValues();

            for (int i = 0; i < conf.getDimension(); i++) {
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

            DATABASE.insert(reading.getSensorName(), null, insertList);
            latest = reading;
        }
        //DATABASE.close();
        if (latest != null)
            LATEST_SENSORS_DATA.put(latest.getSensorName(), latest);
    }


    public synchronized void store(SensorReading reading){

        ArrayList<SensorReading> readings = null;
        if (TEMPORARY_STORAGE.containsKey(reading.getSensorName())) {
            readings = TEMPORARY_STORAGE.get(reading.getSensorName());
            readings.add(reading);
            if (readings.size() > cacheSize){
                TEMPORARY_STORAGE.put(reading.getSensorName(), new ArrayList());
                Log.d("STORE", "Store " + reading.getSensorName());
                try {
                    store(readings);
                } catch (Exception e){
                    TEMPORARY_STORAGE.get(reading.getSensorName()).addAll(readings);
                }
            }
        }
        else {
            ArrayList<SensorReading> newArray = new ArrayList();
            newArray.add(reading);
            TEMPORARY_STORAGE.put(reading.getSensorName(), newArray);
            return;
        }
    }

    public synchronized void removeOldReadings(String sensorName, long threshold){
        String sql = "DELETE FROM " + sensorName + " WHERE " + Constants.TIMESTAMP + " < " + threshold + ";";
        //SQLiteDatabase db = this.getWritableDatabase();
        DATABASE.execSQL(sql);
        //DATABASE.close();
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

}
