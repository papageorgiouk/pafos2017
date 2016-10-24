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

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.trackandtalk.pafos17.data.AccountManager;
import com.trackandtalk.pafos17.data.model.Account;
import com.trackandtalk.pafos17.ui.base.BasePresenter;

import javax.inject.Inject;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

public class SignInPresenter extends BasePresenter<SignInView> {

    private AccountManager accountManager;

    @Inject
    public SignInPresenter(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    void onSignInButtonClicked() {
        if (isViewAttached()) {
            getView().showLoading(true);
        }
    }

    void onResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
//            prefsHelper.setUserLoggedIn(true);
            if (isViewAttached()) {
                getView().showSuccess();
                getView().exit();
            }

            GoogleSignInAccount gAccount = result.getSignInAccount();
            String photoUrl = gAccount.getPhotoUrl() == null ? null : gAccount.getPhotoUrl().toString();

            Account account = new Account.Builder()
                    .setName(gAccount.getDisplayName())
                    .setEmail(gAccount.getEmail())
                    .setProfilePhotoUrl(photoUrl)
                    .setId(gAccount.getId())
                    .setIdToken(gAccount.getIdToken())
                    .setServerAuthCode(gAccount.getServerAuthCode())
                    .build();

            accountManager.setAccount(account);
        } else {
            if (isViewAttached()) {
                getView().showFailure();
            }
        }
    }
}
