package sensor;

/**
 * Created by ales on 02/09/16.
 */
public class SensorValue implements iSensorValue {

    private int ID;
    private String name;
    private double[] values;

    public SensorValue(int ID, String name, double[] values){
        this.ID = ID;
        this.name = name;
        this.values = values;
    }

    @Override
    public int getID() {
        return 0;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public double[] getValues() {
        return new double[0];
    }
}
