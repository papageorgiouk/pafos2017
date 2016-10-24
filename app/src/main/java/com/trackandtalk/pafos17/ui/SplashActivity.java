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

package com.trackandtalk.pafos17.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.trackandtalk.pafos17.CulturalCapitalApp;
import com.trackandtalk.pafos17.ui.intro.IntroActivity;
import com.trackandtalk.pafos17.ui.main.MainActivity;


/**
 * <p>Just a splash activity. No inflating layouts or anything, just the splash.xml as the windowBackground.
 * Its job is to show the splash "background", and then forward you along to the {@link MainActivity}
 * (or the {@link IntroActivity}, if this is the app's first run)</p>
 *
 *
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 *
 */
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (((CulturalCapitalApp)getApplication()).isFirstRun()) {
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }

        ActivityCompat.finishAfterTransition(this);
    }
}
