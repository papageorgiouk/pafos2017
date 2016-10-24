/*
 *
 *  * Copyright (C) 2016 Track & Talk Ltd
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.trackandtalk.pafos17.ui.settings;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;

import com.trackandtalk.pafos17.BuildConfig;
import com.trackandtalk.pafos17.R;


/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */
public class AboutActivity extends AppCompatActivity {

    private TextView poweredBy;
    private TextView license;
    private TextView trademarks;
    private TextView version;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        poweredBy = (TextView)findViewById(R.id.powered_by);
        Typeface pf_notepad = Typeface.createFromAsset(getAssets(), "fonts/pf_notepad_bold.otf");
        poweredBy.setTypeface(pf_notepad);

        license = (TextView)findViewById(R.id.license);
        license.setText(Html.fromHtml(getString(R.string.copyright_license)));

        trademarks = (TextView)findViewById(R.id.trademarks);
        String tm = getString(R.string.pafos_trademarks) + "\n\n" + getString(R.string.tnt_trademarks) + "\n\n";
        trademarks.setText(tm);

        version = (TextView)findViewById(R.id.version);
//        version.setText("Version " + BuildConfig.VERSION_NAME);
        version.setText(String.format(getString(R.string.version), BuildConfig.VERSION_NAME));

    }

    public void onLicensesButtonClick(View view) {
        WebView webView = (WebView) LayoutInflater.from(this)
                .inflate(R.layout.dialog_license, null);
        webView.loadUrl("file:///android_asset/open_source_licenses.html");
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Open Source Licenses")
                .setView(webView)
                .setPositiveButton("OK", null)
                .show();
    }

    public void onGithubButtonClick(View v) {
        Uri uri = Uri.parse(getString(R.string.github_repo));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);

    }
}
