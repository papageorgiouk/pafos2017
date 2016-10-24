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
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.trackandtalk.pafos17.CulturalCapitalApp;
import com.trackandtalk.pafos17.R;
import com.trackandtalk.pafos17.data.model.CulturalEvent;
import com.trackandtalk.pafos17.network.EndpointProvider;
import com.trackandtalk.pafos17.ui.utils.DateFormatUtils;

import java.util.List;

import javax.inject.Inject;

/**
  * Created by Konstantinos Papageorgiou and Charalambos Xinaris
  */
public class MapCarouselAdapter extends RecyclerView.Adapter<MapCarouselAdapter.ViewHolder> {

    private Context context;
    private List<CulturalEvent> events;
    private int screenWidth;
    private OnItemClickListener mListener;
    @Inject CulturalCapitalApp application;
    @Inject EndpointProvider endpointProvider;

    public MapCarouselAdapter(Context context, List<CulturalEvent> events) {
        this.context = context;
        this.events = events;

        ((CulturalCapitalApp)context.getApplicationContext()).getComponent().inject(this);

        //  get screen width, to set the card layout at percentage
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        this.screenWidth = metrics.widthPixels;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View card = layoutInflater.inflate(R.layout.carousel_card, parent, false);

        //  set card width to percentage of screen width
        context.getSystemService(Context.WINDOW_SERVICE);
        ViewGroup.LayoutParams params =  card.getLayoutParams();
        params.width = screenWidth * 6 / 10;
        card.setLayoutParams(params);  //  set card width to percentage of screenWidth
        return new ViewHolder(card);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CulturalEvent event = events.get(position);

        //  image
        Uri imgUri = Uri.parse(endpointProvider.getFullImagesUrl()
                + event.getImagePath());
        Glide.with(context)
                .fromUri()
                .load(imgUri)
                .placeholder(R.drawable.placeholder_image)
                .dontTransform()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(holder.image);

        //  title
        holder.title.setText(event.getEventTitle());

        //  subtitle
        holder.subtitle.setText(String.valueOf(position));

        //  day and month
        String formattedDate = DateFormatUtils
                .formatDateShort(event.getBeginDatetime(), application.getLocale())
                .replace(" ", "\n");  //  vertical
        holder.dayMonth.setText(formattedDate);

        //  on click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onItemClicked(holder, v, event);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        private TextView title;
        private TextView subtitle;
        private TextView dayMonth;

        ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView)itemView.findViewById(R.id.carousel_image);
            title = (TextView)itemView.findViewById(R.id.carousel_title);
            subtitle = (TextView)itemView.findViewById(R.id.carousel_subtitle);
            dayMonth = (TextView)itemView.findViewById(R.id.day_month);
        }
    }

    public interface OnItemClickListener {
        void onItemClicked(ViewHolder holder, View view, CulturalEvent event);
    }
}
