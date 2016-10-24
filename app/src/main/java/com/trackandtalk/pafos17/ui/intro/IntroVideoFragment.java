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

package com.trackandtalk.pafos17.ui.intro;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.trackandtalk.pafos17.BuildConfig;
import com.trackandtalk.pafos17.R;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */
public class IntroVideoFragment extends Fragment {

    private static final String ARG_BG_COLOR = "bg_color";
    private int bgColor;

    private LinearLayout rootLayout;
    private YouTubePlayerSupportFragment youTubePlayerFragment;

    public static IntroVideoFragment newInstance(@ColorInt int bgColor) {
        IntroVideoFragment sampleSlide = new IntroVideoFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_BG_COLOR, bgColor);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    public IntroVideoFragment() {
        //  required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bgColor = getArguments().getInt(ARG_BG_COLOR);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_intro_video, container, false);
        rootLayout = (LinearLayout)view.findViewById(R.id.intro_video_background);
        setBackgroundColor(bgColor);

        initYouTube();

        return view;
    }

    private void initYouTube() {
        if (youTubePlayerFragment == null) {
            youTubePlayerFragment = (YouTubePlayerSupportFragment) getChildFragmentManager().findFragmentById(R.id.youtube_fragment);
            youTubePlayerFragment.initialize(BuildConfig.YOUTUBE_KEY, new YouTubePlayer.OnInitializedListener() {
                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                    youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
                    youTubePlayer.cueVideo("2B1g1K5xVsU");
                    youTubePlayer.play();
                }

                @Override
                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                    Snackbar.make(rootLayout, "Failed to load Youtube video", Snackbar.LENGTH_SHORT).show();

                }
            });
        }
    }

    private void setBackgroundColor(@ColorInt int color) {
        rootLayout.setBackgroundColor(color);
    }
}
