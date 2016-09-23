package ethz.ch.client;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;

import data.Nervousnet;

public class Client extends Activity {

    TextView sendResponse, textNervousnet;
    Button buttonConnect, buttonNervousnet;
    OnClickListener buttonNervousnetOnClickListener;
    Nervousnet nervousnet;

    boolean isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Don't allow rotation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);

        // Create front-end view
        setContentView(R.layout.activity_client);

        // 1. Initialize buttons
        buttonNervousnet = (Button)findViewById(R.id.nervousnet);
        initButtonNervousnet(this);
        buttonNervousnet.setOnClickListener(buttonNervousnetOnClickListener);

        // 2. Initialize text
        sendResponse = (TextView)findViewById(R.id.sendResponse);
        textNervousnet = (TextView)findViewById(R.id.textNervousnet);

        // 4. Initialize data source
        nervousnet = new Nervousnet(this);
        nervousnet.connect();

    }



    protected void initButtonNervousnet(final Context context) {
        // Create button connector
        this.buttonNervousnetOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            nervousnet.testReadings();
                            Log.d("TEST", "testReadings done");
                            //nervousnet.testAverage();
                            //Log.d("TEST", "testAverage done");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                };

                thread.start();
            }
        };
    }
}