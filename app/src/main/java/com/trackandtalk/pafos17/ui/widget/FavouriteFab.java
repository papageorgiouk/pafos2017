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

package com.trackandtalk.pafos17.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Animatable;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.util.AttributeSet;

import com.trackandtalk.pafos17.R;

/**
 * <p>A FAB specifically made to indicate whether an event is a favourite or not
 * Checks if the SDK >= 21 and uses AnimatedDrawables for nice path transitions, else
 * it uses plain old VectorDrawableCompat.</p>
 *
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

public class FavouriteFab extends FloatingActionButton {

    //  SDK >= 21
    private AnimatedVectorDrawableCompat animatedCheck;
    private AnimatedVectorDrawableCompat animatedPlus;
    //  SDK < 21
    private VectorDrawableCompat simpleCheck;
    private VectorDrawableCompat simplePlus;

    public FavouriteFab(Context context) {
        super(context);
        init();
    }

    public FavouriteFab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FavouriteFab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * <p>Initialize vector drawables</p>
     * <p>If SDK > 21, create vector drawables containing path morph animations.
     * Else, create vectors from simple shapes</p>
     */
    private void init() {
        if (isAnimated()) {
            initAnimatedVectors();
        } else {
            initSimpleVectors();
        }
    }

    @TargetApi(21)
    private void initAnimatedVectors() {
        animatedCheck =  AnimatedVectorDrawableCompat.create(getContext(), R.drawable.animated_check);
        animatedPlus = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.animated_plus);
    }

    private void initSimpleVectors() {
        simpleCheck = VectorDrawableCompat.create(getResources(), R.drawable.simple_check, null);
        simplePlus = VectorDrawableCompat.create(getResources(), R.drawable.plus, null);
    }

    /**
     * Transition to and from "favourite" status.
     * Considers the SDK version and does nice transitions if possible
     *
     * @param isFavourite if the event has been added to the "Favourites"
     */
    public void setFavourite(boolean isFavourite) {
        if (isFavourite) {
            if (isAnimated()) {
                setImageDrawable(animatedCheck);
                ((Animatable)getDrawable()).start();
            } else {
                setImageDrawable(simpleCheck);
            }
        } else {
            if (isAnimated()) {
                setImageDrawable(animatedPlus);
                ((Animatable)getDrawable()).start();
            } else {
                setImageDrawable(simplePlus);
            }
        }
    }

    private boolean isAnimated() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }
}
