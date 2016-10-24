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

package com.trackandtalk.pafos17.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.trackandtalk.pafos17.CulturalCapitalApp;
import com.trackandtalk.pafos17.R;
import com.trackandtalk.pafos17.data.model.Landmark;
import com.trackandtalk.pafos17.network.EndpointProvider;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */
public class LandmarkImagesAdapter extends RecyclerView.Adapter<LandmarkImagesAdapter.ImageViewHolder> {
    private Context context;
    private Landmark landmark;
    private List<String> imgURLs;
    private int screenWidth;
    private int screenHeight;
    private LandmarkImageClickListener mListener;
    @Inject EndpointProvider endpointProvider;

    public LandmarkImagesAdapter(Context context, Landmark landmark) {
        this.context = context;
        this.landmark = landmark;
        this.imgURLs = landmark.getImageURLs();

        ((CulturalCapitalApp)context.getApplicationContext()).getComponent().inject(this);

        //  get dimensions
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        this.screenWidth = metrics.widthPixels;
        this.screenHeight = metrics.heightPixels;
    }

    public void setImageClickListener(LandmarkImageClickListener listener) {
        this.mListener = listener;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.landmark_image, parent, false);

        //  get screen width, to set the card layout at percentage
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        this.screenWidth = metrics.widthPixels;

        //  set photo width & height to percentage of screen
        context.getSystemService(Context.WINDOW_SERVICE);
        ViewGroup.LayoutParams params =  view.getLayoutParams();
        params.width = screenWidth * 9 / 10;
        params.height = screenHeight * 3 / 10;
        view.setLayoutParams(params);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {
        String path = new StringBuilder(endpointProvider
                .getLandmarkImagesFolder())  //  base url
                .append(imgURLs.get(position))
                .toString();
        Uri uri = Uri.parse(path);
        Glide.with(context.getApplicationContext())
                .fromUri()
                .load(uri)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.image);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick(v, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (landmark.getImageURLs() != null) {
            return landmark.getImageURLs().size();
        } else {
            return 0;
        }
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        ImageViewHolder(View itemView) {
            super(itemView);
            this.image = (ImageView)itemView.findViewById(R.id.landmark_image);
        }
    }

    public interface LandmarkImageClickListener {
        void onClick(View v, int position);
    }
}
