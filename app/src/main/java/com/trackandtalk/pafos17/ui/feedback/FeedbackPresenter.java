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

import com.trackandtalk.pafos17.data.AccountManager;
import com.trackandtalk.pafos17.network.EndpointProvider;
import com.trackandtalk.pafos17.network.ZoomService;
import com.trackandtalk.pafos17.ui.base.BasePresenter;

import javax.inject.Inject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

public class FeedbackPresenter extends BasePresenter<FeedbackActivity> {

    private AccountManager accountManager;
    private ZoomService zoomService;
    private EndpointProvider endpointProvider;

    @Inject
    public FeedbackPresenter(AccountManager accountManager, ZoomService service, EndpointProvider endpointProvider) {
        this.accountManager = accountManager;
        this.zoomService = service;
        this.endpointProvider = endpointProvider;
    }

    @Override
    public void attachView(FeedbackActivity mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
    }

    /**
     * When the "Send feedback" button has been clicked
     *
     * @param feedback  the feedback from the user
     */
    void onSendButtonClick(String feedback) {
        sendFeedback(feedback);

    }

    /**
     * <p>Send the entered feedback. If user is logged in, send the account Id. Otherwise send "0".</p>
     *
     * <p>Call the View's {@link FeedbackView#onSuccess()} or {@link FeedbackView#onFailure()} methods depending
     * on outcome</p>
     *
     * <p>Doing the network here in the presenter. MVP be damned, no point in creating a FeedbackManager
     * for such a simple task</p>
     *
     * @param feedback  the text entered as feedback
     */
    private void sendFeedback(String feedback) {

        if (isViewAttached()) {
            getView().showSending(true);  //  display progressBar or something else
        }

        String accountId = getAccountId();

        String feedbackEndpoint = endpointProvider.getFeedbackEndpoint();
        zoomService.postFeedback(feedbackEndpoint, accountId, feedback).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (isViewAttached()) {
                    getView().showSending(false);
                    getView().onSuccess();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if (isViewAttached()) {
                    getView().showSending(false);
                    getView().onFailure();
                }
            }
        });
    }

    /**
     * Gets the current user's ID
     *
     * @return user ID if user is logged in, 0 otherwise
     */
    private String getAccountId() {
        return accountManager.getAccount() == null ? "0" : accountManager.getAccount().getId();
    }
}
