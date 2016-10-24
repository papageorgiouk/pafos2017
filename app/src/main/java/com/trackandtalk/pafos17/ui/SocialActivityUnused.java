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
 * <p>A SocialActivityUnused that ended up unused. Left here just in case</p>
 *
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */
public class SocialActivityUnused extends AppCompatActivity {

    private TextView followOurJourney;
    private TextView hashtag1;
    private TextView hashtag2;
    private TextView hashtag3;

    private static final String TWITTER_HASHTAG = "https://twitter.com/hashtag/";
    private static final String FB_HASHTAG = "https://www.facebook.com/hashtag/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        followOurJourney = (TextView)findViewById(R.id.follow_our_journey);
        hashtag1 = (TextView)findViewById(R.id.hashtag_1);
        hashtag2 = (TextView)findViewById(R.id.hashtag_2);
        hashtag3 = (TextView)findViewById(R.id.hashtag_3);

        setFonts();
    }

    private void setFonts() {
        Typeface pf_notepad = Typeface.createFromAsset(getAssets(), "fonts/pf_notepad_reg.otf");
        Typeface geometricSlabserif = Typeface.createFromAsset(getAssets(), "fonts/geometric_slabserif_medium.ttf");

        followOurJourney.setTypeface(pf_notepad);
        hashtag1.setTypeface(geometricSlabserif);
        hashtag2.setTypeface(geometricSlabserif);
        hashtag3.setTypeface(geometricSlabserif);
    }

    public void onClick(View view) {

        switch (view.getId()) {

            //  social media profile pages
            case R.id.social_youtube:
                String youtubeURL = getString(R.string.youtube_page);
                Uri uri = Uri.parse(youtubeURL);
                Intent i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
                break;
            case R.id.social_instagram:
                String instagramURL = getString(R.string.instagram_page);
                uri = Uri.parse(instagramURL);
                i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
                break;
            case R.id.social_twitter:
                String twitterURL = getString(R.string.twitter_page);
                uri = Uri.parse(twitterURL);
                i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
                break;
            case R.id.social_facebook:
                String fbURL = getString(R.string.facebook_page);
                uri = Uri.parse(fbURL);
                i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
                break;

            //  hashtag 1
            case R.id.hash1_twitter:
                String hashURL = getString(R.string.hashtag1);
                uri = Uri.parse(TWITTER_HASHTAG + hashURL);
                i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
                break;
            case R.id.hash1_fb:
                hashURL = getString(R.string.hashtag1);
                uri = Uri.parse(FB_HASHTAG + hashURL);
                i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
                break;

            //  hashtag 2
            case R.id.hash2_twitter:
                hashURL = getString(R.string.hashtag2);
                uri = Uri.parse(TWITTER_HASHTAG + hashURL);
                i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
                break;
            case R.id.hash2_fb:
                hashURL = getString(R.string.hashtag2);
                uri = Uri.parse(FB_HASHTAG + hashURL);
                i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
                break;

            // hashtag3
            case R.id.hash3_twitter:
                hashURL = getString(R.string.hashtag3);
                uri = Uri.parse(TWITTER_HASHTAG + hashURL);
                i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
                break;
            case R.id.hash3_fb:
                hashURL = getString(R.string.hashtag3);
                uri = Uri.parse(FB_HASHTAG + hashURL);
                i = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(i);
                break;
        }
    }
}
