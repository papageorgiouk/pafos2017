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

package com.trackandtalk.pafos17.ui.eventdetails;

import android.text.Html;
import android.text.Spanned;

import com.trackandtalk.pafos17.data.AccountManager;
import com.trackandtalk.pafos17.data.FavouritesManager;
import com.trackandtalk.pafos17.data.model.CulturalEvent;
import com.trackandtalk.pafos17.network.EndpointProvider;
import com.trackandtalk.pafos17.ui.base.BasePresenter;

import java.util.Date;

import javax.inject.Inject;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

class EventPresenter extends BasePresenter<EventActivity> {

    private CulturalEvent event;
    private FavouritesManager favouritesManager;
    private AccountManager accountManager;
    private EndpointProvider endpointProvider;

    @Inject
    EventPresenter(FavouritesManager favouritesManager, AccountManager accountManager, EndpointProvider endpointProvider) {
        this.favouritesManager = favouritesManager;
        this.accountManager = accountManager;
        this.endpointProvider = endpointProvider;
    }

    @Override
    public void attachView(EventActivity mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    void onMapReady() {
        final double eventLat = event.getLocation().getLatitude();
        final double eventLong = event.getLocation().getLongitude();
        final String locationName = event.getLocation().getLocationName();

        if (isViewAttached()) {
            getView().showMap(eventLat, eventLong, locationName);
        }
    }

    /**
     * Set event to present. Updates the view as soon as it's set
     *
     * @param event
     */
    void setEvent(CulturalEvent event) {
        this.event = event;

        if (isViewAttached()) {
            getView().showTitle(getTitle(event));
            getView().showDate(event.getBeginDatetime(), event.getFinishDatetime());
            getView().showTitle(getTitle(event));
            getView().showDescription(getDescription(event));

            //  image
            String imagePath = endpointProvider.getFullImagesUrl() + event.getImagePath();
            getView().showImage(imagePath);

            boolean isFavourite = favouritesManager.isFavourited(event);
            getView().showFavouriteStatus(isFavourite);

            if (event.getEventCreators() != null) {
                getView().showCreators(getCreators(event));
            }

            //  today indicator
            getView().showIsToday(event.isToday());
        }
    }

    private Date getStartDate(CulturalEvent event) {
        return new Date(event.getStartDate() * 1000);  //  milliseconds
    }

    private Spanned getTitle(CulturalEvent event) {
        return Html.fromHtml(event.getEventTitle());
    }

    private Spanned getDescription(CulturalEvent event) {
        return Html.fromHtml(event.getEventDescription());
    }

    private Spanned getCreators(CulturalEvent event) {
        return Html.fromHtml(event.getEventCreators());
    }

    /**
     * On "add/remove favourite" toggle. Checks if the event is already favourited and removes it,
     * and vice-versa.
     *
     */
    void onAddRemoveButtonClicked() {

        if (accountManager.isLoggedIn()) {
            favouritesManager.addRemoveFavouriteInteraction(event, new FavouritesManager.FavouritesInteractionListener() {
                @Override
                public void onAdded() {
                    if (isViewAttached()){
                        getView().showFavouriteStatus(true);  //  transition to favourite
                        getView().notifyFavouriteStatus(true);
                    }
                }

                @Override
                public void onRemoved() {
                    if (isViewAttached()) {
                        getView().showFavouriteStatus(false);  //  transition to "not a favourite"
                        getView().notifyFavouriteStatus(false);
                    }
                }
            });
        } else {  //  not logged in
            getView().startLoginActivity();
        }
    }
}
