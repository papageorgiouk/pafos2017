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

package com.trackandtalk.pafos17.data;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.trackandtalk.pafos17.data.db.DbHelper;
import com.trackandtalk.pafos17.data.model.CulturalEvent;
import com.trackandtalk.pafos17.helper.SharedPrefsHelper;
import com.trackandtalk.pafos17.network.EndpointProvider;
import com.trackandtalk.pafos17.network.ZoomService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * <p>Class to manage cultural events.</p>
 *
 * <p>Responsible for remote fetching, storing in local db, and fetching from db if required</p>
 *
 * <p>Created by Konstantinos Papageorgiou and Charalambos Xinaris</p>
 */

@Singleton
public class EventsManager {

    private ZoomService apiService;
    private EndpointProvider endpointProvider;
    private SharedPreferences defaultSharedPreferences;
    private SharedPrefsHelper userPrefsHelper;
    private DbHelper dbHelper;

    private EventsListener listener;


    @Inject
    EventsManager(
            ZoomService apiService,
            EndpointProvider endpointProvider,
            SharedPreferences defaultSharedPreferences,
            SharedPrefsHelper userPrefsHelper,
            DbHelper dbHelper) {

        this.apiService = apiService;
        this.endpointProvider = endpointProvider;
        this.defaultSharedPreferences = defaultSharedPreferences;
        this.userPrefsHelper = userPrefsHelper;
        this.dbHelper = dbHelper;
    }

    /**
     * <p>Get all the next events</p>
     *
     * <p>Makes a network request. If successful, updates the database</p>
     *
     * @param listener
     */
    public void getNextEvents(final EventsListener listener) {
        this.listener = listener;

        String language = getUserLanguage();
        String dateLastChecked = getDateLastChecked();

        String endpoint = endpointProvider.getEventsEndpoint();
        apiService.getEvents(endpoint, dateLastChecked, language).enqueue(new Callback<List<CulturalEvent>>() {
            @Override
            public void onResponse(Call<List<CulturalEvent>> call, Response<List<CulturalEvent>> response) {
                onResponseSuccess(response.body());
            }

            @Override
            public void onFailure(Call<List<CulturalEvent>> call, Throwable t) {
                onResponseFailure();
            }
        });
    }

    /**
     * <p>When the request succeeds, consolidate the new events in the database.
     * Then update the listener with events queried from the database</p>
     *
     * <p><strong>CAUTION! </strong>Even if the request is successful, it might still return 0 events.
     * In that case we perform a check</p>
     *
     * @param events  the events from the response. Might be 0;
     */
    private void onResponseSuccess(List<CulturalEvent> events) {
        if (events == null) {  //  still failed (returned zero events)
            onResponseFailure();
        } else {
            dbHelper.insertEventsToDb(events);  //  insert the fresh ones
            if (listener != null) {
                listener.onFetchEvents(fetchNextEventsFromDatabase(), true);
            }
        }

        userPrefsHelper.setLastCheckedDate(Calendar.getInstance().getTimeInMillis());
    }

    /**
     * <p>When the network connection failed, or if the request returned 0 events.</p>
     *
     * <p>Update the listener with events from local database, but notify that the data isn't "fresh"</p>
     *
     */
    private void onResponseFailure() {
        if (listener != null) {
            listener.onFetchEvents(fetchNextEventsFromDatabase(), false);
        }
    }

    /**
     * <p> Get the next events stored in the database.</p>
     * <p> Useful when we don't want to make yet another network call</p>
     *
     * @return  a list of events from our local database
     */
    public List<CulturalEvent> fetchNextEventsFromDatabase() {
        return dbHelper.getNextEvents();
    }

    private String getUserLanguage() {
        return defaultSharedPreferences.getString("language", "en");
    }

    private String getDateLastChecked() {
        long lastChecked = userPrefsHelper.getLastCheckedDate();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat niceDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        niceDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        String dateLastChecked;
        if (lastChecked == 0 ) {
            dateLastChecked = "0";
        } else {
            dateLastChecked = niceDateFormat.format(new Date(lastChecked));
        }

        return dateLastChecked;
    }


    public interface EventsListener {

        /**
         * Callback when events are fetched
         *
         * @param events  the returned events, may be stale
         * @param fresh  true if fetched from online API, false if from offline cache
         */
        void onFetchEvents(@Nullable List<CulturalEvent> events, boolean fresh);

        void onFailure();
    }
}
