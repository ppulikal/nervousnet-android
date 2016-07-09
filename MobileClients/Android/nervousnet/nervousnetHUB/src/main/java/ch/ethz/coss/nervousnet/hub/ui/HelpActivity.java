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

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.webkit.WebView;
import android.widget.ExpandableListView;
import android.widget.TabHost;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        final HashMap<String,List<String>> expandableListDetail = getFAQList();
        final ArrayList<String> expandableListTitle = new ArrayList<String>(expandableListDetail.keySet());

        FaqExpandableListAdapter expandableListAdapter = new FaqExpandableListAdapter(this, expandableListTitle, expandableListDetail);
        expandableListView.setAdapter(expandableListAdapter);

	}

    @NonNull
    private HashMap<String, List<String>> getFAQList() {


        InputStream is = getResources().openRawResource(R.raw.faq);
        Scanner s = new Scanner(is).useDelimiter("\\A");
        String line = s.hasNext() ? s.next() : "";


//        String line = "your k<q>kk insevure an</q>d with aaa but not kkk is <q>it silver aaa</q> confess <a> screw ks </a> ano <a> cros </a>";
        String patternQ = "<q>(.+?)</q>";
        String patternA = "<a>(.+?)</a>";

        // Create a Pattern object
        Pattern pQ = Pattern.compile(patternQ);
        Pattern pA = Pattern.compile(patternA);

        // Now create matcher object.
        Matcher mQ = pQ.matcher(line);
        ArrayList<String> listQ = new ArrayList<String>();
        while(mQ.find( )) {
            listQ.add(mQ.group(1));
        }

        Matcher mA = pA.matcher(line);
        ArrayList<String> listA = new ArrayList<String>();
        while(mA.find( )) {
            listA.add(mA.group(1));
        }

        HashMap<String, List<String>> res = new HashMap<String, List<String>>();
        if (listA.size() != listQ.size()){
            res.put("Unequal Q and A sizes", createArrayListWithOneItem(" "));
            return res;
        }

        for (int i = 0; i < listA.size(); ++i) {
            res.put(listQ.get(i), createArrayListWithOneItem(listA.get(i)));
        }
        return res;
    }

    private ArrayList<String> createArrayListWithOneItem(String item) {
        ArrayList<String> res = new ArrayList<String>();
        res.add(item);
        return res;
    }

}
