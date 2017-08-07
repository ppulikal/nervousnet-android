package ch.ethz.coss.nervousnet.vm;

public class NervousnetVMConstants {

    public static final byte STATE_PAUSED = 0;
    public static final byte STATE_RUNNING = 1;

    public final static byte SENSOR_STATE_NOT_AVAILABLE = -2;
    public final static byte SENSOR_STATE_AVAILABLE_PERMISSION_DENIED = -1;
    public final static byte SENSOR_STATE_AVAILABLE_BUT_OFF = 0;
    public final static byte SENSOR_STATE_AVAILABLE_DELAY_LOW = 1;
    public final static byte SENSOR_STATE_AVAILABLE_DELAY_MED = 2;
    public final static byte SENSOR_STATE_AVAILABLE_DELAY_HIGH = 3;


    public static final long[] sensorIDs = new long[]{1, 2, 4, 6, 3, 5, 7};
    public static String[] sensor_labels = {"ACCELEROMETER", "BATTERY", "GYROSCOPE",
            "LOCATION", "LIGHT", "NOISE", "PROXIMITY"};

    public static String[] sensor_freq_labels = {"Off", "Low", "Medium", "High"};

    public static byte EVENT_PAUSE_NERVOUSNET_REQUEST = 0;
    public static byte EVENT_START_NERVOUSNET_REQUEST = 1;
    public static byte EVENT_CHANGE_SENSOR_STATE_REQUEST = 2;
    public static byte EVENT_CHANGE_ALL_SENSORS_STATE_REQUEST = 3;
    public static byte EVENT_NERVOUSNET_STATE_UPDATED = 4;
    public static byte EVENT_SENSOR_STATE_UPDATED = 5;


}
