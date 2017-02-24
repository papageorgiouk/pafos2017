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

package com.trackandtalk.pafos17.ui.main;

import android.support.annotation.Nullable;

import com.trackandtalk.pafos17.data.AccountManager;
import com.trackandtalk.pafos17.data.EventsManager;
import com.trackandtalk.pafos17.data.model.Account;
import com.trackandtalk.pafos17.data.model.CulturalEvent;
import com.trackandtalk.pafos17.ui.base.BasePresenter;

import java.util.List;

import javax.inject.Inject;

/**
 * The presenter for the main activity
 *
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

public class MainPresenter extends BasePresenter<MainActivity> implements AccountManager.AccountListener {

    private EventsManager eventsManager;
    private AccountManager accountManager;
    private Account account;

    //  events
    private List<CulturalEvent> events;
    private boolean fresh = false;

    @Inject
    MainPresenter(EventsManager eventsManager, AccountManager accountManager) {
        this.eventsManager = eventsManager;
        this.accountManager = accountManager;

        if (accountManager.isLoggedIn()) {
            account = accountManager.getAccount();
        }
    }

    @Override
    public void attachView(MainActivity mvpView) {
        super.attachView(mvpView);
        mvpView.showLoading(true);
        accountManager.addAccountListener(this);

        getView().displayProfileInfo(account);

        if (events == null || events.size() == 0) {
            getView().showLoading(true);
            fetchEvents(false);
        } else {
            provideEvents();
        }
    }

    @Override
    public void detachView() {
        super.detachView();
        accountManager.removeAccountListener(this);
    }

    void onRefresh(boolean purge) {
        fetchEvents(purge);
    }

    private void fetchEvents(boolean purge) {
        eventsManager.getNextEvents(purge, new EventsManager.EventsListener() {
            @Override
            public void onFetchEvents(@Nullable List<CulturalEvent> events, boolean fresh) {
                if (events != null && events.size() > 0) {
                    MainPresenter.this.events = events;
                    MainPresenter.this.fresh = fresh;
                    provideEvents();
                } else {  //  still failed
                    onFailure();
                }
            }

            @Override
            public void onFailure() {
                if (isViewAttached()) {
                    getView().displayNoEvents();
                }
            }
        });
    }

    private void provideEvents() {
        if (isViewAttached()) {
            getView().showLoading(false);

            if (events != null && events.size() > 0) {
                getView().displayEvents(events, fresh);
            } else {
                getView().displayNoEvents();
            }
        }
    }

    @Override
    public void onAccountUpdate(Account account) {
        if (isViewAttached()) {
            getView().displayProfileInfo(account);
        }
    }
}
