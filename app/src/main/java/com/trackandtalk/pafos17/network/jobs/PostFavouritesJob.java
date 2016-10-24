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

package com.trackandtalk.pafos17.network.jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;
import com.google.gson.Gson;
import com.trackandtalk.pafos17.data.AccountManager;
import com.trackandtalk.pafos17.network.EndpointProvider;
import com.trackandtalk.pafos17.network.ZoomService;

import java.util.Set;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Response;

/**
 * <p> Job to update a user's favourited events in the remote db.</p>
 * <p> The queue is updated as soon as the user adds or removes a favourite. However,
 * because every time we're sending all the IDs of the favourites, we only need to keep the last job
 * </p>
 *
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

public class PostFavouritesJob extends Job {

    private static final String POST_FAVOURITES_ID = "post_favourites";
    private static final int PRIORITY = 1;

    private Set<Integer> favouriteEventIDs;
    @Inject ZoomService zoomService;
    @Inject EndpointProvider endpointProvider;
    @Inject AccountManager accountManager;

    public PostFavouritesJob(Params params, Set<Integer> favouriteEventIDs) {
        super(new Params(PRIORITY)
                .requireNetwork()
                .setSingleId(POST_FAVOURITES_ID)  //  group by ...
                .singleInstanceBy(POST_FAVOURITES_ID)  //  we're queueing all favourites every time; we only need the last job
                .persist());
        this.favouriteEventIDs = favouriteEventIDs;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        String serialized = serializeFavouriteIDs(favouriteEventIDs);
        String endpoint = endpointProvider.getFavouritesEndpoint();
        String userId = accountManager.getAccount().getId();

        Response<ResponseBody> response = zoomService
                .postFavourites(endpoint, userId, serialized)
                .execute();  //  synchronous
    }

    @Override
    protected void onCancel(int i, @Nullable Throwable throwable) {
        if (throwable != null) {
            Log.d("Job onCancel error: ", throwable.getMessage());
        }
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int i, int i1) {
        return RetryConstraint.createExponentialBackoff(i, 1000);
    }

    /**
     * Serialize the list of IDs for the favourites
     *
     * @param favouriteIDs  the list of event IDs to serialize
     * @return  a JSON String of event IDs
     */
    private String serializeFavouriteIDs(Set<Integer> favouriteIDs) {
        Gson gson = new Gson();
        return gson.toJson(favouriteIDs);
    }
}
