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
import android.webkit.WebView;
import android.widget.TabHost;

import ch.ethz.coss.nervousnet.hub.R;

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
	}

}
