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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.trackandtalk.pafos17.CulturalCapitalApp;
import com.trackandtalk.pafos17.R;

import javax.inject.Inject;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */
public class GoogleSignInActivity extends AppCompatActivity implements SignInView, GoogleApiClient.OnConnectionFailedListener{


    @Inject GoogleApiClient.Builder mGoogleApiClientBuilder;
    @Inject GoogleSignInOptions gso;
    @Inject SignInPresenter presenter;

    private SignInButton googleButton;
    private TextView errorSuccess;
    private ProgressBar progressBar;

    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_google_sign_in);
        ((CulturalCapitalApp)getApplicationContext()).getComponent().inject(this);
        presenter.attachView(this);

        googleButton = (SignInButton)findViewById(R.id.google_signin_button);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);
        errorSuccess = (TextView)findViewById(R.id.error_success);

        final GoogleApiClient googleApiClient = mGoogleApiClientBuilder
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        googleButton.setScopes(gso.getScopeArray());
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onSignInButtonClicked();
                signIn(googleApiClient);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            presenter.onResult(result);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        showLoading(false);
        errorSuccess.setText(R.string.connection_failed);
        errorSuccess.setTextColor(ContextCompat.getColor(this, R.color.error));
        errorSuccess.setVisibility(View.VISIBLE);

    }

    /*** MVP View methods ***/

    @Override
    public void signIn(GoogleApiClient apiClient) {
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(apiClient);
        startActivityForResult(intent, RC_SIGN_IN);
    }


    @Override
    public void showSuccess() {
        googleButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        errorSuccess.setText(R.string.sign_in_success);
        errorSuccess.setTextColor(ContextCompat.getColor(this, R.color.success));
        errorSuccess.setVisibility(View.VISIBLE);
    }

    @Override
    public void showFailure() {
        progressBar.setVisibility(View.GONE);

        errorSuccess.setText(R.string.sign_in_fail);
        errorSuccess.setTextColor(ContextCompat.getColor(this, R.color.error));
        errorSuccess.setVisibility(View.VISIBLE);
    }

    @Override
    public void showLoading(boolean loading) {
        if (loading) {
            googleButton.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            googleButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }
    }

    /**
     * Delay for 3 seconds and exit the activity
     *
     */
    @Override
    public void exit() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ActivityCompat.finishAfterTransition(GoogleSignInActivity.this);
            }
        };
        final Handler  handler = new Handler();
        handler.postDelayed(runnable, 3000);
    }
}
