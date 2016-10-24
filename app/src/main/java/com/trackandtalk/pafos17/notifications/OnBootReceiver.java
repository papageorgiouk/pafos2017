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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.trackandtalk.pafos17.CulturalCapitalApp;

import javax.inject.Inject;

/**
 *
 * <p>Set favourite notifications on boot, because they don't persist</p>
 *
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */
public class OnBootReceiver extends BroadcastReceiver {

    @Inject
    NotificationScheduler notificationScheduler;

    public OnBootReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        ((CulturalCapitalApp)context.getApplicationContext()).getComponent().inject(this);

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) notificationScheduler.setAllFavouritesNotifications();
    }
}
