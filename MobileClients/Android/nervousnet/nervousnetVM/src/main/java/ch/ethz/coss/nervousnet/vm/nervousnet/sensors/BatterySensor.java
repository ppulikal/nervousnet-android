package ch.ethz.coss.nervousnet.vm.nervousnet.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;

import ch.ethz.coss.nervousnet.lib.SensorReading;
import ch.ethz.coss.nervousnet.vm.NNLog;

public class BatterySensor extends BaseSensor {
    private static final String LOG_TAG = BatterySensor.class.getSimpleName();

    BroadcastReceiver batteryReceiver = new BroadcastReceiver() {
        int scale = -1;
        int level = -1;
        int voltage = -1;
        int temp = -1;

        @Override
        public void onReceive(Context context, Intent batteryStatus) {
            SensorReading reading = extractBatteryData(batteryStatus);
            push(reading);
            Log.d(LOG_TAG, "Received broadcast - " + reading);
            //Log.d(LOG_TAG, "level is " + level + "/" + scale + ", temp is " + temp + ", voltage is " + voltage);
        }

    };
    private Context context;

    public BatterySensor(Context context, String sensorName) {
        super(context, sensorName);
        this.context = context;
    }


    public void readBattery() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        SensorReading reading = extractBatteryData(batteryStatus);
        if (reading != null)
            push(reading);

    }

    static String[] PARAM_HARD_CODED = new String[]{"temp", "volt", "health", "level",
            "scale", "status", "chargePlug", "isCharging", "isChargingUBS", "isChargingAC",
            "tech", "percent"};

    private SensorReading extractBatteryData(Intent batteryStatus) {
        int temp = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        int volt = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        byte health = (byte) batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, 0);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL;
        boolean usbCharge = (chargePlug == BatteryManager.BATTERY_PLUGGED_USB);
        boolean acCharge = (chargePlug == BatteryManager.BATTERY_PLUGGED_AC);
        String technology = batteryStatus.getExtras().getString(BatteryManager.EXTRA_TECHNOLOGY);
        float percent = level / (float) scale;

        SensorReading reading = new SensorReading(sensorName, paramNames);
        reading.setTimestampEpoch(System.currentTimeMillis());
        reading.setValue("temp", temp);
        reading.setValue("volt", volt);
        reading.setValue("health", (int)health);
        reading.setValue("level", level);
        reading.setValue("scale", scale);
        reading.setValue("status", status);
        reading.setValue("chargePlug", chargePlug);
        reading.setValue("isCharging", isCharging ? 1 : 0);
        reading.setValue("isChargingUSB", usbCharge ? 1 : 0);
        reading.setValue("isChargingAC", acCharge ? 1 : 0);
        reading.setValue("tech", technology);
        reading.setValue("percent", percent);

        return reading;
    }


    @Override
    public boolean startListener() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(batteryReceiver, ifilter);
        SensorReading reading = extractBatteryData(batteryStatus);
        push(reading);
        return true;
    }

    @Override
    public boolean stopListener() {
        try {
            context.unregisterReceiver(batteryReceiver);
            return true;
        } catch (IllegalArgumentException e) {
            NNLog.d(LOG_TAG, "Exception trying to close battery sensor");
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
        return false;
    }

}