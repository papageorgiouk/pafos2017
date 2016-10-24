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


import com.trackandtalk.pafos17.data.model.Account;
import com.trackandtalk.pafos17.data.model.CulturalEvent;
import com.trackandtalk.pafos17.data.model.Landmark;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
  * <p>Interface required by Retrofit</p>
  *
  * Created by Konstantinos Papageorgiou and Charalambos Xinaris
  */
public interface ZoomService {

    @FormUrlEncoded
    @POST("{path}")
    Call<List<CulturalEvent>> getEvents(@Path("path") String eventsEndpoint, @Field("date") String date, @Field("lang") String language);

    @POST("{path}")
    Call<int[]> postAccount(@Path("path") String loginEndpoint, @Body Account jayson);

    @FormUrlEncoded
    @POST("{path}")
    Call<ResponseBody> postFavourites(@Path("path") String postFavsEndpoint, @Field("user_id") String userId, @Field("favorites") String favourites);

    @FormUrlEncoded
    @POST("{path}")
    Call<ResponseBody> postFeedback(@Path("path") String feedbackEndpoint, @Field("user_id") String userId, @Field("text") String feedback);

    @FormUrlEncoded
    @POST("{path}")
    Call<List<Landmark>> getLandmarks(@Path("path") String landmarksEndpoint, @Field("lang") String language);

    class Creator {

        public static ZoomService newZoomService(String baseUrl) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            return retrofit.create(ZoomService.class);
        }
    }
}
