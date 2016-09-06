package clustering;

import java.util.ArrayList;

/**
 * Created by ales on 20/07/16.
 *
 * This is a class for a cluster, which contains the point (Point) of its location and
 * other details. Additionally, it includes array of all points under its supervision -
 * the points, belonging to this cluster/centroid.
 */
public interface iCluster {

    public void setCentroid(double[] centroid);
    public double[] getCentroid();

    public void addPoint(iPoint point);
    public ArrayList<iPoint> getPoints();

    public void clear();
}
