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
import java.util.NoSuchElementException;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationMap;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationGeneralSensor;


/**
 * Created by ales on 18/10/16.
 */
public class NervousnetManagerDB extends SQLiteOpenHelper {

    private int cacheSize = 20; // TODO: this is just temporary
    SQLiteDatabase DATABASE = this.getWritableDatabase();

    // Defining a singleton
    private static NervousnetManagerDB instance = null;
    public static NervousnetManagerDB getInstance(Context context){
        if (instance == null){
            instance = new NervousnetManagerDB(context);
        }
        return instance;
    }

    private static HashMap<Long, SensorReading> LATEST_SENSORS_DATA = new HashMap<>();
    private HashMap<Long, ArrayList<SensorReading>> TEMPORARY_STORAGE = new HashMap<>();

    private long readingAgeThreshold = 864000;

    private NervousnetManagerDB(Context context) {
        super(context, ConstantsDB.DATABASE_NAME, null, ConstantsDB.DATABASE_VERSION);
        createConfigTableIfNotExists();
    }

    public static NervousnetManagerDB getInstance(Context context, long readingAgeThreshold){
        if (instance == null){
            instance = new NervousnetManagerDB(context);
        }
        instance.readingAgeThreshold = readingAgeThreshold;
        return instance;
    }

    /////////////////////////////////////////////////////////////
    // QUERY
    /////////////////////////////////////////////////////////////


    public static SensorReading getLatestReading(long sensorID){
        SensorReading reading = LATEST_SENSORS_DATA.get(sensorID);
        return reading;
    }

    public ArrayList<SensorReading> getReadings(long sensorID){
        String query = "SELECT * FROM " + getTableName(sensorID);
        return getReadings(sensorID, query);
    }

    public ArrayList<SensorReading> getReadings(long sensorID, long start, long stop){
        String query = "SELECT * FROM " + getTableName(sensorID) + " WHERE " + ConstantsDB.TIMESTAMP + " >= " + start + " AND " + ConstantsDB.TIMESTAMP + " <= " + stop + ";";
        return getReadings(sensorID, query);
    }

    public ArrayList<SensorReading> getReadings(long sensorID, long start, long stop,
                                                ArrayList<String> selectColumns){
        String cols = TextUtils.join(", ", selectColumns);
        String query = "SELECT " + ConstantsDB.ID + ", " + ConstantsDB.TIMESTAMP + ", " + cols + " " +
                "FROM " + getTableName(sensorID) + " WHERE " + ConstantsDB.TIMESTAMP + " >= " + start + " AND " +
                "" + ConstantsDB.TIMESTAMP + " <= " + stop + ";";
        return getReadings(sensorID, query);
    }


    public ArrayList<SensorReading> getLatestReadingUnderRange(long sensorID,
                                                               long start, long stop,
                                                               ArrayList<String> selectColumns) {
        String cols = TextUtils.join(", ", selectColumns);
        String query = "SELECT " + ConstantsDB.ID + ", MAX(" + ConstantsDB.TIMESTAMP + ") AS " + ConstantsDB.TIMESTAMP+ ", " + cols + " " +
                "FROM " + getTableName(sensorID) + " WHERE " + ConstantsDB.TIMESTAMP + " >= " + start + " AND " +
                "" + ConstantsDB.TIMESTAMP + " <= " + stop + ";";

        ArrayList<SensorReading> readings = getReadings(sensorID, query);
        return readings;
    }

    public ArrayList<SensorReading> getReadings(long sensorID, String query) {

        ConfigurationGeneralSensor conf = ConfigurationMap.getSensorConfig(sensorID);
        ArrayList<String> sensorParamNames = conf.getParametersNames();

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
                int indexTimestamp = cursor.getColumnIndex(ConstantsDB.TIMESTAMP);
                SensorReading reading = new SensorReading(sensorID, conf.getSensorName(), sensorParamNames);
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
        //DATABASE.close();
        return returnList;
    }

    /////////////////////////////////////////////////////////////
    // STORE
    /////////////////////////////////////////////////////////////


    public synchronized void deleteTableIfExists(long sensorID){
        String sql = "DROP TABLE IF EXISTS " + getTableName(sensorID) + ";";
        //SQLiteDatabase db = getWritableDatabase();
        DATABASE.execSQL(sql);
        //db.close();
    }

    public synchronized void createTableIfNotExists(long sensorID){
        //Log.d(LOG_TAG, "Create table " + tableName);
        String sql = "CREATE TABLE IF NOT EXISTS " + getTableName(sensorID) + " ( " +
                ConstantsDB.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ConstantsDB.TIMESTAMP + " INTEGER";

        ConfigurationGeneralSensor confSensor = ConfigurationMap.getSensorConfig(sensorID);
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

            insertList.put(ConstantsDB.TIMESTAMP, reading.getTimestampEpoch());

            ConfigurationGeneralSensor conf = ConfigurationMap.getSensorConfig(reading.getSensorID());
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

            DATABASE.insert(getTableName(conf.getSensorID()), null, insertList);
            latest = reading;
        }
        //DATABASE.close();
    }


    public void store(SensorReading reading){
        LATEST_SENSORS_DATA.put(reading.getSensorID(), reading);


        ArrayList<SensorReading> readings = null;
        if (TEMPORARY_STORAGE.containsKey(reading.getSensorID())) {
            readings = TEMPORARY_STORAGE.get(reading.getSensorID());
            readings.add(reading);
            if (readings.size() > cacheSize){
                TEMPORARY_STORAGE.put(reading.getSensorID(), new ArrayList());
                Log.d("STORE", "Store " + reading.getSensorName());
                try {
                    store(readings);
                } catch (Exception e){
                    TEMPORARY_STORAGE.get(reading.getSensorID()).addAll(readings);
                }
            }
        }
        else {
            ArrayList<SensorReading> newArray = new ArrayList();
            newArray.add(reading);
            TEMPORARY_STORAGE.put(reading.getSensorID(), newArray);
            return;
        }
    }

    public synchronized void removeOldReadings(long sensorID, long threshold){
        String sql = "DELETE FROM " + String.valueOf(sensorID) + " WHERE " + ConstantsDB.TIMESTAMP + " < " + threshold + ";";
        //SQLiteDatabase db = this.getWritableDatabase();
        DATABASE.execSQL(sql);
        //DATABASE.close();
    }

    /////////////////////////////////////////////////////////////
    // STORE CONFIGURATION DATA
    /////////////////////////////////////////////////////////////

    public synchronized void createConfigTableIfNotExists(){
        //Log.d(LOG_TAG, "Create table " + tableName);
        String sql = "CREATE TABLE IF NOT EXISTS " + ConstantsDB.CONFIG_TABLE + " ( " +
                ConstantsDB.ID + " INTEGER PRIMARY KEY, "+ ConstantsDB.STATE+" INTEGER);";
        DATABASE.execSQL(sql);
    }

    public synchronized void storeState(long sensorID, int state){
        ContentValues insertList = new ContentValues();
        insertList.put(ConstantsDB.ID, sensorID);
        insertList.put(ConstantsDB.STATE, state);
        DATABASE.insertWithOnConflict(ConstantsDB.CONFIG_TABLE, null, insertList, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public synchronized int getState(long sensorID, int state) throws NoSuchElementException{
        String query = "SELECT " + ConstantsDB.STATE + " FROM " + ConstantsDB.CONFIG_TABLE + " WHERE " +
                "" + ConstantsDB.ID + " == " + sensorID + " ;";
        Cursor cursor = DATABASE.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getInt(0);
        }
        else {
            throw new NoSuchElementException("Config table has no config info for the id="+sensorID);
        }
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

}
