package ch.ethz.coss.nervousnet.hub;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import ch.ethz.coss.nervousnet.hub.ui.adapters.AxonsArrayAdapter;
import ch.ethz.coss.nervousnet.hub.ui.adapters.NervousnetAxon;

public class SensorPermissionsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_permissions);

        ListView listAxons = (ListView) findViewById(R.id.lst_axonsPermissions);
        NervousnetAxon[] axonNames = getAxonList();
        AxonsArrayAdapter adapter = new AxonsArrayAdapter(this, R.layout.axon_list_item, axonNames);
        listAxons.setAdapter(adapter);

    }

    private NervousnetAxon[] getAxonList() {
        //// TODO: 02/08/2016 Get real axon list
        NervousnetAxon data[] = new NervousnetAxon[]
                {
                        new NervousnetAxon("Survive", R.drawable.ic_analytics),
                        new NervousnetAxon("Whatever", R.drawable.ic_apps),
                        new NervousnetAxon("Stuff", R.drawable.ic_analytics),
                };

        return data;
    }
}
