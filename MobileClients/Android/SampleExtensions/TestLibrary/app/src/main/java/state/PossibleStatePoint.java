package state;

/**
 * Created by ales on 28/07/16.
 * This class represents one possible state. When sending to the server,
 * list of this classes is sent. We can look at it as a unit.
 */
public class PossibleStatePoint {
    public double[] values;

    public PossibleStatePoint(){}

    public PossibleStatePoint(double[] coordinates){
        this.values = coordinates;
    }

    public void setCopy(double[] coordinates){
        values = new double[coordinates.length];
        int len = coordinates.length;
        for (int i = 0; i < len; i++)
            values[i] = coordinates[i];
    }
}
