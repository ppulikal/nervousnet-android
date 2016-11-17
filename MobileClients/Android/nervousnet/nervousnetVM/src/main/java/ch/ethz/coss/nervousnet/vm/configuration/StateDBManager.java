package ch.ethz.coss.nervousnet.vm.configuration;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import ch.ethz.coss.nervousnet.vm.nervousnet.database.NoSuchElementInDBException;

/**
 * Created by ales on 16/11/16.
 */
public class StateDBManager extends SQLiteOpenHelper{

    public StateDBManager(Context context){
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
        createConfigTableIfNotExists();
        createNervousnetConfigTableIfNotExists();
    }

    public synchronized void createConfigTableIfNotExists(){
        //Log.d(LOG_TAG, "Create table " + tableName);
        String sql = "CREATE TABLE IF NOT EXISTS " + Constants.SENSOR_CONFIG_TABLE + " ( " +
                Constants.ID + " INTEGER PRIMARY KEY, "+ Constants.STATE+" INTEGER);";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    public synchronized void storeSensorState(long sensorID, int state){
        ContentValues insertList = new ContentValues();
        insertList.put(Constants.ID, sensorID);
        insertList.put(Constants.STATE, state);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insertWithOnConflict(Constants.SENSOR_CONFIG_TABLE, null, insertList, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public synchronized byte getSensorState(long sensorID) throws NoSuchElementInDBException {
        String query = "SELECT " + Constants.STATE + " FROM " +
                Constants.SENSOR_CONFIG_TABLE + " WHERE " +
                "" + Constants.ID + " == " + sensorID + " ;";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            long c = cursor.getInt(0);
            db.close();
            return (byte)c;
        }
        else {
            db.close();
            throw new NoSuchElementInDBException("Config table has no config info for the id="+sensorID);
        }
    }



    // NERVOUSNET STATE

    public synchronized void createNervousnetConfigTableIfNotExists(){
        //Log.d(LOG_TAG, "Create table " + tableName);
        String sql = "CREATE TABLE IF NOT EXISTS " + Constants.NERVOUSNET_CONFIG_TABLE + " ( " +
                Constants.ID + " INTEGER PRIMARY KEY, "+ Constants.STATE+" INTEGER);";
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(sql);
        db.close();
    }

    private int nervousnet_row_id = 0; // ID is always the same - we have only 1 row in the nervousnet table

    public synchronized void storeNervousnetState(byte state) {
        ContentValues insertList = new ContentValues();
        insertList.put(Constants.ID, nervousnet_row_id);
        insertList.put(Constants.STATE, state);
        SQLiteDatabase db = this.getWritableDatabase();
        db.insertWithOnConflict(Constants.NERVOUSNET_CONFIG_TABLE, null, insertList,
                SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    public synchronized byte getNervousnetState() throws NoSuchElementInDBException {
        String query = "SELECT " + Constants.STATE + " FROM " +
                Constants.NERVOUSNET_CONFIG_TABLE + " WHERE " +
                "" + Constants.ID + " == " + nervousnet_row_id + " ;";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            int c = cursor.getInt(0);
            db.close();
            return (byte) c;
        }
        else {
            db.close();
            throw new NoSuchElementInDBException("Nervousnet state has not been stored yet");
        }
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
