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

import java.util.Arrays;
import java.util.List;

/**
  * Created by Konstantinos Papageorgiou and Charalambos Xinaris
  */
public class Landmark implements Parcelable{
    @SerializedName("ID") private int id;
    @SerializedName("name") private String name;
    @SerializedName("description") private String description;
    @SerializedName("address") private String address;
    private double latitude;
    private double longitude;
    @SerializedName("contact_info") private String contactInfo;
    @SerializedName("images") private String imgPaths;

    private Landmark(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.address = builder.address;
        this.latitude = builder.latitude;
        this.longitude = builder.longitude;
        this.contactInfo = builder.contactInfo;
        this.imgPaths = builder.imgPaths;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public LatLng getPosition() {
        if (latitude != 0 && longitude != 0) {
            return new LatLng(latitude, longitude);
        } else {
            return null;
        }
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getImgPaths() {
        return imgPaths;
    }

    public void setImgPaths(String imgPaths) {
        this.imgPaths = imgPaths;
    }

    public List<String> getImageURLs() {
        if (imgPaths != null) {
            String[] parts = imgPaths.split(";");
            return Arrays.asList(parts);
        } else return null;
    }

    public static class Builder {
        private int id;
        private String name = null;
        private String description = null;
        private String address = null;
        private double latitude;
        private double longitude;
        private String contactInfo = null;
        private String imgPaths = null;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setAddress(String address) {
            this.address = address;
            return this;
        }

        public Builder setLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public Builder setLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public Builder setContactInfo(String contactInfo) {
            this.contactInfo = contactInfo;
            return this;
        }

        public Builder setImagePaths(String imagePaths) {
            this.imgPaths = imagePaths;
            return this;
        }

        public Landmark build() {
            return new Landmark(this);
        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeString(this.address);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeString(this.contactInfo);
        dest.writeString(this.imgPaths);
    }

    protected Landmark(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.address = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.contactInfo = in.readString();
        this.imgPaths = in.readString();
    }

    public static final Creator<Landmark> CREATOR = new Creator<Landmark>() {
        @Override
        public Landmark createFromParcel(Parcel source) {
            return new Landmark(source);
        }

        @Override
        public Landmark[] newArray(int size) {
            return new Landmark[size];
        }
    };
}
