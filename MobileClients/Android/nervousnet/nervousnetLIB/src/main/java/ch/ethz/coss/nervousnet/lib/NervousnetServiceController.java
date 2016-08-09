package ch.ethz.coss.nervousnet.lib;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

/*******************************************************************************
 * *     Nervousnet - a distributed middleware software for social sensing.
 * *      It is responsible for collecting and managing data in a fully de-centralised fashion
 * *
 * *     Copyright (C) 2016 ETH Zürich, COSS
 * *
 * *     This file is part of Nervousnet Framework
 * *
 * *     Nervousnet is free software: you can redistribute it and/or modify
 * *     it under the terms of the GNU General Public License as published by
 * *     the Free Software Foundation, either version 3 of the License, or
 * *     (at your option) any later version.
 * *
 * *     Nervousnet is distributed in the hope that it will be useful,
 * *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 * *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * *     GNU General Public License for more details.
 * *
 * *     You should have received a copy of the GNU General Public License
 * *     along with NervousNet. If not, see http://www.gnu.org/licenses/.
 * *
 * *
 * * 	Contributors:
 * * 	Prasad Pulikal - prasad.pulikal@gess.ethz.ch  -  Initial API and implementation
 *******************************************************************************/
public class NervousnetServiceController {

    private static String LOG_TAG = NervousnetServiceController.class.getSimpleName();
    public NervousnetRemote mService;
    NervousnetServiceConnectionListener listener;
    Context context;
    private ServiceConnection mServiceConnection;
    private Boolean bindFlag;

    public NervousnetServiceController(Context context, NervousnetServiceConnectionListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void connect() {
        if (mServiceConnection == null) {
            initConnection();
        }

        if (mService == null) {
            try {
                doBindService();
                Log.d(LOG_TAG, bindFlag.toString());
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
            } catch (SecurityException e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "SecurityException - cannot bind to nervousnet service due to missing permission or permission denied. use 'ch.ethz.coss.nervousnet.hub.BIND_PERM' in your manifest to connect to nervousnet HUB Service");
                doUnbindService();

                listener.onServiceConnectionFailed(new ErrorReading(new String[]{"303","Security Exception - Cannot bind to nervousnet HUB service. Missing or denied Permission."}));
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(LOG_TAG, "Exception - not able to bind ! ");
                doUnbindService();

                listener.onServiceConnectionFailed(new ErrorReading(new String[]{"301","Unable to bind to nervousnet HUB service"}));
            }


        }

    }


    public void disconnect() {
        doUnbindService();
    }


    private void initConnection() {
        Log.d(LOG_TAG, "Inside initConnection");
        mServiceConnection = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(LOG_TAG, "Inside onServiceDisconnected 2");

                mService = null;
                mServiceConnection = null;
                Toast.makeText(context, "NervousnetRemote Service not connected", Toast.LENGTH_SHORT)
                        .show();
                Log.d("NervousnetServiceUtil", "Binding - Service disconnected");
                listener.onServiceDisconnected();
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d("NervousnetServiceUtil", "onServiceConnected");

                mService = NervousnetRemote.Stub.asInterface(service);

                Toast.makeText(context, "Nervousnet Remote Service Connected", Toast.LENGTH_SHORT)
                        .show();
                Log.d(LOG_TAG, "Binding is done - Service connected");
                if(listener != null)
                listener.onServiceConnected();
            }
        };

    }


    private void doBindService() {
        Log.d(LOG_TAG, "doBindService successfull");
        Intent it = new Intent();
        it.setClassName("ch.ethz.coss.nervousnet.hub", "ch.ethz.coss.nervousnet.hub.NervousnetHubApiService");
        bindFlag = context.bindService(it, mServiceConnection, 0);

        if(!bindFlag && listener != null) {
            listener.onServiceConnectionFailed(new ErrorReading(new String[]{"302","Cannot bind to nervousnet HUB service."}));
        }

    }

    private void doUnbindService() {
        if(mServiceConnection != null)
         context.unbindService(mServiceConnection);

        bindFlag = false;
        mService = null;
        mServiceConnection = null;
        Log.d(LOG_TAG, "doUnbindService successfull");
    }


    public SensorReading getLatestReading(long sensorID) throws RemoteException {
        if (bindFlag) {
            if (mService != null)
                return mService.getLatestReading(sensorID);
            else
                return new ErrorReading(new String[]{"002", "Service not connected."});
        } else
            return new ErrorReading(new String[]{"003", "Service not bound."});



    }


    public SensorReading getReadings(long sensorID, long startTime, long endTime, RemoteCallback cb) throws RemoteException {
        if (bindFlag) {
            if (mService != null){

                mService.getReadings(sensorID,startTime, endTime, cb);
                return null;
            } else
                return new ErrorReading(new String[]{"002", "Service not connected."});
        } else
            return new ErrorReading(new String[]{"003", "Service not bound."});



    }




//    /**
//     * gets latest reading using a listener.
//     * @param sensorID
//     * @param nnSensorDataListener
//     * @throws RemoteException
//     */
//    public void getReading(long sensorID, NervousnetSensorDataListener nnSensorDataListener) throws RemoteException {
//        if (bindFlag) {
//            if (mService != null)
//                return mService.getLatestReading(sensorID);
//            else
//                nnSensorDataListener.onSensorDataReady(new ErrorReading(new String[]{"002", "Service not connected."}));
//        } else
//            nnSensorDataListener.onSensorDataReady(new ErrorReading(new String[]{"003", "Service not bound."}));
//
//
//    }

}
