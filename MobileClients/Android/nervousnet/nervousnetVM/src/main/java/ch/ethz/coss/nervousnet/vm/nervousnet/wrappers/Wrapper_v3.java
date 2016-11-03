package ch.ethz.coss.nervousnet.vm.nervousnet.wrappers;

import android.content.Context;
import android.hardware.SensorEvent;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationMap;
import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationBasicSensor;

/**
 * Created by ales on 20/09/16.
 */
public class Wrapper_v3 extends aWrapper {

    private static final String LOG_TAG = Wrapper_v3.class.getSimpleName();

    private final int[] parametersPositions;

    // This is actually extraction from config file
    public Wrapper_v3(Context context, String sensorName){
        super(context, sensorName);
        ConfigurationBasicSensor confSensor = (ConfigurationBasicSensor) ConfigurationMap.getSensorConfig(sensorName);
        this.parametersPositions = confSensor.getAndroidParametersPositions();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        long timestamp = System.currentTimeMillis();

        SensorReading reading = new SensorReading(sensorName, paramNames);
        reading.setTimestampEpoch(timestamp);

        // 2. Insert values
        //Log.d(LOG_TAG, "timestamp " + sample.get("timestamp") + " sampling " + sample.get("samplingPeriod") + " metadata " + sample.get("metadata") );
        for (int i = 0; i < parametersPositions.length; i++) {
            Object val = sensorEvent.values[parametersPositions[i]];
            reading.setValue(paramNames.get(i), val);
        }

        this.push(reading);
    }
}
