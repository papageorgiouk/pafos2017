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

package com.trackandtalk.pafos17.network;

import android.content.Context;

import com.trackandtalk.pafos17.R;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Class to provide the strings of API endpoints from api_endpoints.xml.
 * This is done to avoid having to provide Context in the Model layer
 *
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

@Singleton
public class EndpointProvider {

    private Context context;

    @Inject
    public EndpointProvider(Context context) {
        this.context = context;
    }

    public String getEventsEndpoint() {
        return context.getString(R.string.get_events_URL);
    }

    public String getImagesEndpoint() {
        return context.getString(R.string.images_folder_url);
    }

    public String getFullImagesUrl() {
        return context.getString(R.string.base_url) + context.getString(R.string.images_folder_url);
    }

    public String getLandmarkImagesFolder() {
        return context.getString(R.string.base_url) + context.getString(R.string.images_folder_url) + "landmarks/";
    }

    public String getLoginAccountEndpoint() {
        return context.getString(R.string.account_url);
    }

    public String getFavouritesEndpoint() {
        return context.getString(R.string.post_favourites_url);
    }

    public String getFeedbackEndpoint() {
        return context.getString(R.string.feedback_url);
    }

    public String getLandmarksEndpoint() {
        return context.getString(R.string.landmarks_url);
    }
}
