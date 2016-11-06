package ch.ethz.coss.nervousnet.vm.nervousnet.database;

/**
 * Created by ales on 26/09/16.
 */
public class ConstantsDB {

    // SQLite documentation
    public static final int FIELD_TYPE_NULL = 0;
    public static final int FIELD_TYPE_INTEGER = 1;
    public static final int FIELD_TYPE_FLOAT = 2;
    public static final int FIELD_TYPE_STRING = 3;
    public static final int FIELD_TYPE_BLOB = 4;


    public static final String DATABASE_NAME = "SensorDB";
    public static final int DATABASE_VERSION = 1;

    public static final String TIMESTAMP = "timestamp";
    public static final String ID = "ID";

    public static final String CONFIG_TABLE = "ConfigTable";
    public static final String STATE = "STATE";
}
