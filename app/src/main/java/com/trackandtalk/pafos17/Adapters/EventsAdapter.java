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
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.trackandtalk.pafos17.CulturalCapitalApp;
import com.trackandtalk.pafos17.R;
import com.trackandtalk.pafos17.data.model.CulturalEvent;
import com.trackandtalk.pafos17.helper.EventLeadingMarginSpan;
import com.trackandtalk.pafos17.ui.utils.DateFormatUtils;

import org.threeten.bp.LocalDateTime;

import java.util.List;

/**
  * Created by Konstantinos Papageorgiou and Charalambos Xinaris
  */
public class EventsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private CulturalCapitalApp application;
    private List<CulturalEvent> events;
    private OnEventClickedListener listener;

    private static final int ITEM_WHATS_NEXT = 0;
    private static final int ITEM_EVENT = 1;

    //  Extra views
    private boolean showHeader = false;
    private String header = null;
    private int extraViews = 0;

    public EventsAdapter(Context context, List<CulturalEvent> events, @Nullable String header) {
        this.context = context;
        this.application = (CulturalCapitalApp)context.getApplicationContext();
        this.events = events;

        if (header != null) {
            this.showHeader = true;
            extraViews++;
            this.header = header;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);

        if (viewType == ITEM_WHATS_NEXT) {
            View captionView = layoutInflater.inflate(R.layout.list_item_whats_next, parent, false);
            if (events.size() == 0) {
                ((TextView)captionView.findViewById(R.id.list_header)).setText(context.getString(R.string.no_events));
            } else {
                ((TextView)captionView.findViewById(R.id.list_header)).setText(header);
            }

            return new WhatsNextVH(captionView);
        } else {
            View eventView  = layoutInflater.inflate(R.layout.event_list_item, parent, false);
            return new EventsVH(eventView);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof WhatsNextVH) {
          WhatsNextVH whatsNextVH = (WhatsNextVH)holder;

        } else if (holder instanceof EventsVH) {
            final EventsVH eventsVH = (EventsVH)holder;


            final CulturalEvent event = events.get(position - extraViews); //  because the first is "What's next"
            String imgUrl = context.getString(R.string.base_url)
                    + context.getString(R.string.images_folder_url)
                    + event.getImagePath();
            Uri uri = Uri.parse(imgUrl);
            Glide.with(context.getApplicationContext())
                    .fromUri()
                    .load(uri)
                    .placeholder(R.drawable.placeholder_image)
                    .dontTransform()
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(eventsVH.image);

            eventsVH.eventTitle.setText(event.getEventTitle());
            eventsVH.dayMonth.setText(DateFormatUtils
                    .formatDateShort(event.getBeginDatetime(), application.getLocale())
                    .replace(" ", "\n"));  //  vertical
            eventsVH.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        listener.onEventClicked(eventsVH, event);
                    }
                }
            });

            //  event description, set span margins so as to not overlap the displayed date and time
            final SpannableString text = new SpannableString(Html.fromHtml(event.getEventDescription()));
            eventsVH.eventDescr.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {  //  when it's actually drawn, so we don't get dimensions as 0
                    eventsVH.eventDescr.getViewTreeObserver().removeOnPreDrawListener(this);  //  we don't need it anymore

                    //  calculate the number of lines to set margin to
                    int dateHeight = eventsVH.dateContainer.getMeasuredHeight();
                    int lineHeight = eventsVH.eventDescr.getLineHeight();
                    int numberOfLines = (int)Math.ceil(dateHeight/lineHeight);

                    //  margin = date width
                    int dateWidth = eventsVH.dateContainer.getMeasuredWidth();
                    text.setSpan(new EventLeadingMarginSpan(numberOfLines, dateWidth), 0, text.length(), 0);
                    eventsVH.eventDescr.setText(text);
                    return true;
                }
            });

            if (eventHasEnded(event)) {  //  event has ended
                holder.itemView.setAlpha(0.6f);
            } else {
                holder.itemView.setAlpha(1f);
            }
        }
    }

    /**
     * Has the event ended?
     *
     * @param event
     * @return  true if both start and end (if exists) have passed, false if start or end are in the future
     */
    private boolean eventHasEnded(CulturalEvent event) {
        LocalDateTime now = LocalDateTime.now();

        if (event.getFinishDatetime() != null) {
            return event.getBeginDatetime().isBefore(now)
                    && event.getFinishDatetime().isBefore(now);
        } else {
            return event.getBeginDatetime().isBefore(now);
        }
    }

    @Override
    public int getItemCount() {
        if (showHeader) {
            return events.size() + 1; //  + "What's next"
        } else {
            return events.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (showHeader && position == 0) {
            return ITEM_WHATS_NEXT;
        } else {
            return ITEM_EVENT;
        }
    }

    public void setOnEventClickedListener (OnEventClickedListener listener) {
        this.listener = listener;

    }

    public void setEvents(List<CulturalEvent> events) {
        this.events.clear();
        this.events.addAll(events);
        notifyDataSetChanged();
    }

    public static class WhatsNextVH extends RecyclerView.ViewHolder {

        private TextView header;

        public WhatsNextVH(View view) {
            super(view);
            header = (TextView)view.findViewById(R.id.list_header);

        }
    }

    public class EventsVH extends RecyclerView.ViewHolder {

        public ImageView image;
        TextView eventTitle;
        TextView eventDescr;

        //  date
        LinearLayout dateContainer;
        TextView dayMonth;

        EventsVH(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.event_image);
            eventTitle = (TextView) itemView.findViewById(R.id.event_title);
            eventDescr = (TextView)itemView.findViewById(R.id.event_descr);

            dateContainer = (LinearLayout)itemView.findViewById(R.id.date_container);
            dayMonth = (TextView)itemView.findViewById(R.id.day_month);
        }
    }

    public interface OnEventClickedListener {
        void onEventClicked(EventsVH viewholder, CulturalEvent event);
    }
}
