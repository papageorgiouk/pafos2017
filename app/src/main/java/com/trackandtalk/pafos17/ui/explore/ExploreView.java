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

import com.trackandtalk.pafos17.data.model.CulturalEvent;
import com.trackandtalk.pafos17.data.model.EventLocation;
import com.trackandtalk.pafos17.data.model.Landmark;
import com.trackandtalk.pafos17.ui.base.MvpView;

import java.util.List;
import java.util.Set;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

public interface ExploreView extends MvpView {

    void setupMap();

    void displayEventsList(@Nullable List<CulturalEvent> eventList);

    void displayEventLocations(Set<EventLocation> locations);

    void displayLandmarks(List<Landmark> landmarks);

    /**
     * <p>Show details for the selected landmark</p>
     * <p>We keep its view state so
     * we can restore it on config changes</p>
     *
     * @param landmark  the landmark to show details about
     * @param state  the current display status of the landmark. Null if we aren't restoring anything
     */
    void showLandmarkDetails(Landmark landmark, @Nullable ExplorePresenter.LandmarkViewState state);

    /**
     * <p>Starts map intent so the user can navigate to the landmark</p>
     *
     * @param uriString  the String to parse as an intent uri
     */
    void openLandmarkMap(String uriString);
}
