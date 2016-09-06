package sensor;

import java.util.ArrayList;

/**
 * Created by ales on 02/09/16.
 */
public class ActiveSensors {
    private static ArrayList<Integer> activeSensors = new ArrayList<>();

    public static void add(int ID){
        if (!activeSensors.contains(ID))
            activeSensors.add(ID);
    }

    public static void remove(int ID){
        if (activeSensors.contains(ID))
            activeSensors.remove(ID);
    }

    public static ArrayList<Integer> getAll(){
        return activeSensors;
    }

    public static void clear(){
        activeSensors.clear();
    }
}
