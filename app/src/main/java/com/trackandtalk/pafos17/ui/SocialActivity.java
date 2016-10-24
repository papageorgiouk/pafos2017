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

package com.trackandtalk.pafos17.ui;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.trackandtalk.pafos17.R;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */
public class SocialActivity extends AppCompatActivity {

    private TextView txtSlogan;
    private TextView txtUrl;
    private TextView txtHashtags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_new);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        txtSlogan = (TextView)findViewById(R.id.follow_our_journey);
        txtUrl = (TextView)findViewById(R.id.url);
        txtHashtags = (TextView)findViewById(R.id.hashtags);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setFonts();
        setHashtags();
    }

    private void setFonts() {
        Typeface pf_notepad = Typeface.createFromAsset(getAssets(), "fonts/pf_notepad_black_reg.otf");
        Typeface geometricSlabserif = Typeface.createFromAsset(getAssets(), "fonts/geometric_slabserif_medium.ttf");

        txtSlogan.setTypeface(pf_notepad);
        txtUrl.setTypeface(geometricSlabserif);
        txtHashtags.setTypeface(geometricSlabserif);
    }

    private void setHashtags() {
        String hashtag1 = getString(R.string.hashtag1);
        String hashtag2 = getString(R.string.hashtag2);
        String hashtag3 = getString(R.string.hashtag3);

        String hashtags = "#" +
                hashtag1 +
                "\t\t#" +
                hashtag2 +
                "\t\t#" +
                hashtag3;

        txtHashtags.setText(hashtags);
    }

    public void onSocialIconClick(View view) {
        switch (view.getId()) {
            case R.id.fb_fab:
                String fbURL = getString(R.string.facebook_page);
                Uri uri = Uri.parse(fbURL);
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
                break;
            case R.id.twitter_fab:
                String twitterURL = getString(R.string.twitter_page);
                uri = Uri.parse(twitterURL);
                i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
                break;
            case R.id.youtube_fab:
                String youtubeURL = getString(R.string.youtube_page);
                uri = Uri.parse(youtubeURL);
                i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
                break;
            case R.id.instagram_fab:
                String instagramURL = "https://www.instagram.com/_u/pafos2017_official/";  //  _u if app is installed
                uri = Uri.parse(instagramURL);
                i = new Intent(Intent.ACTION_VIEW, uri);
                i.setPackage("com.instagram.android");
                try {
                    startActivity(i);
                } catch (ActivityNotFoundException e) {  //  instagram isn't installed, launch website
                    i = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.instagram_page)));
                    startActivity(i);
                }
                break;
            case R.id.url:
                String webUrl = getString(R.string.web_url);
                uri = Uri.parse(webUrl);
                i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
                break;

        }
    }
}
