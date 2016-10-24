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

import android.util.Log;

import com.trackandtalk.pafos17.dagger.AppModule;
import com.trackandtalk.pafos17.data.model.Account;
import com.trackandtalk.pafos17.data.db.DbHelper;
import com.trackandtalk.pafos17.helper.SharedPrefsHelper;
import com.trackandtalk.pafos17.network.EndpointProvider;
import com.trackandtalk.pafos17.network.ZoomService;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;

/**
  * <p>Simple class to manage the user account</p>
  *
  * Created by Konstantinos Papageorgiou and Charalambos Xinaris
  */
@Singleton
public class AccountManager {

    private Account account;
    private FavouritesManager favouritesManager;
    private SharedPrefsHelper prefsHelper;
    private List<AccountListener> listeners;
    private ZoomService zoomService;
    private EndpointProvider endpointProvider;
    private DbHelper dbHelper;
    private File filesDirectory;

    @Inject
    public AccountManager(FavouritesManager favouritesManager,
                          SharedPrefsHelper helper,
                          DbHelper dbHelper,
                          ZoomService zoomService,
                          EndpointProvider endpointProvider,
                          @Named(AppModule.FILES_DIRECTORY) File filesDirectory) {

        this.favouritesManager = favouritesManager;
        this.prefsHelper = helper;
        this.account = prefsHelper.retrieveAccount();
        this.dbHelper = dbHelper;
        this.listeners = new ArrayList<>();
        this.zoomService = zoomService;
        this.endpointProvider = endpointProvider;
        this.filesDirectory = filesDirectory;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
        prefsHelper.storeAccount(account);
        postAccount(account);
        account.downloadProfileImage(filesDirectory);

        for (AccountListener listener : listeners) {
            listener.onAccountUpdate(this.account);
        }
    }

    /**
     * <p>Post the account to the remote server</p>
     * <p>If it's a returning user, the callback contains a list of TrIDs for their backed-up favourited events</p>
     *
     * @param account  the account to post
     */
    private void postAccount(Account account) {
        String endPoint = endpointProvider.getLoginAccountEndpoint();
        zoomService.postAccount(endPoint, account)
                .enqueue(new Callback<int[]>() {
                    @Override
                    public void onResponse(Call<int[]> call, retrofit2.Response<int[]> response) {
                        favouritesManager.restoreFavourites(response.body());
                    }

                    @Override
                    public void onFailure(Call<int[]> call, Throwable t) {
                        Log.d("Retrofit postAccount", "FAILURE");
                    }
                });

    }

    public boolean isLoggedIn() {
        return account != null;
    }

    public void logout() {
        this.account = null;

        for (AccountListener listener : listeners) {
            listener.onAccountUpdate(null);
        }
        prefsHelper.deleteAccount();

        dbHelper.deleteFavourites();
    }

    public void addAccountListener(AccountListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeAccountListener(AccountListener listener) {
        listeners.remove(listener);
    }

    public interface AccountListener {

        void onAccountUpdate(Account account);
    }
}
