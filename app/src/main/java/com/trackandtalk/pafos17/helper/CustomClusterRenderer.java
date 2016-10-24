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
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.trackandtalk.pafos17.R;

/**
  * Created by Konstantinos Papageorgiou and Charalambos Xinaris
  */
public class CustomClusterRenderer<T extends ClusterItem> extends DefaultClusterRenderer {
    private Context mContext;
    public CustomClusterRenderer(Context context, GoogleMap map, ClusterManager clusterManager) {
        super(context, map, clusterManager);

        mContext = context;
    }

    @Override  //  for custom marker icon
    protected void onClusterItemRendered(ClusterItem clusterItem, Marker marker) {
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        super.onClusterItemRendered(clusterItem, marker);

    }

    @Override
    protected int getColor(int clusterSize) {
        int color = mContext.getResources().getColor(R.color.colorAccent);

        //  convert hex to rgb
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);


        return Color.rgb(r, g, b);
    }

    @Override
    protected void onBeforeClusterRendered(Cluster cluster, MarkerOptions markerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {

        return cluster.getSize() > 1;
    }
}
