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

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.trackandtalk.pafos17.CulturalCapitalApp;
import com.trackandtalk.pafos17.R;

import javax.inject.Inject;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */
public class FeedbackActivity extends AppCompatActivity implements FeedbackView {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    static final int FINISH_ACTIVITY_DELAY = 3000;

    @Inject FeedbackPresenter presenter;

    private MediaPlayer mediaPlayer;
    private ImageView btnSend;
    private TextInputEditText txtFeedback;

    //  progress indicator
    private FrameLayout progressBackground;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        ((CulturalCapitalApp)getApplicationContext()).getComponent().inject(this);

        Toolbar toolbar = (Toolbar)findViewById(R.id.feedback_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.send_feedback));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBackground = (FrameLayout)findViewById(R.id.progress_background);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);

        setupSound();

        btnSend = (ImageView)findViewById(R.id.btn_send);
        enableDisableSendButton(false, btnSend);

        txtFeedback = (TextInputEditText)findViewById(R.id.feedback_text);
        txtFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 0) {
                    enableDisableSendButton(false, btnSend);
                } else {
                    enableDisableSendButton(true, btnSend);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                mediaPlayer.start();  //  play "send" sound
                presenter.onSendButtonClick(txtFeedback.getText().toString());
                hideKeyboard();

            }
        });

        presenter.attachView(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    private void setupSound() {
        mediaPlayer = MediaPlayer.create(this, R.raw.message_sent);
    }

    /**
     * <p>Enable or disable the "Send" button, depending on whether feedback text has been entered.</p>
     * <p>No point in being enabled if there is no text</p>
     *
     * @param enable enable or disable
     * @param button  the "Send" button
     */
    private void enableDisableSendButton(boolean enable, ImageView button) {
        if (enable) {
            button.setEnabled(true);
            button.setColorFilter(ResourcesCompat.getColor(getResources(), R.color.colorAccent, null));
        } else {
            button.setEnabled(false);
            button.setColorFilter(Color.GRAY);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(btnSend.getWindowToken(), 0);
    }

    private void setProgressVisibility(boolean visible) {
        if (visible) {
            progressBackground.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBackground.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }

    /**** MVP methods ****/

    @Override
    public void showSending(boolean sending) {
        setProgressVisibility(sending);
    }

    @Override
    public void onSuccess() {
        txtFeedback.setText("");  //  clear the input text view

        //  Show "thank you message" and exit after X seconds
        Snackbar.make(btnSend, getString(R.string.feedback_sent), Snackbar.LENGTH_SHORT).show();
        Handler timerHandler = new Handler();
        timerHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, FINISH_ACTIVITY_DELAY);
    }

    @Override
    public void onFailure() {
        Snackbar snackbar = Snackbar.make(btnSend, getString(R.string.internet_problems), Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(FeedbackActivity.this, R.color.error));
        snackbar.show();
    }
}
