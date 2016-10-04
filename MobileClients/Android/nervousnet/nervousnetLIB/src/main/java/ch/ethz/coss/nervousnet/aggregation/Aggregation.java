package ch.ethz.coss.nervousnet.aggregation;

import java.util.ArrayList;

import ch.ethz.coss.nervousnet.lib.SensorReading;

/**
 * Created by ales on 04/10/16.
 */
public class Aggregation extends GeneralAggrFunction<SensorReading> {
    public Aggregation(ArrayList<SensorReading> list) {
        super(list);
    }
}
