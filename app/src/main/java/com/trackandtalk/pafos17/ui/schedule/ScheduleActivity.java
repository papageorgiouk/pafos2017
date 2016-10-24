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

package com.trackandtalk.pafos17.ui.schedule;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.trackandtalk.pafos17.CulturalCapitalApp;
import com.trackandtalk.pafos17.R;
import com.trackandtalk.pafos17.data.model.CulturalEvent;
import com.trackandtalk.pafos17.network.EndpointProvider;
import com.trackandtalk.pafos17.ui.eventdetails.EventActivity;
import com.trackandtalk.pafos17.ui.utils.ImageUtils;
import com.trackandtalk.pafos17.ui.utils.PaletteTintTransformation;
import com.trackandtalk.pafos17.ui.utils.calendar.EventDecorator;

import org.threeten.bp.LocalDate;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */
public class ScheduleActivity extends AppCompatActivity implements ScheduleView {

    private MaterialCalendarView calendarView;
    private LinearLayout indicatorLayout;
    private LayoutInflater inflater;

    @Inject SchedulePresenter presenter;
    @Inject EndpointProvider endpointProvider;

    private static final int TEXT_SIZE = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        calendarView = (MaterialCalendarView)findViewById(R.id.calendar_view);
        indicatorLayout = (LinearLayout)findViewById(R.id.indicator_layout);
        inflater = getLayoutInflater();

        ((CulturalCapitalApp)getApplicationContext()).getComponent().inject(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        presenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void initCalendar() {
        calendarView.invalidateDecorators();
        calendarView.setDateSelected(Calendar.getInstance().getTime(), true);  //  today
        calendarView.setCurrentDate(Calendar.getInstance());
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if (selected) {
                    LocalDate converted = LocalDate.of(
                            date.getYear(),
                            date.getMonth() + 1,  //  January = 0
                            date.getDay());
                    presenter.onDateSelected(converted);
                }
            }
        });
    }

    @Override
    public void highlightDates(Set<LocalDate> dates) {
        Set<CalendarDay> calendarDays = new HashSet<>();

        //  transform to Calendar Days
        for (LocalDate date : dates) {
            CalendarDay day = CalendarDay.from(
                    date.getYear(),
                    date.getMonthValue() - 1,  //  because java.util.Calendar is a mess
                    date.getDayOfMonth()
            );
            calendarDays.add(day);
        }

        int decoratorColor = ContextCompat.getColor(this, R.color.colorAccentLight);
        calendarView.addDecorator(new EventDecorator(decoratorColor, calendarDays));
    }

    @Override
    public void showNoFavsAtAll() {
        TextView textView = new TextView(this);
        textView.setText(getString(R.string.no_events_in_schedule));
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);  //  bold
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextSize(TEXT_SIZE);
        textView.setTextColor(textView.getTextColors().withAlpha(160));

        indicatorLayout.addView(textView);
    }

    @Override
    public void showFavsOnDay(List<CulturalEvent> events) {
        for (CulturalEvent event : events) {
            final View card = inflater.inflate(R.layout.schedule_card, indicatorLayout, false);
            ImageView image = (ImageView)card.findViewById(R.id.image);
            TextView label = (TextView)card.findViewById(R.id.label);

            Uri imgUri = Uri.parse(endpointProvider.getFullImagesUrl() + event.getImagePath());
            Glide.with(this)
                    .fromUri()
                    .load(imgUri)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .transform(new PaletteTintTransformation(this))  //  for more legible text
                    .into(image);

            label.setText(event.getEventTitle());
            card.setTag(event);  //  for later retrieval
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CulturalEvent e = (CulturalEvent) v.getTag();
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(ScheduleActivity.this, v, getString(R.string.image_transition));
                    Intent intent = new Intent(ScheduleActivity.this, EventActivity.class);
                    intent.putExtra(CulturalEvent.INTENT_EXTRA_NAME, e);
                    startActivity(intent, options.toBundle());
                }
            });

            indicatorLayout.addView(card);
        }
    }

    @Override
    public void showNoFavsOnDay() {
        TextView textView = new TextView(this);
        textView.setText(getString(R.string.no_events_on_day));
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);  //  bold
        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        textView.setTextSize(TEXT_SIZE);
        textView.setTextColor(textView.getTextColors().withAlpha(100));
        indicatorLayout.addView(textView);
    }

    @Override
    public void clear() {
        indicatorLayout.removeAllViews();
    }

    @Override
    public void enableCalendar(boolean enable) {

        if (enable) {
            calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_SINGLE);
        } else {
            calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);
        }
    }
}
