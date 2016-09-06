package virtual;

/**
 * Created by ales on 30/08/16.
 */
public abstract class AVirtualSensorPoint {

    /*
    The coordinates represents [Noise, Light, Battery, accX, accY, accZ, gyroX, gyroY, gyroZ, Proxim, free, free]
    The fields are initialized to -INF. If there is field with value -INF, then the data hasn't been
    inserted. The field positions of specific sensord are determined below.
     */
    final int N = 0;
    final int L = 1;
    final int B = 2;
    final int ACCX = 3;
    final int ACCY = 4;
    final int ACCZ = 5;
    final int GYROX = 6;
    final int GYROY = 7;
    final int GYROZ = 8;
    final int PROX = 9;

    private long timestamp;

    private static final int DIMENSIONS = 10;
    private double[] values = new double[DIMENSIONS];

    // SETTER

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setNoise(double val){
        this.values[N] = val;
    }

    public void setLight(double val){
        this.values[L] = val;
    }

    public void setBattery(double val){
        this.values[B] = val;
    }

    public void setAccelerometer(double x, double y, double z){
        this.values[ACCX] = x;
        this.values[ACCY] = y;
        this.values[ACCZ] = z;
    }

    public void setGyrometer(double x, double y, double z){
        this.values[GYROX] = x;
        this.values[GYROY] = y;
        this.values[GYROZ] = z;
    }

    public void setProximity(double val){
        this.values[PROX] = val;
    }

    public void setValues(double[] values){
        this.values = values;
    }

    // GETTER
    public long getTimestamp()  {
        return timestamp;
    }

    public double getNoise(){
        return values[N];
    }

    public double getLight(){
        return values[L];
    }

    public double getBattery() {
        return values[B];
    }

    public double[] getAccelerometer() {
        return new double[] {
                values[ACCX],
                values[ACCY],
                values[ACCZ]
        };
    }

    public double[] getGryometer() {
        return new double[] {
                values[GYROX],
                values[GYROY],
                values[GYROZ]
        };
    }

    public double getProximity() {
        return values[PROX];
    }

    public double[] getValues(){
        return this.values;
    }
}
