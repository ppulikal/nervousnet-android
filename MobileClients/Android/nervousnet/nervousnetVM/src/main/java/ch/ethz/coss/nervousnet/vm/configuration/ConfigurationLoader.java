package ch.ethz.coss.nervousnet.vm.configuration;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by ales on 21/09/16.
 */
public class ConfigurationLoader {

    private String CONF_FILE_NAME = "sensors_configuration.json";
    private static final String LOG_TAG = ConfigurationLoader.class.getSimpleName();
    private Context context;

    public ConfigurationLoader(Context context) {
        this.context = context;
    }

    public ArrayList<ConfigurationBasicSensor> load() {

        String line,line1 = "";
        try
        {
            //TODO
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(context.getAssets().open(CONF_FILE_NAME)));

            try
            {
                while ((line = reader.readLine()) != null)
                    line1+=line;
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }
        catch (Exception e)
        {
            Log.d(LOG_TAG, "ERROR " + e.getMessage());
        }

        return load(line1);
    }


    public static ArrayList<ConfigurationBasicSensor> load(String strJson){
        ArrayList<ConfigurationBasicSensor> list = new ArrayList();

        try {
            JSONArray sensorConfList = (new JSONObject(strJson)).getJSONArray("sensors_configurations");

            for (int i = 0; i < sensorConfList.length(); i++) {
                JSONObject sensorConf = sensorConfList.getJSONObject(i);

                ConfigurationBasicSensor confClass = null;
                // MANDATORY
                int sensorID = sensorConf.getInt("sensorID");
                String sensorName = sensorConf.getString("sensorName");
                ArrayList<String> paramNames = convertToArr(sensorConf.getJSONArray("parametersNames"));
                ArrayList<String> paramTypes = convertToArr(sensorConf.getJSONArray("parametersTypes"));
                int state = sensorConf.getInt("initialState");
                ArrayList<Long> samplingRates = convertToArrLong(sensorConf.getJSONArray("samplingRates"));
                // OPTIONAL

                // This one is for simple android sensors
                if (sensorConf.has("androidSensorType") && sensorConf.has("androidParametersPositions")) {
                    int androidSensorType = sensorConf.getInt("androidSensorType");
                    int[] positions = convertToIntArr(sensorConf.getJSONArray("androidParametersPositions"));
                    confClass = new ConfigurationBasicSensor(sensorID, sensorName, androidSensorType,
                            paramNames, paramTypes, positions, samplingRates, state);
                }
                // This one is for other sensors
                else if (sensorConf.has("wrapperName")) {
                    String wrapperName = sensorConf.getString("wrapperName");
                    confClass = new ConfigurationBasicSensor(sensorID, sensorName,
                            paramNames, paramTypes, wrapperName, samplingRates, state);
                }

                ConfigurationMap.addSensorConfig(confClass);

                list.add(confClass);
                Log.d(LOG_TAG, confClass.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return list;
    }


    private static ArrayList<String> convertToArr(JSONArray jList){
        int len = jList.length();
        ArrayList<String> arr = new ArrayList();
        for (int i = 0; i < len; i++){
            try {
                arr.add(jList.getString(i));
            } catch (JSONException e) {
                arr.add( null );
                e.printStackTrace();
            }
        }
        return arr;
    }

    private static ArrayList<Long> convertToArrLong(JSONArray jList){
        int len = jList.length();
        ArrayList<Long> arr = new ArrayList();
        for (int i = 0; i < len; i++){
            try {
                arr.add(jList.getLong(i));
            } catch (JSONException e) {
                arr.add( null );
                e.printStackTrace();
            }
        }
        return arr;
    }

    private static int[] convertToIntArr(JSONArray jList){
        int len = jList.length();
        int[] list = new int[len];
        for (int i = 0; i < len; i++){
            try {
                list[i] = jList.getInt(i);
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return list;
    }

}


