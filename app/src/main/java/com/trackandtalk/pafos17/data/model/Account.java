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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
  * Created by Konstantinos Papageorgiou and Charalambos Xinaris
  */
public class Account implements Parcelable {

    private String profileName;
    private String profileEmail;
    private String photoUrl;
    private String id;
    private String idToken;
    private String serverAuthCode;

    public static final String PROFILE_PHOTO_PATH = "profile.jpg";

    //  builder pattern
    private Account(Builder builder) {
        this.profileName = builder.name;
        this.profileEmail = builder.email;
        this.photoUrl = builder.photoUrl;
        this.id = builder.id;
        this.idToken = builder.idToken;
        this.serverAuthCode = builder.serverAuthCode;
    }

    public String getProfileName() {
        return profileName;
    }

    public String getProfileEmail() {
        return profileEmail;
    }


    /** <p><strong>CAUTION!!! DO NOT USE!!!</strong></p>
     * <p>This is only for internal purposes, such as storing the string into SharedPrefs. </p>
     *
     * If you're trying to get a profile photo Bitmap, use {@link #getPhotoUri(Context)} instead.
     * @return the web URL string
     */
    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getId() {
        return id;
    }

    public String getIdToken() {
        return idToken;
    }

    public String getServerAuthCode() {
        return serverAuthCode;
    }


    /**
     * If cached profile photo exists, return the file uri. Else, return the web url
     *
     * @param context ApplicationContext is preferable
     * @return the photo Uri. It might be local or web
     */
    public Uri getPhotoUri(Context context) {
        File file = new File(context.getFilesDir(), Account.PROFILE_PHOTO_PATH);
        if (file.exists()) {
            return Uri.fromFile(file);
        } else {
            if (photoUrl != null) {
                return Uri.parse(photoUrl);
            } else {
                return null;
            }
        }
    }

    public static class Builder {
        private String name = null;
        private String email;
        private String photoUrl = null;
        private String id = null;
        private String idToken = null;
        private String serverAuthCode = null;


        public Builder setName(String name) {
            this.name = name;

            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;

            return this;
        }

        public Builder setProfilePhotoUrl(String url) {
            this.photoUrl = url;

            return this;
        }

        public Builder setId(String id) {
            this.id = id;

            return this;
        }

        public Builder setIdToken(String idToken) {
            this.idToken = idToken;

            return this;
        }

        public Builder setServerAuthCode(String serverAuthCode) {
            this.serverAuthCode = serverAuthCode;

            return this;
        }

        public Account build() {
            return new Account(this);
        }
    }

    public boolean isNull() {
        return this.profileEmail == null || this.id == null;
    }

    public void downloadProfileImage(File filesDirectory) {
        DownloadProfileImageTask task = new DownloadProfileImageTask(filesDirectory);
        task.execute();
    }

    private class DownloadProfileImageTask extends AsyncTask<Void, Void, Void> {

        File filesDirectory;

        DownloadProfileImageTask(File filesDirectory) {
            this.filesDirectory = filesDirectory;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            InputStream inputStream;
            HttpURLConnection connection;
            Bitmap bmp;

            try {
                URL url = new URL(photoUrl);
                connection = (HttpURLConnection)url.openConnection();
                connection.connect();

                //  if HTTP != 200 OK
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    throw new IOException("Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
                }

                //  download the photo
                inputStream = connection.getInputStream();
                bmp = BitmapFactory.decodeStream(inputStream);

                //  save the photo in internal storage
                File file = new File(filesDirectory, PROFILE_PHOTO_PATH);
                FileOutputStream outputStream = null;
                outputStream = new FileOutputStream(file, false);
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    /***************************/
    /**** Parcelable stuff ****/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.profileName);
        dest.writeString(this.profileEmail);
        dest.writeString(this.photoUrl);
        dest.writeString(this.id);
        dest.writeString(this.idToken);
        dest.writeString(this.serverAuthCode);
    }

    protected Account(Parcel in) {
        this.profileName = in.readString();
        this.profileEmail = in.readString();
        this.photoUrl = in.readString();
        this.id = in.readString();
        this.idToken = in.readString();
        this.serverAuthCode = in.readString();
    }

    public static final Creator<Account> CREATOR = new Creator<Account>() {
        @Override
        public Account createFromParcel(Parcel source) {
            return new Account(source);
        }

        @Override
        public Account[] newArray(int size) {
            return new Account[size];
        }
    };
}
