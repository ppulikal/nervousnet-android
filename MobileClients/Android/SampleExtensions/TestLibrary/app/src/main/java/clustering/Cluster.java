package clustering;

import java.util.ArrayList;

/**
 * Created by ales on 20/07/16.
 *
 * This is a class for a cluster, which contains the point (Point) of its location and
 * other details. Additionally, it includes array of all points under its supervision -
 * the points, belonging to this cluster/centroid.
 */
public class Cluster implements iCluster {

    private double[] centroid;
    private ArrayList<iPoint> points = new ArrayList<iPoint>();

    public Cluster(){}

    public double[] getCentroid(){
        return centroid;
    }

    public ArrayList<iPoint> getPoints(){
        return points;
    }

    public void setCentroid(double[] centroid){
        this.centroid = centroid;
    }

    public void clear() {
        this.points.clear();
    }

    public void addPoint(iPoint point){
        this.points.add(point);
    }

}
