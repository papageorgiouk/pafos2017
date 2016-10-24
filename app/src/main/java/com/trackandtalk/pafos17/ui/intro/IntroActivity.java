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

package com.trackandtalk.pafos17.ui.intro;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.WindowManager;

import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntro2Fragment;
import com.trackandtalk.pafos17.CulturalCapitalApp;
import com.trackandtalk.pafos17.R;
import com.trackandtalk.pafos17.ui.main.MainActivity;

import java.util.Locale;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */
public class IntroActivity extends AppIntro2 implements IntroLanguageFragment.OnLanguageSelectListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        skipButtonEnabled = false;
        setGoBackLock(true);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        addLanguageFragment();
        addRemainingFragments();
    }

    private void addLanguageFragment() {
        Fragment languageFragment = IntroLanguageFragment.newInstance(ResourcesCompat.getColor(getResources(), R.color.intro_blue, null));
        addSlide(languageFragment);

    }

    private void addRemainingFragments() {
        Fragment video = IntroVideoFragment.newInstance(ResourcesCompat.getColor(getResources(), android.R.color.black, null));
        addSlide(video);

        Fragment welcome = AppIntro2Fragment.newInstance(
                getString(R.string.welcome),
                getString(R.string.stay_updated),
                R.drawable.screenshot_main,
                ResourcesCompat.getColor(getResources(), R.color.intro_dark_blue, null));
        addSlide(welcome);

        Fragment addEvent = IntroAddEventFragment.newInstance(ResourcesCompat.getColor(getResources(), R.color.intro_light_blue, null));
        addSlide(addEvent);

        Fragment explore = AppIntro2Fragment.newInstance(
                getString(R.string.explore_intro),
                getString(R.string.discover_pafos),
                R.drawable.screenshot_map,
                ResourcesCompat.getColor(getResources(), R.color.intro_muted_blue, null));
        addSlide(explore);

        addSlide(AppIntro2Fragment.newInstance(
                getString(R.string.social_intro),
                getString(R.string.social_description),
                R.drawable.screenshot_social,
                ResourcesCompat.getColor(getResources(), R.color.intro_green_blue, null)
        ));
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);

//        if (oldFragment instanceof IntroLanguageFragment) {
//            String langCode = ((IntroLanguageFragment)oldFragment).getSelectedLocale().getLanguage();
//
//
//
//            for (Fragment fragment : getSupportFragmentManager().getFragments()) {
//                if (!(fragment instanceof IntroLanguageFragment)) {
//                    getSupportFragmentManager().beginTransaction().remove(fragment).attach(fragment).commit();
//                }
//            }
//        }
    }

    @Override
    public void onLanguageChange(String langCode, Locale locale) {
        ((CulturalCapitalApp)getApplication()).setLocale(langCode, true);
        recreate();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

        ActivityCompat.finishAfterTransition(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
