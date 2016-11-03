package ch.ethz.coss.nervousnet.vm.nervousnet.configuration;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

import ch.ethz.coss.nervousnet.vm.nervousnet.configuration.ConfigurationMap;

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
        ArrayList<ConfigurationBasicSensor> list = new ArrayList<>();

        try {
            JSONArray sensorConfList = (new JSONObject(strJson)).getJSONArray("sensors_configurations");

            for (int i = 0; i < sensorConfList.length(); i++) {
                JSONObject sensorConf = sensorConfList.getJSONObject(i);

                String sensorName = sensorConf.getString("sensorName");
                int androidSensorType = sensorConf.getInt("androidSensorType");
                ArrayList<String> paramNames = convertToArr(sensorConf.getJSONArray("parametersNames"));
                ArrayList<String> paramTypes = convertToArr(sensorConf.getJSONArray("parametersTypes"));
                int[] positions = convertToIntArr(sensorConf.getJSONArray("androidParametersPositions"));
                int samplingPeriod = sensorConf.getInt("samplingPeriod");

                ConfigurationBasicSensor confClass = new ConfigurationBasicSensor(sensorName, androidSensorType,
                        paramNames, paramTypes, positions, samplingPeriod);

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
        ArrayList<String> arr = new ArrayList<>();
        for (int i = 0; i < len; i++){
            try {
                arr.add(jList.getString(i));
            } catch (JSONException e) {
                arr.add( "null" );
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


