package ch.ethz.coss.nervousnet.vm.utils;

/**
 * Created by ales on 14/09/16.
 */
public class StableAverageComputation {

    /*
    This function enables iterative computation of average. At every step, there avg is
    computed.

    oldAvg: an average of all previous numbers a_1, a_2, ... a_{n-1}
    a_n:    new number to be added
    n:      number of all considered numbers

    return value: average for a_1, a_2, ... , a_n
     */
    public static double computeNext(double oldAvg, double a_n, int n){
        return oldAvg + (a_n - oldAvg) / n;
    }

}
