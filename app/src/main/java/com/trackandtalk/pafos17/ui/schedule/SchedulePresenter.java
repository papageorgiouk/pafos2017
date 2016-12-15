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
import com.trackandtalk.pafos17.data.db.DbHelper;
import com.trackandtalk.pafos17.ui.base.BasePresenter;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

public class SchedulePresenter extends BasePresenter<ScheduleView> {

    private DbHelper dbHelper;
    private List<CulturalEvent> favourites;
    private Set<LocalDate> datesWithEvents;

    @Inject
    SchedulePresenter(DbHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    @Override
    public void attachView(ScheduleView mvpView) {
        super.attachView(mvpView);

        favourites = dbHelper.getFavourites();
        datesWithEvents = getDatesOfFavourites(favourites);

        getView().initCalendar();
        if (favourites != null && favourites.size() > 0) {
            getView().highlightDates(datesWithEvents);
            getView().enableCalendar(true);

        } else {
            getView().clear();
            getView().showNoFavsAtAll();
            getView().enableCalendar(false);
        }
    }

    /**
     * <p>Get the list of dates that our events are happening on, regardless of the number of events on a particular day</p>
     *
     * @param favourites  events to get dates from
     * @return  a set of unique dates with favourited events
     */
    private Set<LocalDate> getDatesOfFavourites(List<CulturalEvent> favourites) {
        Set<LocalDate> dates = new HashSet<>();

        for (CulturalEvent e : favourites) {

            LocalDate start = e.getBeginDatetime().toLocalDate();

            //  add days between (and including) start and end
            if (e.getFinishDatetime() != null) {
                LocalDate end = e.getFinishDatetime().toLocalDate();
                while (!start.isAfter(end)) {
                    dates.add(start);
                    start = start.plusDays(1);
                }
            } else {  //  just add the start date
                dates.add(start);
            }
        }

        return dates;
    }

    /**
     * Find favourited events on this day, and act accordingly
     *
     * @param dateSelected  the selected date
     */
    void onDateSelected(LocalDate dateSelected) {
        List<CulturalEvent> eventsOnDay = new ArrayList<>();

        for (CulturalEvent event : favourites) {
            LocalDate starting = event.getBeginDatetime().toLocalDate();
            LocalDate ending = null;
            if (event.getFinishDatetime() != null) ending = event.getFinishDatetime().toLocalDate();

            //  if an event's start date falls on selected day, add it
            if (starting.isEqual(dateSelected)) eventsOnDay.add(event);

            //  if we have an end date and the selected day falls on the date range, add it
            if (ending != null) {
                if (isBetween(starting, ending, dateSelected)) eventsOnDay.add(event);
            }
        }

        if (isViewAttached()) {
            getView().clear();

            if (eventsOnDay.size() > 0) {
                getView().showFavsOnDay(eventsOnDay);
            } else {
                getView().showNoFavsOnDay();
            }
        }
    }

    /**
     * Whether a date is between two others
     *
     * @param start  start of range
     * @param end  end of range
     * @param target  the query
     * @return  true or false
     */
    private boolean isBetween(LocalDate start, LocalDate end, LocalDate target) {
        return !target.isBefore(start) && !target.isAfter(end);
    }

    @Override
    public void detachView() {
        super.detachView();
    }
}
