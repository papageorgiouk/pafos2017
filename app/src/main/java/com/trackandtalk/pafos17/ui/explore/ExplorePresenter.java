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

package com.trackandtalk.pafos17.ui.explore;

import android.support.annotation.Nullable;

import com.trackandtalk.pafos17.data.EventsManager;
import com.trackandtalk.pafos17.data.LandmarksManager;
import com.trackandtalk.pafos17.data.model.CulturalEvent;
import com.trackandtalk.pafos17.data.model.EventLocation;
import com.trackandtalk.pafos17.data.model.Landmark;
import com.trackandtalk.pafos17.ui.base.BasePresenter;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

public class ExplorePresenter extends BasePresenter<ExploreView> {

    private LandmarksManager landmarksManager;
    private EventsManager eventsManager;

    private List<CulturalEvent> events;
    private Set<EventLocation> eventLocations;

    @Inject
    ExplorePresenter(LandmarksManager landmarksManager, EventsManager eventsManager) {
        this.landmarksManager = landmarksManager;
        this.eventsManager = eventsManager;

    }

    /**
     * <p>Notify that the map view is ready and display stuff on it</p>
     * <p>We also pass saved state stuff from onRestoreInstanceState, so we don't have to reload them</p>
     * <p>We only restore if they're null</p>
     *
     * @param events  saved state List of events
     * @param landmarks  saved state List of landmarks
     */
    void onMapReady(@Nullable List<CulturalEvent> events, @Nullable List<Landmark> landmarks) {
        //  restore or load events
        if (events != null) {
            this.events = events;
        } else {
            this.events = eventsManager.fetchNextEventsFromDatabase();
        }
        if (isViewAttached()) {
            getView().displayEventsList(this.events);
            getView().displayEventLocations(gatherUniqueEventLocations(this.events));
        }

        //  restore or load landmarks
        if (landmarks != null) {
            if (isViewAttached()) {
                getView().displayLandmarks(landmarks);
            }
        } else {
            landmarksManager.fetchLandmarks(new LandmarksManager.LandmarksListener() {
                @Override
                public void onFetched(List<Landmark> landmarks) {
                    if (isViewAttached()) {
                        getView().displayLandmarks(landmarks);
                    }
                }

                @Override
                public void onFailure() {

                }
            });
        }
    }

    void onLandmarkMapButtonClicked(Landmark landmark) {
        double latitude = landmark.getLatitude();
        double longitude = landmark.getLongitude();
        String name = landmark.getName();
        String uriString = String.format(Locale.US, "geo:0,0?q=%f,%f(%s)", latitude, longitude, name);

        if (isViewAttached()) {
            getView().openLandmarkMap(uriString);
        }
    }

    private Set<EventLocation> gatherUniqueEventLocations(List<CulturalEvent> events) {
        eventLocations = new HashSet<>();

        for (CulturalEvent event : events) {
            eventLocations.add(event.getLocation());
        }

        return eventLocations;
    }

    void onLandmarkMarkerClicked(Landmark landmark) {
        if (isViewAttached()) {
            getView().showLandmarkDetails(landmark, null);
        }
    }

    @Override
    public void attachView(ExploreView mvpView) {
        super.attachView(mvpView);

        mvpView.setupMap();
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    enum LandmarkViewState {
        EXPANDED, COLLAPSED, HIDDEN
    }
}
