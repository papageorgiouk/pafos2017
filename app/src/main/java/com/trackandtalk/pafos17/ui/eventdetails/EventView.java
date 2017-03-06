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

package com.trackandtalk.pafos17.ui.eventdetails;

import android.support.annotation.Nullable;
import android.text.Spanned;

import com.trackandtalk.pafos17.ui.base.MvpView;

import org.threeten.bp.LocalDateTime;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

public interface EventView extends MvpView {

    /**
     * Set the event's title
     *
     * @param title  the title
     */
    void showTitle(Spanned title);

    /**
     * Set the event's start date and time and, if it exists, the end date
     *
     * @param start  start datetime
     * @param end  end datetime
     */
    void showDate(LocalDateTime start, @Nullable LocalDateTime end);

    /**
     * Display whether the event is happening today
     *
     * @param isToday
     */
    void showIsToday(boolean isToday);

    void showImage(String imageUrl);

    void showDescription(Spanned string);

    /**
     * Show creators (artists) if available
     *
     * @param creators Spanned String with creators' info
     */
    void showCreators(Spanned creators);

    /**
     * Manage the on-screen representation of the event, depending if it's a favourite or not
     *
     * @param favourite true if it's a favourite, false otherwise
     */
    void showFavouriteStatus(boolean favourite);

    /**
     * Notify the user whether the event has been added or removed from favourites
     *
     * @param favourited true if added, false otherwise
     */
    void notifyFavouriteStatus(boolean favourited);

    /**
     * Prompt the user to log in
     */
    void startLoginActivity();

    /**
     * Show the event venue on the map. Called when the "map ready" async operation is done
     *
     * @param lat  venue's latitude
     * @param lon  venue's longitude
     * @param name  venue's name
     */
    void showMap(double lat, double lon, String name);
}
