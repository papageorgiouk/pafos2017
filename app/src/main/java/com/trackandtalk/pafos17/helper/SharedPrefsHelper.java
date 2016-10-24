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

package com.trackandtalk.pafos17.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.trackandtalk.pafos17.data.model.Account;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
  * Created by Konstantinos Papageorgiou and Charalambos Xinaris
  */
@Singleton
public class SharedPrefsHelper implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static SharedPreferences sharedPreferences;
    private Context context;

    private static final String PAFOS2017_PREFS = "Preferences";
    private static final String LOGGED_IN = "logged_in";

    private Account account;
    private static final String ACCOUNT_PROFILE_NAME = "profile_name";
    private static final String ACCOUNT_PROFILE_EMAIL = "profile_EMAIL";
    private static final String ACCOUNT_PROFILE_PHOTOURL = "photo_url";
    private static final String ACCOUNT_PROFILE_ID = "profile_id";
    private static final String ACCOUNT_PROFILE_ID_TOKEN = "profile_id_token";
    private static final String ACCOUNT_SERVER_AUTH_CODE = "server_auth_code";

    private static final String LAST_CHECKED_DATE = "last_checked";
    private static final String NOTIFICATIONS_BEFORE = "notifications";



    @Inject
    public SharedPrefsHelper(Context context, SharedPreferences defaultSharedPrefs) {
        this.context = context;
        defaultSharedPrefs.registerOnSharedPreferenceChangeListener(this);  //  listen to app settings changes

        sharedPreferences = context.getSharedPreferences(PAFOS2017_PREFS, Context.MODE_PRIVATE);
    }

    public void storeAccount(Account account) {
        this.account = account;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ACCOUNT_PROFILE_NAME, account.getProfileName());
        editor.putString(ACCOUNT_PROFILE_EMAIL, account.getProfileEmail());
        editor.putString(ACCOUNT_PROFILE_PHOTOURL, account.getPhotoUrl());
        editor.putString(ACCOUNT_PROFILE_ID, account.getId());
        editor.putString(ACCOUNT_PROFILE_ID_TOKEN, account.getIdToken());
        editor.putString(ACCOUNT_SERVER_AUTH_CODE, account.getServerAuthCode());
        editor.apply();
    }

    public void deleteAccount() {
        account = null;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(ACCOUNT_PROFILE_NAME);
        editor.remove(ACCOUNT_PROFILE_EMAIL);
        editor.remove(ACCOUNT_PROFILE_PHOTOURL);
        editor.remove(ACCOUNT_PROFILE_ID);
        editor.remove(ACCOUNT_PROFILE_ID_TOKEN);
        editor.remove(ACCOUNT_SERVER_AUTH_CODE);
        editor.apply();
    }

    public Account retrieveAccount() {
        account = new Account.Builder()
                .setName(sharedPreferences.getString(ACCOUNT_PROFILE_NAME, null))
                .setEmail(sharedPreferences.getString(ACCOUNT_PROFILE_EMAIL, null))
                .setProfilePhotoUrl(sharedPreferences.getString(ACCOUNT_PROFILE_PHOTOURL, null))
                .setId(sharedPreferences.getString(ACCOUNT_PROFILE_ID, null))
                .setIdToken(sharedPreferences.getString(ACCOUNT_PROFILE_ID_TOKEN, null))
                .setServerAuthCode(sharedPreferences.getString(ACCOUNT_SERVER_AUTH_CODE, null))
                .build();

        if (account.isNull()) {  //  checks if email present
            return null;  //  essentially no account
        } else {
            return account;
        }
    }

    public Long getLastCheckedDate() {
        return sharedPreferences.getLong(LAST_CHECKED_DATE, 0);
    }

    public void setLastCheckedDate(long unixTime) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(LAST_CHECKED_DATE, unixTime);
        editor.apply();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(NOTIFICATIONS_BEFORE)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, PreferenceManager.getDefaultSharedPreferences(context).getString(key, "1"));  //  I think this is pointless
            editor.apply();
        }
    }
}
