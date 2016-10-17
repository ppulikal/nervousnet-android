package ch.ethz.coss.nervousnet.clustering;

/**
 * Created by ales on 27/06/16.
 *
 * Algorithm for clustering, implements class Clustering.
 */
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class KMeans {

    private int numOfClusters;
    private int numOfDimensions;

    public KMeans(int numOfDimensions, int numOfClusters) {
        this.numOfDimensions = numOfDimensions;
        this.numOfClusters = numOfClusters;
    }

    private ArrayList<Cluster> initClusters(ArrayList<? extends iPoint> points){

        ArrayList<Cluster> clusters = new ArrayList<>();
        int sizeOfPoints = points.size();
        Random rand = new Random();
        Log.d("INIT-CLUSTER", "Size " + sizeOfPoints);
        // Choose random centroids
        for (int i = 0; i < this.numOfClusters; i++){
            // Get one of the points as an initial cluster
            Integer newRandomInt = rand.nextInt(sizeOfPoints);
            iPoint point = points.get(newRandomInt);
            double[] coordinates = point.getCoordinates();
            // Just add a little bit of disturbation into the first coordinate
            // so that clusters don't overlap in case of selecting the same point
            // several times
            for( int j = 0; j < coordinates.length; j++ )
                coordinates[j] += coordinates[j] * (1 + rand.nextDouble());
            Cluster c = new Cluster();
            c.setCoordinates(coordinates);
            clusters.add(c);
            Log.d("KMEANS-init-centroids", Arrays.toString(c.getCoordinates()) + "");
        }

        return clusters;
    }


    //The process to calculate the K Means, with iterating method.
    public ArrayList<Cluster> compute(ArrayList<? extends iPoint> points) throws ClusteringException {

        Log.d("KMEANS", "Start computing clusters ...");

        boolean finish = false;

        ArrayList<Cluster> clusters = initClusters(points);

        if (points.size() <= 0) finish = true;

        // Add in new data, one at a time, recalculating centroids with each new one.
        while(!finish) {
            //Assign points to the closer cluster
            assignCluster(points, clusters);
            //Calculate new centroids
            ArrayList<Cluster> newClusters = calculateCentroids(points, clusters);
            //Calculates total distance between new and old Centroids
            double distance = 0;
            int nc = clusters.size();
            for(int i = 0; i < nc; i++)
                distance += distance(clusters.get(i).getCoordinates(),
                        newClusters.get(i).getCoordinates(), numOfDimensions);

            if(distance == 0) {
                finish = true;
            }
            if(Double.isNaN(distance)) {
                throw new ClusteringException("Distance between clusters is NaN");
            }
            Log.d("KMEAN", "Cluster diff ... "+distance);
            clusters = newClusters;
        }

        for(Cluster c : clusters)
            Log.d("KMEANS-final-centroids", Arrays.toString(c.getCoordinates()) + "");

        Log.d("KMEANS", "Computing clusters finished!");
        return clusters;
    }


    private ArrayList<Cluster> calculateCentroids(ArrayList<? extends iPoint> points, ArrayList<Cluster> clusters) {

        // Initialize array of new clusters that will be computed at the end
        ArrayList<Cluster> newClusters = new ArrayList<>();

        // Create hash for calculating number of points for each cluster and corresponding sum
        HashMap<Cluster, Object[]> clusterSumMap = new HashMap<>();
        for (Cluster c : clusters){
            clusterSumMap.put(c, new Object[]{(double)0, new double[this.numOfDimensions]});  // init to 0

            // Also initialize new clusters in this loop
            newClusters.add(new Cluster());
        }

        // Run through all points and for each point update corresponding cluster with
        // number of points that belong to that cluster and sum
        for (iPoint point : points){
            // Get point's cluster
            Cluster c = clusters.get(point.getCluster());
            // Increase number of points
            Object[] hashValue = clusterSumMap.get(c);
            hashValue[0] = (double)hashValue[0] + 1;
            // Update cluster's sum
            double[] coord = (double[]) hashValue[1];
            double[] pointCoord = point.getCoordinates();
            for (int i = 0; i < this.numOfDimensions; i++)
                coord[i] += pointCoord[i];
        }

        // Run over clusters again and divide by the number of points to average coordinates
        // and create new clusters
        for(int i = 0; i < clusters.size(); i++) {
            Cluster cluster = clusters.get(i);
            // Get number of points for the cluster
            Object[] hashValue = clusterSumMap.get(cluster);
            double n = (double) hashValue[0];
            // Get new coordinates
            double[] coord = (double[]) hashValue[1];
            for (int j = 0; j < this.numOfDimensions; j++)
                coord[j] /= n;
            // Update new cluster with fresh coordinates
            newClusters.get(i).setCoordinates(coord);
        }
        return newClusters;
    }


    public int classify(iPoint point, ArrayList<Cluster> clusters) {
        double min = Double.MAX_VALUE;
        int cluster = 0;

        for(int i = 0; i < this.numOfClusters; i++) {
            Cluster c = clusters.get(i);
            double distance = distance( point.getCoordinates(), c.getCoordinates() , numOfDimensions);
            if(distance < min){
                min = distance;
                cluster = i;
            }
        }
        point.setCluster(cluster);
        return cluster;
    }

    public int classify(double[] point, ArrayList<Cluster> clusters) {
        double min = Double.MAX_VALUE;
        int cluster = 0;

        for(int i = 0; i < this.numOfClusters; i++) {
            Cluster c = clusters.get(i);
            double distance = distance(c.getCoordinates(), point, numOfDimensions);
            if(distance < min){
                min = distance;
                cluster = i;
            }
        }
        return cluster;
    }

    private void assignCluster(ArrayList<? extends iPoint> points, ArrayList<Cluster> clusters) {
        for(iPoint point : points) {
            classify(point, clusters);
        }
    }

    public static double distance(double[] arr1, double[] arr2, int dimension){
        double sum = 0;
        for (int i = 0; i < dimension; i++){
            double diff = arr1[i] - arr2[i];
            sum += diff * diff;
        }
        sum = Math.sqrt(sum);
        // Return
        return sum;
    }
}