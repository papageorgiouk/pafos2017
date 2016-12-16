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

import com.trackandtalk.pafos17.data.model.CulturalEvent;
import com.trackandtalk.pafos17.ui.base.MvpView;

import org.threeten.bp.LocalDate;

import java.util.Set;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

public interface ScheduleView extends MvpView {

    void initCalendar();

    void highlightDates(Set<LocalDate> dates);

    /**
     * Indicate that the user has no favourites to display on the calendar
     */
    void showNoFavsAtAll();

    /**
     * Show favourites on selected day
     *
     * @param events
     */
    void showFavsOnDay(Set<CulturalEvent> events);

    /**
     * Indicate that selected day has no favourites
     */
    void showNoFavsOnDay();

    /**
     * Clear the space showing details
     */
    void clear();

    /**
     * Enable or disable calendar day selection, depending on whether the user has added favourites
     *
     * @param enable
     */
    void enableCalendar(boolean enable);

}
