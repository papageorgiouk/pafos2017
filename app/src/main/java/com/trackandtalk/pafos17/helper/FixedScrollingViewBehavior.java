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

package com.trackandtalk.pafos17.helper;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;

/** <p>A class to help buggy scrolling behaviour inside CoordinatorLayout</p>
 *
  * Created by Konstantinos Papageorgiou and Charalambos Xinaris
  */
public class FixedScrollingViewBehavior extends AppBarLayout.ScrollingViewBehavior{

    public FixedScrollingViewBehavior() {
    }

    public FixedScrollingViewBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onMeasureChild(CoordinatorLayout parent, View child, int parentWidthMeasureSpec, int widthUsed, int parentHeightMeasureSpec, int heightUsed) {
        if(child.getLayoutParams().height == -1) {
            List dependencies = parent.getDependencies(child);
            if(dependencies.isEmpty()) {
                return false;
            }

            AppBarLayout appBar = findFirstAppBarLayout(dependencies);
            if(appBar != null && ViewCompat.isLaidOut(appBar)) {
                if(ViewCompat.getFitsSystemWindows(appBar)) {
                    ViewCompat.setFitsSystemWindows(child, true);
                }

                int scrollRange = appBar.getTotalScrollRange();
                int height = parent.getHeight() - appBar.getMeasuredHeight() + Math.min(scrollRange,  parent.getHeight()- heightUsed);
                int heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
                parent.onMeasureChild(child, parentWidthMeasureSpec, widthUsed, heightMeasureSpec, heightUsed);
                return true;
            }
        }

        return false;
    }

    private static AppBarLayout findFirstAppBarLayout(List<View> views) {
        int i = 0;

        for(int z = views.size(); i < z; ++i) {
            View view = (View)views.get(i);
            if(view instanceof AppBarLayout) {
                return (AppBarLayout)view;
            }
        }

        return null;
    }
}
