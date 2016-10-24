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

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.VideoView;

import com.trackandtalk.pafos17.R;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */
public class IntroAddEventFragment extends Fragment {

    private VideoView videoView;
    private LinearLayout rootLayout;

    private static final String ARG_BG_COLOR = "bg_colour";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_intro_add, container, false);

        videoView = (VideoView)view.findViewById(R.id.video_view);
        rootLayout = (LinearLayout)view.findViewById(R.id.rootLayout);
        setBackgroundColour(getArguments().getInt(ARG_BG_COLOR));

        String videoPath = "android.resource://" + getActivity().getPackageName() + "/" + R.raw.add_event;
        Uri videoUri = Uri.parse(videoPath);



        videoView.setVideoURI(videoUri);
//        videoView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                videoView.start();
//            }
//        });

//        //MediaController
//        MediaController mediaController = new MediaController(getActivity());
//        mediaController.setVisibility(View.GONE);
//        mediaController.setAnchorView(videoView);
//
//        // Init Video
//        videoView.setMediaController(mediaController);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
                mp.start();
            }
        });

        return view;
    }

    private void setBackgroundColour(@ColorInt int bgColour) {
        rootLayout.setBackgroundColor(bgColour);
    }

    public static IntroAddEventFragment newInstance(@ColorInt int bgColour) {
        IntroAddEventFragment fragment = new IntroAddEventFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_BG_COLOR, bgColour);
        fragment.setArguments(args);

        return fragment;
    }
}
