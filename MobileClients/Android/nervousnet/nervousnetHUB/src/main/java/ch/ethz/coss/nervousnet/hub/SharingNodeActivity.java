package ch.ethz.coss.nervousnet.hub;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import ch.ethz.coss.nervousnet.hub.ui.adapters.NervousnetNode;
import ch.ethz.coss.nervousnet.hub.ui.adapters.NodesArrayAdapter;

public class SharingNodeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharing_node);

        ListView list = (ListView) findViewById(R.id.lst_Nodes);

        NervousnetNode[] nodesNames = getNodeList();
        NodesArrayAdapter adapter = new NodesArrayAdapter(this,R.layout.node_list_item,nodesNames);
        list.setAdapter(adapter);

    }

    private NervousnetNode[] getNodeList() {
        //// TODO: 02/08/2016 get real node names
        NervousnetNode[] data = new NervousnetNode[]{
                new NervousnetNode("ETH Main Node"),
                new NervousnetNode("Alice's Node"),
                new NervousnetNode("Switzerland"),
                new NervousnetNode("nervousnet internal"),
        };

        return data;
    }
}
