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

package com.trackandtalk.pafos17.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;

/**
  * Created by Konstantinos Papageorgiou and Charalambos Xinaris
  */
public class EventLocation implements ClusterItem, Parcelable {
    @SerializedName("location_name") private String locationName;
    @SerializedName("location_lat") private double latitude;
    @SerializedName("location_lng") private double longitude;
//    @SerializedName("location_coordinates") private String locationCoordinatesString;
    @SerializedName("location_address") private String locationAddress;

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public EventLocation() {

    }

    @Override
    public LatLng getPosition() {
        return new LatLng(latitude, longitude);
    }

    @Override
    public int hashCode() {
        String latLng = String.valueOf(latitude) + String.valueOf(longitude);
        return latLng.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof EventLocation
                && ((EventLocation) o).getLatitude() == this.getLatitude()
                && ((EventLocation) o).getLongitude() == this.getLongitude();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.locationName);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.locationAddress);
    }

    protected EventLocation(Parcel in) {
        this.locationName = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.locationAddress = in.readString();
    }

    public static final Creator<EventLocation> CREATOR = new Creator<EventLocation>() {
        @Override
        public EventLocation createFromParcel(Parcel source) {
            return new EventLocation(source);
        }

        @Override
        public EventLocation[] newArray(int size) {
            return new EventLocation[size];
        }
    };
}
