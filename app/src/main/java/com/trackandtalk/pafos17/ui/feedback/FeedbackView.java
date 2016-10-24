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

package com.trackandtalk.pafos17.ui.feedback;

import com.trackandtalk.pafos17.ui.base.MvpView;

/**
 * View for sending
 *
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

public interface FeedbackView extends MvpView {

    /**
     * For while the REST call is being made
     *
     * @param sending true if ongoing, false if finished
     */
    void showSending(boolean sending);

    /**
     * Feedback has been sent successfully
     */
    void onSuccess();

    /**
     * Feedback sending has failed
     */
    void onFailure();
}
