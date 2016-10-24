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

import android.content.SharedPreferences;

import com.trackandtalk.pafos17.data.model.Landmark;
import com.trackandtalk.pafos17.data.db.DbHelper;
import com.trackandtalk.pafos17.network.EndpointProvider;
import com.trackandtalk.pafos17.network.ZoomService;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

@Singleton
public class LandmarksManager {

    private DbHelper dbHelper;
    private SharedPreferences defaultSharedPrefs;
    private ZoomService apiService;
    private EndpointProvider endpointProvider;

    //  threading
    private ExecutorService executorService;
    private Future<List<Landmark>> landmarksFuture;

    @Inject
    LandmarksManager(final DbHelper dbHelper, SharedPreferences defaultSharedPrefs, ZoomService apiService, EndpointProvider endpointProvider) {
        this.dbHelper = dbHelper;
        this.defaultSharedPrefs = defaultSharedPrefs;
        this.apiService = apiService;
        this.endpointProvider = endpointProvider;

        executorService = Executors.newSingleThreadExecutor();
        fetchCachedInBackground();
    }

    /**
     * <p>Start fetching cached landmarks from the database in a background thread,
     * in case we need them</p>
     *
     */
    private void fetchCachedInBackground() {
        Callable<List<Landmark>> callable = new Callable<List<Landmark>>() {
            @Override
            public List<Landmark> call() throws Exception {
                return dbHelper.getLandmarks();
            }
        };

        landmarksFuture = executorService.submit(callable);
    }

    public void fetchLandmarks(final LandmarksListener listener) {
        String language = defaultSharedPrefs.getString("language", "en");
        String apiEndpoint = endpointProvider.getLandmarksEndpoint();

        apiService.getLandmarks(apiEndpoint, language).enqueue(new Callback<List<Landmark>>() {
            @Override
            public void onResponse(Call<List<Landmark>> call, Response<List<Landmark>> response) {
                updateDb(response.body());
                listener.onFetched(response.body());
            }

            @Override
            public void onFailure(Call<List<Landmark>> call, Throwable t) {
                try {
                    listener.onFetched(landmarksFuture.get());
                } catch (InterruptedException | ExecutionException e) {
                    listener.onFailure();
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Store fetched landmarks in the database, so we can use them offline
     *
     */
    private void updateDb(final List<Landmark> landmarks) {
        Runnable insert = new Runnable() {
            @Override
            public void run() {
                dbHelper.insertLandmarksToDb(landmarks);
            }
        };

        executorService.submit(insert);
    }

    public interface LandmarksListener {

        void onFetched(List<Landmark> landmarks);

        /**
         * In the unlikely case that both the network AND the db have failed
         */
        void onFailure();

    }
}
