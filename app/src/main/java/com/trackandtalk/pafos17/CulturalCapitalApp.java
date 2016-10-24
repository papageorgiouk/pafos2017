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

package com.trackandtalk.pafos17;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;

import com.facebook.stetho.Stetho;
import com.squareup.leakcanary.LeakCanary;
import com.trackandtalk.pafos17.dagger.AppComponent;
import com.trackandtalk.pafos17.dagger.AppModule;
import com.trackandtalk.pafos17.dagger.DaggerAppComponent;
import com.trackandtalk.pafos17.data.AccountManager;
import com.trackandtalk.pafos17.data.model.Account;
import com.trackandtalk.pafos17.data.db.DbHelper;
import com.trackandtalk.pafos17.helper.SharedPrefsHelper;
import com.trackandtalk.pafos17.notifications.NotificationScheduler;

import java.util.Locale;

import javax.inject.Inject;

/**
  * Created by Konstantinos Papageorgiou and Charalambos Xinaris
  */
public class CulturalCapitalApp extends Application implements SharedPreferences.OnSharedPreferenceChangeListener, AccountManager.AccountListener {

    AppComponent component;

    @Inject DbHelper dbHelper;
    @Inject SharedPrefsHelper prefsHelper;
    @Inject SharedPreferences appSettings;
    private Account account;
    @Inject AccountManager accountManager;
    @Inject NotificationScheduler notificationScheduler;

    private boolean firstRun;
    private int launchCount;
    private static final String PREFERENCE_LAUNCH_COUNTER = "launch_counter";
    private static final String PREFERENCE_IS_RATED = "is_rated";

    String langCode;  //  language code from settings


    @Override
    public void onCreate() {
        super.onCreate();

        //  leakcanary
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);

        //  dagger 2
        component = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
        component.inject(this);

//        //  stetho only for debug build
//        if (BuildConfig.DEBUG) {
//            Stetho.initializeWithDefaults(this);
//        }

        //  override device locale according to user settings
        langCode = appSettings.getString("language", "en");
        setLocale(langCode, false);
        appSettings.registerOnSharedPreferenceChangeListener(this);  //  listen for settings changes

        //  launch counter
        new Thread(new Runnable() {
            @Override
            public void run() {
                launchCount = appSettings.getInt(PREFERENCE_LAUNCH_COUNTER, 0);
                launchCount++;  //  increment it
                appSettings.edit().putInt(PREFERENCE_LAUNCH_COUNTER, launchCount).apply();
            }
        });

        account = accountManager.getAccount();
        accountManager.addAccountListener(this);

        firstRun = appSettings.getBoolean("first_run", true);
        if (firstRun) {
            appSettings.edit().putBoolean("first_run", false).apply();
        }

    }

    public AppComponent getComponent() {
        return component;
    }

    public DbHelper getDbHelper() {

        return dbHelper;
    }

    /**
     * Set the locale for the application.
     *
     * @param langCode ISO-639 language code
     * @param flushDatabase  if the setting is changed, we flush the db and set lastChecked=0 in order to redownload stuff in the new language
     */
    public void setLocale(String langCode, boolean flushDatabase) {
        Locale locale = new Locale(langCode);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.setLocale(locale);
        res.updateConfiguration(conf, dm);

        this.langCode = langCode;
        PreferenceManager.getDefaultSharedPreferences(this).edit().putString("language", langCode).apply();

        if (flushDatabase) {
            getSharedPrefsHelper().setLastCheckedDate(0);
            getDbHelper().deleteAll();
        }
    }

    public Locale getLocale() {
        return new Locale(langCode);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("language") && !appSettings.getString(key, "en").equals(langCode)) {  //  if language changed...
            setLocale(sharedPreferences.getString(key, "en"), true);  //  change app language and delete all events (to redownload in new language)
        }
    }

    public SharedPrefsHelper getSharedPrefsHelper() {

        return prefsHelper;
    }

    public boolean rateDialogCondition() {
        boolean isRated = appSettings.getBoolean(PREFERENCE_IS_RATED, false);

        return launchCount != 0 && launchCount % 5 == 0 && !isRated;
    }

    public void setRated(boolean rated) {
        appSettings.edit().putBoolean(PREFERENCE_IS_RATED, rated).apply();
    }

    public boolean isFirstRun() {

        /* We return in a roundabout way, to avoid firstRun sometimes returning
        * true because it's cached but the method gets called again due to
        * activity lifecycle*/
        if (firstRun) {
            firstRun = false;
            return true;
        }

        return false;
    }

    protected Account getAccount() {
        return this.account;
    }

    @Override
    public void onAccountUpdate(Account account) {
        this.account = account;
    }

    @Override
    public void onTerminate() {
        accountManager.removeAccountListener(this);
        super.onTerminate();
    }
}
