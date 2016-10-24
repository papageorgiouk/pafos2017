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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.trackandtalk.pafos17.data.model.Account;
import com.trackandtalk.pafos17.data.model.CulturalEvent;
import com.trackandtalk.pafos17.ui.base.MvpView;

import java.util.List;


/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

public interface MainView extends MvpView {

    /**
     * Indicate that we're loading data.
     *
     * @param loading true if data is still being loaded, false if we're done
     */
    void showLoading(boolean loading);

    /**
     * Display the events fetched
     *
     * @param events  list of events to display
     * @param fresh  true if retrieved online, false if from offline cache
     */
    void displayEvents(@NonNull List<CulturalEvent> events, boolean fresh);

    /**
     * When there are no events to display
     */
    void displayNoEvents();

    void showNetworkError();

    /**
     * Display the profile info
     *
     * @param account contains the profile info
     */
    void displayProfileInfo(@Nullable Account account);

}
