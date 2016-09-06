package periodic;

import android.os.RemoteException;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

import clustering.Point;
import clustering.iCluster;
import clustering.iClustering;
import clustering.iPoint;
import database.iDatabase;
import virtual.ClusterVirtualSensorPoint;
import virtual.DataSourceHelper;
import data.iDataSource;
import virtual.OriginalVirtualSensorPoint;
import virtual.VirtualPoint;
import state.PossibleStatePoint;

/**
 * Created by ales on 26/07/16.
 */
public class PeriodicExecution extends Thread {

    iDatabase database;
    ArrayList<iPoint> points;
    iClustering clustering;
    iDataSource dataSource;

    state.State state;

    public boolean isRunning = false;

    private static long lastClusteringTimestamp;    // epoch in milliseconds
    private static final long clusteringIntervalInMillisec = 5000;  // in milliseconds

    private static int sleepTime = 100;

    public static int id = 0;

    public PeriodicExecution(state.State state, iClustering clustering, iDataSource dataSource,
                             iDatabase database){
        this.state = state;             // we'll be updating state.possibleStates periodically
        this.clustering = clustering;
        this.dataSource = dataSource;
        this.database = database;
        isRunning = false;
    }

    @Override
    public void run() {

        Log.d("PERIODICITY", "Start periodic execution ...");
        this.isRunning = true;

        points = new ArrayList<iPoint>();

        // 1. Get virtual sensor data

        try {
            ArrayList<OriginalVirtualSensorPoint> original = DataSourceHelper.getInitData( dataSource );

        } catch (RemoteException e) {
            e.printStackTrace();
            // TODO: Instead of terminting here, we can probably still proceed
            return;
        }
        Log.d("PERIODIC-INIT-SIZE", "" + points.size());


    }

    public void stopExecution(){
        Log.d("PERIODICITY", "Accepted stop instruction ...");
        this.isRunning = false;
    }

    public iClustering getClustering(){
        return this.clustering;
    }


    public void addVPtoDB(VirtualPoint vp){
        ClusterVirtualSensorPoint cluster = vp.getCluster();
        OriginalVirtualSensorPoint original = vp.getOriginal();
        database.add(original.getTimestamp(),
                cluster.getNoise(),
                cluster.getLight(),
                cluster.getBattery(),
                cluster.getAccelerometer()[0],
                cluster.getAccelerometer()[1],
                cluster.getAccelerometer()[2],
                cluster.getGryometer()[0],
                cluster.getGryometer()[1],
                cluster.getGryometer()[2],
                cluster.getProximity(),
                original.getNoise(),
                original.getLight(),
                original.getBattery(),
                original.getAccelerometer()[0],
                original.getAccelerometer()[1],
                original.getAccelerometer()[2],
                original.getGryometer()[0],
                original.getGryometer()[1],
                original.getGryometer()[2],
                original.getProximity()
        );
    }
}
