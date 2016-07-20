package ch.ethz.coss.nervousnet.extensions.lightmeter;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import ch.ethz.coss.nervousnet.lib.NervousnetRemote;
import ch.ethz.coss.nervousnet.lib.Utils;

/**
 * Created by prasad on 19/07/16.
 */
public class NervousnetServiceUtil {


    /**********
     * Step 1 for nervousnet HUB API's
     **********/
    protected NervousnetRemote mService;
    private ServiceConnection mServiceConnection;
    private Boolean bindFlag;
    /***********
     * END OF STEP 1
     **************/


    NervousnetServiceConnectionListener listener;
    Context context;

    public NervousnetServiceUtil(Context context, NervousnetServiceConnectionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void init() {
/***********STEP 2 for nervousnet HUB API's************/
        if (mServiceConnection == null) {
            initConnection();
        }

        if (mService == null) {
            try {
                doBindService();
                Log.d("NervousnetServiceUtil", bindFlag.toString());
                if (!bindFlag) {
                    Utils.displayAlert(context, "Alert",
                            "Nervousnet HUB application is required to be installed and running to use this app. If not installed please download it from the App Store. If already installed, please turn on the Data Collection option inside the Nervousnet HUB application.",
                            "Download Now", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    try {
                                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=ch.ethz.coss.nervousnet.hub")));
                                    } catch (android.content.ActivityNotFoundException anfe) {
                                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=ch.ethz.coss.nervousnet.hub")));
                                    }

                                }
                            }, "Exit", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    System.exit(0);
                                }
                            });
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("NervousnetServiceUtil", "not able to bind ! ");
            }


        }
        /**********END OF STEP 2**************/
    }

    /*********
     * STEP3 for nervousnet HUB API's
     ********/
    void initConnection() {
        Log.d("NervousnetServiceUtil", "Inside initConnection");
        mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("NervousnetServiceUtil", "Inside onServiceDisconnected 2");
                ;
                mService = null;
                mServiceConnection = null;
                Toast.makeText(context, "NervousnetRemote Service not connected", Toast.LENGTH_SHORT)
                        .show();
                Log.d("NervousnetServiceUtil", "Binding - Service disconnected");
                listener.ServiceDisconnected();
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("NervousnetServiceUtil", "onServiceConnected");

                mService = NervousnetRemote.Stub.asInterface(service);


                Toast.makeText(context, "Nervousnet Remote Service Connected", Toast.LENGTH_SHORT)
                        .show();
                Log.d("NervousnetServiceUtil", "Binding is done - Service connected");
                listener.ServiceConnected(mService);
            }
        };

    }


    protected void doBindService() {
        Log.d("NervousnetServiceUtil", "doBindService successfull");
        Intent it = new Intent();
        it.setClassName("ch.ethz.coss.nervousnet.hub", "ch.ethz.coss.nervousnet.hub.NervousnetHubApiService");
        bindFlag = context.bindService(it, mServiceConnection, 0);

    }

    protected void doUnbindService() {
        context.unbindService(mServiceConnection);
        bindFlag = false;
        Log.d("NervousnetServiceUtil ", "doUnbindService successfull");
    }
    /*********END OF STEP3********/


}