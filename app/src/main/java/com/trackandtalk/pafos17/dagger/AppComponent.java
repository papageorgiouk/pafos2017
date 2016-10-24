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

package com.trackandtalk.pafos17.dagger;

import com.trackandtalk.pafos17.CulturalCapitalApp;
import com.trackandtalk.pafos17.adapters.LandmarkImagesAdapter;
import com.trackandtalk.pafos17.adapters.MapCarouselAdapter;
import com.trackandtalk.pafos17.network.jobs.PostFavouritesJob;
import com.trackandtalk.pafos17.notifications.NotificationPublisher;
import com.trackandtalk.pafos17.notifications.OnBootReceiver;
import com.trackandtalk.pafos17.slideshow.ImageSlideFragment;
import com.trackandtalk.pafos17.slideshow.LandmarkImageFragment;
import com.trackandtalk.pafos17.ui.eventdetails.EventActivity;
import com.trackandtalk.pafos17.ui.explore.ExploreActivity;
import com.trackandtalk.pafos17.ui.feedback.FeedbackActivity;
import com.trackandtalk.pafos17.ui.main.MainActivity;
import com.trackandtalk.pafos17.ui.schedule.ScheduleActivity;
import com.trackandtalk.pafos17.ui.signin.GoogleSignInActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

@Singleton
@Component(
        modules = {
                AppModule.class
        }
)

public interface AppComponent {

    void inject(CulturalCapitalApp app);
    void inject(ExploreActivity exploreActivity);
    void inject(GoogleSignInActivity googleSignInActivity);
    void inject(EventActivity eventActivity);
    void inject(FeedbackActivity feedbackActivity);
    void inject(MainActivity mainActivity);
    void inject(NotificationPublisher publisher);
    void inject(ScheduleActivity scheduleActivity);
    void inject(OnBootReceiver onBootReceiver);
    void inject(PostFavouritesJob job);
    void inject(MapCarouselAdapter adapter);
    void inject(LandmarkImagesAdapter adapter);
    void inject(LandmarkImageFragment adapter);
    void inject(ImageSlideFragment fragment);

}
