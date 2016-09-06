package clustering;

import java.util.ArrayList;

/**
 * Created by ales on 28/06/16.
 *
 * Interface specifies the exact structure of any clustering algorithm.
 */
public interface iClustering {
    public ArrayList<iCluster> compute(ArrayList<? extends iPoint> points);
    public iCluster classify(iPoint point);
    public iCluster classify(double[] point);
    public ArrayList<iCluster> getClusters();
}
