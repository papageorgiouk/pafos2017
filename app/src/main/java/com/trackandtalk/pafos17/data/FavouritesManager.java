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

import android.support.annotation.WorkerThread;

import com.birbit.android.jobqueue.JobManager;
import com.trackandtalk.pafos17.notifications.NotificationScheduler;
import com.trackandtalk.pafos17.data.model.CulturalEvent;
import com.trackandtalk.pafos17.data.db.DbHelper;
import com.trackandtalk.pafos17.network.jobs.PostFavouritesJob;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * <p>Class to manage favourited events</p>
 *
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

@Singleton
public class FavouritesManager {

    private DbHelper dbHelper;
    private NotificationScheduler notificationScheduler;
    private JobManager jobManager;

    @Inject
    FavouritesManager(
            DbHelper dbHelper,
            NotificationScheduler scheduler,
            JobManager jobManager) {

        this.dbHelper = dbHelper;
        this.notificationScheduler = scheduler;
        this.jobManager = jobManager;
    }

    /**
     * Add or remove as favourite depending on current status
     * If it's a favourite, it removes it. Otherwise, it adds it
     *
     * @param event event to act upon
     * @param listener callback for result
     *
     */
    public void addRemoveFavouriteInteraction(CulturalEvent event, FavouritesInteractionListener listener) {

        if (isFavourited(event)) {
            removeFromFavourites(event);  //  remove it
            listener.onRemoved();  //  notify that it has been removed
        } else {
            addAsFavourite(event);  //  add it
            listener.onAdded();  //  notify that it has been added
        }

    }

    private void addAsFavourite(final CulturalEvent event) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dbHelper.addEventToFavourites(event.getTrId());  //  add to favourites
                notificationScheduler.scheduleEventNotification(event);
                postFavourites();
            }
        }).run();
    }

    private void removeFromFavourites(final CulturalEvent event) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                dbHelper.removeEventFromFavourites(event.getTrId());  //  remove
                notificationScheduler.cancelEventNotification(event);
                postFavourites();
            }
        }).run();
    }

    /**
     * <p>Insert event IDs in the db as favourites and update the notifications</p>
     * <p>For when the user logs in and we get back their backed-up favourite events</p>
     *
     * @param ids  the list of event TrIds
     */
    public void restoreFavourites(final int[] ids) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int id : ids) {
                    dbHelper.addEventToFavourites(id);
                }
                updateNotifications();
            }
        }).run();
    }

    /**
     * <p>Remote backup of user favourites after every add/remove operation</p>
     * <p>Schedules a job to upload all current favourites. The jobs are grouped together, to avoid redundancy</p>
     */
    @WorkerThread
    private void postFavourites() {
        Set<Integer> favourites = getFavouriteIds();

        jobManager.addJobInBackground(new PostFavouritesJob(null, favourites));
    }

    @WorkerThread
    private Set<Integer> getFavouriteIds() {
         return dbHelper.getFavouriteIds();
    }

    /**
     * Checks the database if the event exists in the favourites.
     *
     * @param event  the event to check
     *
     * @return true if event is favourited
     */
    public boolean isFavourited(CulturalEvent event) {
        return dbHelper.isEventFavourite(event.getTrId());
    }

    @WorkerThread
    private void updateNotifications() {
        notificationScheduler.setAllFavouritesNotifications();
    }

    /**
     * Callback listener for results on "Favourites" actions
     */
    public interface FavouritesInteractionListener {

        /**
         * Called if the event was added successfully
         */
        void onAdded();

        /**
         * Called if the event was removed successfully
         */
        void onRemoved();

    }
}
