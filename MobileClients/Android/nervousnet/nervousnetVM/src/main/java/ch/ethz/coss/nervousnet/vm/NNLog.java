package ch.ethz.coss.nervousnet.vm;

import android.util.Log;

public class NNLog {

    public static boolean debug_flag = true;

    public static void d(String tag, String message) {
        if (debug_flag)
            Log.d(tag, message);
    }

}
