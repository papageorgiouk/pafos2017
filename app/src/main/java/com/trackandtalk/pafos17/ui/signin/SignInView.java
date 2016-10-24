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

package com.trackandtalk.pafos17.ui.signin;

import com.google.android.gms.common.api.GoogleApiClient;
import com.trackandtalk.pafos17.ui.base.MvpView;

/**
 * The MVP View for Google Sign In authentication
 *
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

public interface SignInView extends MvpView {

    void signIn(GoogleApiClient apiClient);

    void showSuccess();

    void showFailure();

    void showLoading(boolean loading);

    void exit();

}
