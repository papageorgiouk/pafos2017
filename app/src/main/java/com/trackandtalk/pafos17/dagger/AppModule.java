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

import android.app.AlarmManager;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.di.DependencyInjector;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.trackandtalk.pafos17.CulturalCapitalApp;
import com.trackandtalk.pafos17.R;
import com.trackandtalk.pafos17.network.ZoomService;
import com.trackandtalk.pafos17.network.jobs.PostFavouritesJob;

import java.io.File;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

@Module
public class AppModule {
    private CulturalCapitalApp mApp;
    private GoogleSignInOptions gso;

    public static final String FILES_DIRECTORY = "files_directory";

    public AppModule(CulturalCapitalApp application) {
        this.mApp = application;
    }

    @Provides
    @Singleton
    Application providesApplication() {
        return mApp;
    }

    @Provides
    @Singleton
    @Named(FILES_DIRECTORY)
    File providesFilesDirectory() {
        return mApp.getFilesDir();
    }

    @Provides
    @Singleton
    CulturalCapitalApp providesCulturalCapitalApp() {
        return (CulturalCapitalApp)mApp;
    }

    @Provides
    @Singleton
    SharedPreferences providesDefaultSharedPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(mApp);
    }

    @Provides
    @Singleton
    Context providesApplicationContext() {
        return mApp.getApplicationContext();
    }

    @Provides
    @Singleton
    ZoomService providesZoomService() {
        return ZoomService.Creator.newZoomService(mApp.getString(R.string.base_url));
    }

    @Provides
    @Singleton
    GoogleSignInOptions providesGoogleSignInOptions() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
              .requestEmail()
              .requestId()
              .requestProfile()
              .build();

        return gso;
    }

    @Provides
    @Singleton
    GoogleApiClient.Builder providesGoogleApiClientBuilder() {

        return new GoogleApiClient.Builder(mApp);

    }

    @Provides
    @Singleton
    AlarmManager providesAlarmManager() {
        return (AlarmManager)mApp.getSystemService(Context.ALARM_SERVICE);
    }

    @Provides
    @Singleton
    JobManager providesJobManager() {
        DependencyInjector injector = new DependencyInjector() {
            @Override
            public void inject(Job job) {
                mApp.getComponent().inject((PostFavouritesJob)job);
            }
        };

        Configuration configuration = new Configuration.Builder(mApp)
                .injector(injector)
                .build();

        return new JobManager(configuration);
    }
}
