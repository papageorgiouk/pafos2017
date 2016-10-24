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

import com.trackandtalk.pafos17.data.model.CulturalEvent;
import com.trackandtalk.pafos17.slideshow.ImageSlideFragment;

import java.util.List;

/**
  * Created by Konstantinos Papageorgiou and Charalambos Xinaris
  */
public class ImageSlidePagerAdapter extends FragmentStatePagerAdapter {
    private List<CulturalEvent> events;
    private OnItemClickedListener mListener;

    public ImageSlidePagerAdapter(FragmentManager fm, List<CulturalEvent> events) {
        super(fm);
        this.events = events;
    }

    @Override
    public Fragment getItem(int position) {

        return ImageSlideFragment.newInstance(events.get(position));
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);

        if (object instanceof ImageSlideFragment) {
            ((ImageSlideFragment) object).setOnSlidingEventListener(new ImageSlideFragment.OnSlidingEventClickedListener() {
                @Override
                public void onSlidingEventClicked(ImageSlideFragment fragment, CulturalEvent event) {
                    if (mListener!= null) {
                        mListener.onItemClicked(fragment, event);
                    }
                }
            });
            ((ImageSlideFragment) object).playAnimation();
        }
    }

    @Override
    public int getCount() {
        return events.size();
    }



    public void setOnItemClickedListener(OnItemClickedListener listener) {
        this.mListener = listener;

    }

    public interface OnItemClickedListener {
        void onItemClicked(ImageSlideFragment fragment, CulturalEvent clickedItem);
    }
}
