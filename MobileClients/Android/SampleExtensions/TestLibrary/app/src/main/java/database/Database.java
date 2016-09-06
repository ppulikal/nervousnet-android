package database;

/**
 * Created by ales on 27/06/16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import virtual.ClusterVirtualSensorPoint;
import virtual.OriginalVirtualSensorPoint;
import virtual.VirtualPoint;

public class Database extends SQLiteOpenHelper implements iDatabase {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "VirtualSensorDB";
    private static final String TABLE = "VirtualSensor";
    private static final String KEY_ID = "id";

    private static final String timestamp = "VTimestamp";
    private static final String vnoise = "VNoise";
    private static final String vlight = "VLight";
    private static final String vbattery = "VBattery";
    private static final String vaccX = "VaccX";
    private static final String vaccY = "VaccY";
    private static final String vaccZ = "VaccZ";
    private static final String vgyroX = "VgyroX";
    private static final String vgyroY = "VgyroY";
    private static final String vgyroZ = "VgyroZ";
    private static final String vproxim = "VProximity";

    private static final String onoise = "ONoise";
    private static final String olight = "OLight";
    private static final String obattery = "OBattery";
    private static final String oaccX = "OaccX";
    private static final String oaccY = "OaccY";
    private static final String oaccZ = "OaccZ";
    private static final String ogyroX = "OgyroX";
    private static final String ogyroY = "OgyroY";
    private static final String ogyroZ = "OgyroZ";
    private static final String oproxim = "OProximity";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create table
        String CREATE_TABLE = "CREATE TABLE "+TABLE+" ( " +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                // Virtual sensor data
                timestamp +" NUMERIC," +

                vnoise +" REAL," +
                vlight  +" REAL," +
                vbattery  +" REAL," +
                vaccX  +" REAL," +
                vaccY +" REAL," +
                vaccZ +" REAL," +
                vgyroX +" REAL," +
                vgyroY +" REAL," +
                vgyroZ +" REAL," +
                vproxim +" REAL," +
                // Original sensor data
                onoise +" REAL," +
                olight +" REAL," +
                obattery +" REAL," +
                oaccX +" REAL," +
                oaccY +" REAL," +
                oaccZ +" REAL," +
                ogyroX +" REAL," +
                ogyroY +" REAL," +
                ogyroZ +" REAL," +
                oproxim +" REAL" +
                ")";
        // create table
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS "+TABLE);

        // create fresh table
        this.onCreate(db);
    }

    public void add(double timestampd,
                    double vnoised,
                    double vlightd,
                    double vbatteryd,
                    double vaccxd,
                    double vaccyd,
                    double vacczd,
                    double vgyroxd,
                    double vgyroyd,
                    double vgyrozd,
                    double vproxd,
                    double onoised,
                    double olightd,
                    double obatteryd,
                    double oaccxd,
                    double oaccyd,
                    double oacczd,
                    double ogyroxd,
                    double ogyroyd,
                    double ogyrozd,
                    double oproxd){

        Log.d("DATABASE", "Start inserting values ...");

        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();

        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        values.put(timestamp, timestampd);
        values.put(vnoise, vnoised);
        values.put(vlight, vlightd);
        values.put(vbattery, vbatteryd);
        values.put(vaccX, vaccxd);
        values.put(vaccY, vaccyd);
        values.put(vaccZ, vacczd);
        values.put(vgyroX, vgyroxd);
        values.put(vgyroY, vgyroyd);
        values.put(vgyroZ, vgyrozd);
        values.put(vproxim, vproxd);

        values.put(onoise, onoised);
        values.put(olight, olightd);
        values.put(obattery, obatteryd);
        values.put(oaccX, oaccxd);
        values.put(oaccY, oaccyd);
        values.put(oaccZ, oacczd);
        values.put(ogyroX, ogyroxd);
        values.put(ogyroY, ogyroyd);
        values.put(ogyroZ, ogyrozd);
        values.put(oproxim, oproxd);
        // 3. insert

        db.insert(TABLE,    // table
                null,       //nullColumnHack
                values);    // key/value -> keys = column names/ values = column values

        // 4. close
        db.close();
        Log.d("DATABASE", "Inserting values done");
    }

    @Override
    public void update(double timestampd, double vnoised, double vlightd, double vbatteryd,
                       double vaccxd, double vaccyd, double vacczd, double vgyroxd, double vgyroyd,
                       double vgyrozd, double vproxd, double onoised, double olightd, double obatteryd,
                       double oaccxd, double oaccyd, double oacczd, double ogyroxd, double ogyroyd,
                       double ogyrozd, double oproxd) {

    }



    @Override
    public VirtualPoint get(double timestampQuery) {
        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE+" WHERE "+timestamp+" = "+timestampQuery, null);
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build object

        VirtualPoint vpoint = getVSP(cursor);

        Log.d("DB-get-original", Arrays.toString(vpoint.getOriginal().getValues()));
        Log.d("DB-get-cluster", Arrays.toString(vpoint.getCluster().getValues()));
        // 5. return
        return vpoint;
    }

    @Override
    public void delete(double timestampDelete) {
        Log.d("DATABASE", "Delete element " + timestampDelete);
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "DELETE FROM "+TABLE+" WHERE "+timestamp+" = "+timestampDelete;
        db.execSQL(sql);
    }

    @Override
    public void deleteTable() {
        Log.d("DATABASE", "Delete table " + TABLE);
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "DROP TABLE "+TABLE;
        db.execSQL(sql);
    }

    @Override
    public void initTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        onCreate(db);
    }

    public VirtualPoint get(int id){

        // 1. get reference to readable DB
        SQLiteDatabase db = this.getReadableDatabase();

        // 2. build query
        Cursor cursor =
                db.query(TABLE,     // a. table
                        null,       // b. column names
                        null,       // c. selections
                        null,       // d. selections args
                        null,       // e. group by
                        null,       // f. having
                        null,       // g. order by
                        null);      // h. limit

        // 3. if we got results get the first one
        if (cursor != null)
            cursor.moveToFirst();

        // 4. build object

        VirtualPoint vpoint = getVSP(cursor);

        Log.d("DB-get-original", Arrays.toString(vpoint.getOriginal().getValues()));
        Log.d("DB-get-cluster", Arrays.toString(vpoint.getCluster().getValues()));
        // 5. return
        return vpoint;
    }

    private VirtualPoint getVSP(Cursor cursor){

        OriginalVirtualSensorPoint original = new OriginalVirtualSensorPoint();
        ClusterVirtualSensorPoint cluster = new ClusterVirtualSensorPoint();

        cluster.setNoise( cursor.getDouble(2));
        cluster.setLight( cursor.getDouble(3));
        cluster.setBattery( cursor.getDouble(4));
        cluster.setAccelerometer( cursor.getDouble(5),
                cursor.getDouble(6),
                cursor.getDouble(7));
        cluster.setGyrometer( cursor.getDouble(8),
                cursor.getDouble(9),
                cursor.getDouble(10));
        cluster.setProximity( cursor.getDouble(11));

        cluster.setNoise( cursor.getDouble(12));
        cluster.setLight( cursor.getDouble(13));
        cluster.setBattery( cursor.getDouble(14));
        cluster.setAccelerometer( cursor.getDouble(15),
                cursor.getDouble(16),
                cursor.getDouble(17));
        cluster.setGyrometer( cursor.getDouble(18),
                cursor.getDouble(19),
                cursor.getDouble(20));
        cluster.setProximity( cursor.getDouble(21));


        return new VirtualPoint(cluster, original);
    }

    public ArrayList<VirtualPoint> getAll() {
        ArrayList<VirtualPoint> values = new ArrayList();

        // 1. build the query
        String query = "SELECT  * FROM " + TABLE;

        // 2. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        // 3. go over each row, build sensor value and add it to list
        if (cursor.moveToFirst()) {
            do {
                VirtualPoint vp = getVSP(cursor);
                values.add(vp);
            } while (cursor.moveToNext());
        }
        return values;
    }

    public static void test(Context context){
        Log.d("TEST", "Start ...");

        Database db = new Database(context);
        db.add(1, 2, 3, 4, 5, 6, 7, 8, 9,
                1, 2, 3, 4, 5, 6, 7, 8, 9,
                1, 2, 3
        );

        // Test reading values
        ArrayList<VirtualPoint> list = db.getAll();
        for( VirtualPoint vp : list )
            Log.d("VP",Arrays.toString(vp.getCluster().getValues()) + " " + Arrays.toString(vp.getOriginal().getValues()));

        Log.d("TEST", "Stop");
    }
}
