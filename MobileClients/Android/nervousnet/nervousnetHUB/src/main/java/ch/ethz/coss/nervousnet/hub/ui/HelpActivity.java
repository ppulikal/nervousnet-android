/*******************************************************************************
 *
 *  *     Nervousnet - a distributed middleware software for social sensing. 
 *  *      It is responsible for collecting and managing data in a fully de-centralised fashion
 *  *
 *  *     Copyright (C) 2016 ETH Zürich, COSS
 *  *
 *  *     This file is part of Nervousnet Framework
 *  *
 *  *     Nervousnet is free software: you can redistribute it and/or modify
 *  *     it under the terms of the GNU General Public License as published by
 *  *     the Free Software Foundation, either version 3 of the License, or
 *  *     (at your option) any later version.
 *  *
 *  *     Nervousnet is distributed in the hope that it will be useful,
 *  *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  *     GNU General Public License for more details.
 *  *
 *  *     You should have received a copy of the GNU General Public License
 *  *     along with NervousNet. If not, see <http://www.gnu.org/licenses/>.
 *  *
 *  *
 *  * 	Contributors:
 *  * 	Prasad Pulikal - prasad.pulikal@gess.ethz.ch  -  Initial API and implementation
 *******************************************************************************/

package ch.ethz.coss.nervousnet.hub.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.webkit.WebView;
import android.widget.ExpandableListView;
import android.widget.TabHost;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.ethz.coss.nervousnet.hub.R;
import ch.ethz.coss.nervousnet.hub.ui.views.FaqExpandableListAdapter;

/**
 * @author prasad
 *
 */
public class HelpActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		TabHost host = (TabHost)findViewById(R.id.tabHostHelp);
		host.setup();

		//Tab 1
		TabHost.TabSpec spec = host.newTabSpec("Getting Started");
		spec.setContent(R.id.tab_gettingStarted);
		spec.setIndicator("Getting Started");
		host.addTab(spec);

		//Tab 2
		spec = host.newTabSpec("FAQ");
		spec.setContent(R.id.tab_FAQ);
		spec.setIndicator("FAQ");
		host.addTab(spec);

        //WebView
        WebView webview = (WebView) findViewById(R.id.gettingStarted_webView);
        webview.loadUrl("file:///android_asset/getting_started.html");

        //FAQ
        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.lstView_faq);
        final HashMap<String,List<String>> expandableListDetail = getStringListHashMap();
        final ArrayList<String> expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());

        FaqExpandableListAdapter expandableListAdapter = new FaqExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

	}

    @NonNull
    private HashMap<String, List<String>> getStringListHashMap() {
        HashMap<String, List<String>> res = new HashMap<String, List<String>>();

        res.put("Is my data safe?", createArrayListWithOneItem("Sure. It is. Yay."));
        res.put("How many times should I look at this app?", createArrayListWithOneItem("Depending on the Axon you do not have to look at it at all"));
        res.put("How can I contribute?", createArrayListWithOneItem("Development guides can be found on the webpage"));
        res.put("Are we alone?", createArrayListWithOneItem("Most probably not"));
        return res;
    }

    private ArrayList<String> createArrayListWithOneItem(String item) {
        ArrayList<String> res = new ArrayList<String>();
        res.add(item);
        return res;
    }

}
