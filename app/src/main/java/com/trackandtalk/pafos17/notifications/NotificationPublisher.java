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

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Html;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.trackandtalk.pafos17.CulturalCapitalApp;
import com.trackandtalk.pafos17.R;
import com.trackandtalk.pafos17.data.model.CulturalEvent;
import com.trackandtalk.pafos17.data.db.DbHelper;
import com.trackandtalk.pafos17.ui.eventdetails.EventActivity;
import com.trackandtalk.pafos17.ui.utils.ImageUtils;

import java.util.Locale;

import javax.inject.Inject;

/**
 * <p>Publishes notifications when the broadcast is sent</p>
 * <p>In this case, the broadcast is done by a pending intent, scheduled by the {@link NotificationScheduler}</p>
 *
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */
public class NotificationPublisher extends BroadcastReceiver {

    private Context context;
    NotificationManagerCompat notificationManager;
    @Inject DbHelper dbHelper;

    public static String NOTIFICATION_ID = "notification_id";
    public static String NOTIFICATION_TYPE = "type";
    public static String TYPE_EVENT = "event";

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        ((CulturalCapitalApp)context.getApplicationContext()).getComponent().inject(this);

        this.notificationManager = NotificationManagerCompat.from(context);

        String eventType = intent.getStringExtra(NOTIFICATION_TYPE);

        if (eventType.equals(TYPE_EVENT)) {
            final int eventID = intent.getIntExtra(NOTIFICATION_ID, 0);
            if (eventID != 0) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        showEventNotification(eventID);
                    }
                };
                new Thread(runnable).run();
            }
        }
    }

    private void showEventNotification(final int eventID) {

        final CulturalEvent event = dbHelper.getEventById(eventID);

        long eventStart = event.getStartDate();  //  seconds
//        long eventEnd = event.getEndDate();  //  seconds

        double dtInHours = Math.round(((double)eventStart - System.currentTimeMillis()/1000d) / 3600d);  //  hours
//        double dtInHours = Math.round((1464270300L - System.currentTimeMillis()/1000d) / 3600d);  //  hours
        final String text;

        if (dtInHours > 0) {  //  event hasn't started yet
            if (dtInHours == 1) {  //  1 hour, singular
                text = String.format(Locale.US, context.getString(R.string.event_starts_in), (int)dtInHours, context.getString(R.string.hour));
//                text = context.getString(R.string.event_starts_in) + dtInHours + " " + context.getString(R.string.hour);
            } else {  //  multiple hours, plural
                text = String.format(Locale.US, context.getString(R.string.event_starts_in), (int)dtInHours, context.getString(R.string.hours));
//                text = context.getString(R.string.event_starts_in) + dtInHours + " " + context.getString(R.string.hours);
            }
        }  else {  //  event has already started
            if (dtInHours == -1) {  //  1 hour ago, singular
                text = String.format(Locale.US, context.getString(R.string.event_started_ago), Math.abs((int)dtInHours), context.getString(R.string.hour));
//                text = context.getString(R.string.event_started) + Math.abs(dtInHours) + " " + context.getString(R.string.hour) + " " + context.getString(R.string.ago);
            } else {  //  X hours ago, plural
                text = String.format(Locale.US, context.getString(R.string.event_started_ago), Math.abs((int)dtInHours), context.getString(R.string.hours));
//                text = context.getString(R.string.event_started) + Math.abs(dtInHours) + " " + context.getString(R.string.hours) + " " + context.getString(R.string.ago);
            }
        }

        //  notification button share
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, event.getEventTitle()).setType("text/plain");
        PendingIntent pendingShareIntent = PendingIntent.getActivity(context, 55, Intent.createChooser(shareIntent, "Send to"), PendingIntent.FLAG_UPDATE_CURRENT);
        final NotificationCompat.Action shareAction = new NotificationCompat.Action(R.drawable.ic_share_48pt_2x, "Share", pendingShareIntent);

        //  notification button map
        String place = event.getLocation().getLocationName();
        String s = String.format(Locale.US, "geo:0,0?q=%f,%f(%s)", event.getLocation().getLatitude(), event.getLocation().getLongitude(), place);
        Uri uri = Uri.parse(s);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        final PendingIntent pendingMapIntent = PendingIntent.getActivity(context, 555, Intent.createChooser(mapIntent, "Show location in "), PendingIntent.FLAG_UPDATE_CURRENT);
        final NotificationCompat.Action mapAction = new NotificationCompat.Action(R.drawable.ic_map_48pt_2x, "Map", pendingMapIntent);

        String imgUrl = context.getString(R.string.base_url) + context.getString(R.string.images_folder_url) + event.getImagePath();
        Uri imgUri = Uri.parse(imgUrl);
        Glide.with(context.getApplicationContext())
                .load(imgUri)
                .asBitmap()
                .placeholder(R.drawable.placeholder_image)
                .centerCrop()
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                        final Bitmap roundedBitmap = ImageUtils.getRoundedBitmap(resource);  //  round the image
                        Intent intent = new Intent(context.getApplicationContext(), EventActivity.class);  //  open EventActivity when clicked
                        intent.putExtra(CulturalEvent.INTENT_EXTRA_NAME, event);

                        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                                .setLargeIcon(roundedBitmap)
                                .setSmallIcon(R.drawable.ic_stat_logo_all_white)
                                .setColor(ResourcesCompat.getColor(context.getResources(), R.color.colorPrimary, null))
                                .setContentTitle(context.getString(R.string.pafos_event) + " - " + event.getEventTitle())
                                .setContentIntent(PendingIntent.getActivity(context.getApplicationContext(), eventID, intent, PendingIntent.FLAG_ONE_SHOT))
                                .setCategory(NotificationCompat.CATEGORY_EVENT)
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setWhen(event.getStartDate() * 1000)  //  (event.getStartDate() * 1000)
                                .setVibrate(new long[] {})
                                .setContentText(text)
                                .setContentInfo("ID: " + String.valueOf(eventID))
                                .addAction(mapAction)
                                .addAction(shareAction)
                                .setAutoCancel(true);

                        //  expanded style
                        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle(notificationBuilder);
                        style.setBigContentTitle(event.getEventTitle());
                        style.bigText(Html.fromHtml(event.getEventDescription()));

//                        notificationBuilder.flags = Notification.DEFAULT_LIGHTS | Notification.FLAG_AUTO_CANCEL;  //  dismiss when clicked
                        int notificationId = eventID << 2;
                        notificationManager.notify(notificationId, notificationBuilder.build());
                    }
                });



    }
}
