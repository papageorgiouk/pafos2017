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


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.trackandtalk.pafos17.CulturalCapitalApp;
import com.trackandtalk.pafos17.R;
import com.trackandtalk.pafos17.network.EndpointProvider;

import javax.inject.Inject;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */
public class LandmarkImageFragment extends Fragment {

    private static final String STRING_URL = "url";

    private String imageUrl;
    private PhotoViewAttacher mAttacher;
    private OnFragmentClickListener mListener;
    @Inject EndpointProvider endpointProvider;


    public LandmarkImageFragment() {
        // Required empty public constructor
    }

    /**
     * Provide a new instance of the fragment
     *
     * @param url the image Url to load
     * @return A new instance of LandmarkImageFragment.
     */
    public static LandmarkImageFragment newInstance(String url) {
        LandmarkImageFragment fragment = new LandmarkImageFragment();
        Bundle args = new Bundle();
        args.putString(STRING_URL, url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUrl = getArguments().getString(STRING_URL);
        }

        ((CulturalCapitalApp)getActivity().getApplication()).getComponent().inject(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentClickListener) {
            this.mListener = (OnFragmentClickListener)context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentClickListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ImageView rootView = (ImageView)inflater.inflate(R.layout.fragment_landmark_image, container, false);

        String path = new StringBuilder(endpointProvider.getLandmarkImagesFolder())
                .append(imageUrl)
                .toString();

        Glide.with(this)
                .load(path)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        mAttacher.setZoomable(true);
                        return false;
                    }
                })
                .into(rootView);

        mAttacher = new PhotoViewAttacher(rootView);
        mAttacher.setZoomable(false);  //  bug workaround, set to true when image is loaded
        mAttacher.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {  //  show/hide controls
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                mListener.onFragmentClick();
            }

            @Override
            public void onOutsidePhotoTap() {
                mListener.onFragmentClick();
            }
        });

        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFragmentClick();
            }
        });

        return rootView;
    }

    public interface OnFragmentClickListener {
        void onFragmentClick();
    }
}
