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

package com.trackandtalk.pafos17.notifications;

import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.support.annotation.WorkerThread;

import com.trackandtalk.pafos17.data.db.DbHelper;
import com.trackandtalk.pafos17.data.model.CulturalEvent;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * <p>Responsible for scheduling notifications</p>
 *
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */
@WorkerThread
@Singleton
public class NotificationScheduler implements SharedPreferences.OnSharedPreferenceChangeListener{

    private Application application;
    private AlarmManager alarmManager;
    private DbHelper dbHelper;
    private SharedPreferences defaultSharedPrefs;

    private int preferenceHoursBefore;


    @Inject
    public NotificationScheduler(Application application, AlarmManager alarmManager, DbHelper dbHelper, SharedPreferences defaultSharedPrefs) {
        this.application = application;
        this.alarmManager = alarmManager;
        this.dbHelper = dbHelper;
        this.defaultSharedPrefs = defaultSharedPrefs;

        preferenceHoursBefore = Integer.parseInt(defaultSharedPrefs.getString("notifications", "1"));
        defaultSharedPrefs.registerOnSharedPreferenceChangeListener(this);

//        try {
//            preferenceHoursBefore = Integer.parseInt(defaultSharedPrefs.getString("notifications", "1"));
//        } catch (NumberFormatException e) {  //  something terrible has happened, setting wasn't an integer
//            preferenceHoursBefore = 1; //  setting to default (1 hour before)
//            e.printStackTrace();
//        }
    }

    public void setEventNotifications(int[] eventIDs) {
        long timeFromNow = 3000; //  milliseconds

        for (int eventID : eventIDs) {
            Intent intent = new Intent(application, NotificationPublisher.class);
            intent.putExtra(NotificationPublisher.NOTIFICATION_ID, eventID);
            intent.putExtra(NotificationPublisher.NOTIFICATION_TYPE, NotificationPublisher.TYPE_EVENT);


            PendingIntent pi = PendingIntent.getBroadcast(application, eventID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + timeFromNow, pi);
            timeFromNow += 6000;  //  increment by 6000 milliseconds
        }
    }

    /**
     * <p>Schedules notifications for all user-favourited events, depending on "notify before" setting</p>
     * <p>Called when the favourited events are restored from backup, when the language changes (for new strings),
     * or when the device boots up (because the previous alarms don't persist between reboots)</p>
     */
    public void setAllFavouritesNotifications() {

        Set<Integer> favouriteIds = dbHelper.getFavouriteIds();
        if (preferenceHoursBefore > 0) {  //  not "never"
            for (Integer favouriteId : favouriteIds) {
                CulturalEvent event = dbHelper.getEventById(favouriteId);

                if (event != null && shouldNotify(event)) {

                    Intent intent = new Intent(application, NotificationPublisher.class);
                    intent.putExtra(NotificationPublisher.NOTIFICATION_ID, favouriteId);
                    intent.putExtra(NotificationPublisher.NOTIFICATION_TYPE, NotificationPublisher.TYPE_EVENT);

                    PendingIntent pi = PendingIntent.getBroadcast(application, favouriteId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    LocalDateTime notifyAt = event.getBeginDatetime().minusHours(preferenceHoursBefore);
                    long notifyAtEpochMillis = notifyAt.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
                    alarmManager.set(AlarmManager.RTC_WAKEUP, notifyAtEpochMillis, pi);
                }
            }
        }
    }

    /**
     * Schedule a notification for the favourited event
     *
     */
    public void scheduleEventNotification(CulturalEvent event) {

        if (preferenceHoursBefore > 0 && shouldNotify(event)) { //  not "notify never" & event is in the future (or ongoing)
            Intent intent = new Intent(application, NotificationPublisher.class);
            intent.putExtra(NotificationPublisher.NOTIFICATION_ID, event.getTrId());
            intent.putExtra(NotificationPublisher.NOTIFICATION_TYPE, NotificationPublisher.TYPE_EVENT);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(application, event.getTrId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

            LocalDateTime notifyAt = event.getBeginDatetime().minusHours(preferenceHoursBefore);
            long notifyAtEpochMillis = notifyAt.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;
            alarmManager.set(AlarmManager.RTC_WAKEUP, notifyAtEpochMillis, pendingIntent);
        }

    }

    /**
     * <p>Cancel the notification for the un-favourited event</p>
     * <p>We need to first create the pending intent (which is identical to the one for scheduling), then call the alarm manager to cancel it</p>
     *
     */
    public void cancelEventNotification(CulturalEvent event) {
        Intent intent = new Intent(application, NotificationPublisher.class);
        intent.putExtra(NotificationPublisher.NOTIFICATION_ID, event.getTrId());
        intent.putExtra(NotificationPublisher.NOTIFICATION_TYPE, NotificationPublisher.TYPE_EVENT);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(application, event.getTrId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

    /**
     * <p>Figure out if we should notify about the event</p>
     *
     * @param event
     *
     * @return  true if the event is in the future or ongoing, false otherwise
     */
    private boolean shouldNotify(CulturalEvent event) {
        LocalDateTime starts = event.getBeginDatetime();
        LocalDateTime ends = event.getFinishDatetime();
        LocalDateTime now = LocalDateTime.now();

        if (ends != null) {  //  has an end datetime
            if (starts.isAfter(now)  //  starts in the future
                    || (ends.isAfter(now) && starts.isBefore(now))) {  //  has started but hasn't ended
                return true;
            }
        } else {  //  only starting datetime
            if (starts.isAfter(now)) return true;
        }

        return false;
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("notifications")) {
            preferenceHoursBefore = Integer.parseInt(defaultSharedPrefs.getString(key, "1"));
            setAllFavouritesNotifications();
        }
    }
}
