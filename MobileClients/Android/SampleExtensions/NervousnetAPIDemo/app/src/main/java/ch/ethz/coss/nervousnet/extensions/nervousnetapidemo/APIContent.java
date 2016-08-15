package ch.ethz.coss.nervousnet.extensions.nervousnetapidemo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class APIContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<ApiItem> ITEMS = new ArrayList<ApiItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, ApiItem> ITEM_MAP = new HashMap<String, ApiItem>();

    private static final int COUNT = 3;

    static {
        // Add some sample items.
        for (int i = 1; i <= COUNT; i++) {
            addItem(createApiItem(i));
        }
    }

    private static void addItem(ApiItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static ApiItem createApiItem(int position) {

        switch(position){
            case 1:
                return new ApiItem(String.valueOf(position), "getLatestReading()", makeDetails(position));
            case 2:
                return new ApiItem(String.valueOf(position), "getReading() with Callback", makeDetails(position));
            case 3:
                return new ApiItem(String.valueOf(position), "getReadings() with Callback", makeDetails(position));
            default:
                return null;
        }

    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("API Details: ").append(position);
        switch(position) {

            case 1:
                return builder.append("\n SensorReading getLatestReading(long sensorType);").
                        append("\n Returns latest Sensor values.\n" +
                                " sensorType = type of Sensor. Check LibConstants.java for types. \n" +
                                " startTime = from time , endTime = to time\n" +
                                " returns SensorReading object").toString();

            case 2:
                return builder.append("\n void getReading(long sensorType, RemoteCallback cb);").
                        append("\n Returns Sensor values in a List of SensorReading Objects using callback \n" +
                                "sensorType = type of Sensors. Check LibConstants for types.\n" +
                                "cb = Callback object with list that will contain a single returned object of SensorReading").toString();

            case 3:
                return builder.append("\n void getReadings(long sensorType, long startTime, long endTime,  RemoteCallback cb);").
                        append("\n Returns Sensor values in a List of SensorReading Objects using callback \n" +
                                "sensorType = type of Sensors. Check LibConstants for types.\n" +
                                "startTime = from time\n" +
                                "endTime = to time\n" +
                                "cb = Callback object with list that will contain the returned objects of SensorReadings").toString();
        }
        return "";
    }


    public static class ApiItem {
        public final String id;
        public final String content;
        public final String details;

        public ApiItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
