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

package com.trackandtalk.pafos17.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.trackandtalk.pafos17.data.model.Landmark;
import com.trackandtalk.pafos17.slideshow.LandmarkImageFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */
public class LandmarkImagesPagerAdapter extends FragmentStatePagerAdapter{
    private List<String> imageUrls = new ArrayList<>();
    private int primaryItem;
    private OnImageClickListener mListener;

    public LandmarkImagesPagerAdapter(FragmentManager fm, Landmark landmark, int primaryItem) {
        super(fm);
        this.imageUrls = landmark.getImageURLs();
        this.primaryItem = primaryItem;
    }

    @Override
    public Fragment getItem(int position) {
        return LandmarkImageFragment.newInstance(imageUrls.get(position));
    }

    @Override
    public int getCount() {
        return imageUrls.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, primaryItem, object);
    }

    public void setOnImageClickListener(OnImageClickListener listener) {
        this.mListener = listener;

    }

    public interface OnImageClickListener {
        void onClick();
    }
}
