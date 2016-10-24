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

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.content.res.ResourcesCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.trackandtalk.pafos17.R;

/**
 * <p>Image loader for the account header image in the Material Drawer</p>
 *
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

public class MaterialDrawerImageLoader extends AbstractDrawerImageLoader implements DrawerImageLoader.IDrawerImageLoader {

    @Override
    public void set(ImageView imageView, Uri uri, Drawable placeholder) {
        Glide.with(imageView.getContext())
                .load(uri)
                .placeholder(placeholder)
                .into(imageView);
    }

    @Override
    public void cancel(ImageView imageView) {
        super.cancel(imageView);
    }

    @Override
    public Drawable placeholder(Context ctx) {
        return ResourcesCompat.getDrawable(ctx.getResources(), R.drawable.person_image_empty, null);
    }

    @Override
    public Drawable placeholder(Context ctx, String tag) {
        return super.placeholder(ctx, tag);
    }
}
