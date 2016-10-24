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

package com.trackandtalk.pafos17.slideshow;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.trackandtalk.pafos17.CulturalCapitalApp;
import com.trackandtalk.pafos17.R;
import com.trackandtalk.pafos17.data.model.CulturalEvent;
import com.trackandtalk.pafos17.network.EndpointProvider;

import javax.inject.Inject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnSlidingEventClickedListener} interface
 * to handle interaction events.
 * Use the {@link ImageSlideFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 *
 */
public class ImageSlideFragment extends Fragment {
    private static final String EVENT = "event";
    private CulturalEvent event;
    private String imageUrl;
    private String labelText;
    android.view.animation.Interpolator interpolator;

    @Inject EndpointProvider endpointProvider;
    private OnSlidingEventClickedListener mListener;

    private TextView label;

    public ImageSlideFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ImageSlideFragment.
     */
    public static ImageSlideFragment newInstance(CulturalEvent event) {
        ImageSlideFragment fragment = new ImageSlideFragment();
        Bundle args = new Bundle();
        args.putParcelable(EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CulturalCapitalApp)getActivity().getApplication()).getComponent().inject(this);

        if (getArguments() != null) {
            this.event = getArguments().getParcelable(EVENT);
            labelText = event.getEventTitle();
            imageUrl = endpointProvider.getFullImagesUrl()
                    + event.getImagePath();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_image_slide, container, false);
        ImageView imageView = (ImageView) rootView.findViewById(R.id.image);
        label = (TextView)rootView.findViewById(R.id.label);

        Uri uri = Uri.parse(imageUrl);
        Glide.with(this)
                .fromUri()
                .load(uri)
                .dontTransform()
                .into(imageView);

        label.setText(labelText);

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onSlidingEventClicked(ImageSlideFragment.this, event);
                }
            }
        });

        label.setAlpha(0.1f);
        label.setTranslationX(64);

        interpolator = new DecelerateInterpolator();

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    /**
     * <p>Play the animation. </p>
     * <p>Called when the fragment becomes visible, usually from the PagerAdapter</p>
     *
     */
    public void playAnimation() {

        if (isResumed()) {
            label.animate()
                    .translationX(0)
                    .alpha(1)
                    .setInterpolator(interpolator)
                    .setDuration(600)
                    .start();
        }
    }

    public void setOnSlidingEventListener (OnSlidingEventClickedListener listener) {
        this.mListener = listener;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * When a sliding image is clicked
     */
    public interface OnSlidingEventClickedListener {
        void onSlidingEventClicked(ImageSlideFragment fragment, CulturalEvent event);
    }
}
