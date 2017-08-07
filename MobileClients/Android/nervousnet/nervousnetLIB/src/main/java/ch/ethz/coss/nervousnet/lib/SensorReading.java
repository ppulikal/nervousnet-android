package ch.ethz.coss.nervousnet.lib;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;

/**
 * Created by ales on 20/09/16.
 */
public class SensorReading implements Parcelable {

    public static final Creator<SensorReading> CREATOR = new Creator<SensorReading>() {
        @Override
        public SensorReading createFromParcel(Parcel in) {
            return new SensorReading(in);
        }

        @Override
        public SensorReading[] newArray(int size) {
            return new SensorReading[size];
        }
    };
    // Description
    protected long sensorID;
    protected String sensorName;         // sensor name
    protected long timestampEpoch;     // timestamp
    protected ArrayList<String> parametersNames;    // list of parameters' names
    // Data
    protected ArrayList values;

    // Constructor
    public SensorReading() {
    }

    public SensorReading(long sensorID, String sensorName,
                         ArrayList<String> paramNames) {
        this.sensorID = sensorID;
        this.sensorName = sensorName;
        this.parametersNames = paramNames;
        this.values = new ArrayList();
        for (String name : parametersNames)
            values.add(0);
    }

    protected SensorReading(Parcel in) {
        readFromParcel(in);
    }

    public String getSensorName() {
        return sensorName;
    }

    public long getTimestampEpoch() {
        return timestampEpoch;
    }

    public void setTimestampEpoch(long timestamp) {
        this.timestampEpoch = timestamp;
    }

    public ArrayList<String> getParametersNames() {
        return parametersNames;
    }

    public void setParametersNames(ArrayList<String> paramNames) {
        this.parametersNames = paramNames;
        if (values == null || values.size() != paramNames.size())
            this.values = new ArrayList();
    }

    public ArrayList getValues() {
        return values;
    }

    public void setValues(ArrayList values) {
        this.values = values;
    }

    public void setValue(String paramName, Object value) {
        int index = this.parametersNames.indexOf(paramName);
        this.values.set(index, value);
    }


    // Context for communication

    public long getSensorID() {
        return sensorID;
    }

    public void setSensorID(int id) {
        this.sensorID = id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeLong(sensorID);
        parcel.writeString(sensorName);
        parcel.writeLong(timestampEpoch);
        parcel.writeStringList(parametersNames);
        // Pass values seperately, not as a list, as the types can be different
        parcel.writeList(values);
    }

    // if the class id extended, one can override this function to satisfy reading
    public void readFromParcel(Parcel in) {
        sensorID = in.readLong();
        sensorName = in.readString();
        timestampEpoch = in.readLong();

        if (parametersNames == null)
            parametersNames = new ArrayList<String>();

        in.readStringList(parametersNames);
        if (values == null)
            values = new ArrayList<Object>();

        in.readList(values, Object.class.getClassLoader());

    }


    @Override
    public String toString() {
        return "SensorReading{" +
                "id=" + sensorID +
                ", sensorName='" + sensorName + '\'' +
                ", timestampEpoch=" + timestampEpoch +
                ", parametersNames=" + parametersNames +
                ", values=" + TextUtils.join(", ", values) +
                '}';
    }
}
