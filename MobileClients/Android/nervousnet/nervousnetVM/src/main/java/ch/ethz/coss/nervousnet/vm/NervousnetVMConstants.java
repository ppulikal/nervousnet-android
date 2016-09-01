package ch.ethz.coss.nervousnet.vm;

public class NervousnetVMConstants {

    public static final byte STATE_PAUSED = 0;
    public static final byte STATE_RUNNING = 1;

    /******************
     * Preferences
     ****************/
    public final static String SENSOR_PREFS = "SensorPreferences";
    public final static String SENSOR_FREQ = "SensorFrequencies";
    public final static String SERVICE_PREFS = "ServicePreferences";
    public final static String UPLOAD_PREFS = "UploadPreferences";

    public static byte SENSOR_STATE_NOT_AVAILABLE = -2;
    public static byte SENSOR_STATE_AVAILABLE_PERMISSION_DENIED = -1;
    public static byte SENSOR_STATE_AVAILABLE_BUT_OFF = 0;
    public static byte SENSOR_STATE_AVAILABLE_DELAY_LOW = 1;
    public static byte SENSOR_STATE_AVAILABLE_DELAY_MED = 2;
    public static byte SENSOR_STATE_AVAILABLE_DELAY_HIGH = 3;


    public static int REQUEST_CODE_ASK_PERMISSIONS_LOC = 1;
    public static int REQUEST_CODE_ASK_PERMISSIONS_NOISE = 2;

    public static long[] sensor_ids = {0, 1, 2, 3, 4, 5, 6};

    public static String[] sensor_labels = {"ACCELEROMETER", "BATTERY", "GYROSCOPE",
            "LOCATION", "LIGHT", "NOISE", "PROXIMITY"};

    public static byte[] sensor_default_states = {SENSOR_STATE_AVAILABLE_DELAY_HIGH, SENSOR_STATE_AVAILABLE_DELAY_HIGH, SENSOR_STATE_AVAILABLE_DELAY_HIGH,
            SENSOR_STATE_AVAILABLE_BUT_OFF, SENSOR_STATE_AVAILABLE_DELAY_HIGH, SENSOR_STATE_AVAILABLE_BUT_OFF, SENSOR_STATE_AVAILABLE_DELAY_HIGH};

//	public static long[] sensor_ids = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
//
//	public static String[] sensor_labels = { "Accelerometer", "Battery", "Beacons", "Connectivity", "Gyroscope",
//			"Humidity", "Location", "Light", "Magnetic", "Noise", "Pressure", "Proximity", "Temperature" };

    public static String[] sensor_freq_labels = {"Off", "Low", "Medium", "High"};

    public static int[][] sensor_freq_constants = {{-1, 1000000, 250000, 60000}, {-1, 300000, 120000, 60000},
            {-1, 1000000, 250000, 60000}, {-1, 300000, 120000, 60000}, {-1, 300000, 120000, 60000}, {-1, 300000, 120000, 60000},
            {-1, 300000, 120000, 60000}
    };

    public static byte EVENT_PAUSE_NERVOUSNET_REQUEST = 0;
    public static byte EVENT_START_NERVOUSNET_REQUEST = 1;
    public static byte EVENT_CHANGE_SENSOR_STATE_REQUEST = 2;
    public static byte EVENT_CHANGE_ALL_SENSORS_STATE_REQUEST = 3;
    public static byte EVENT_NERVOUSNET_STATE_UPDATED = 4;
    public static byte EVENT_SENSOR_STATE_UPDATED = 5;


}
