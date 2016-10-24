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

package com.trackandtalk.pafos17.data.db;

import android.provider.BaseColumns;

/**
 * <p>The database contract for easy reference to tables and columns</p>
 *
  * Created by Konstantinos Papageorgiou and Charalambos Xinaris
  */
public final class DbContract {

    public DbContract() {
        //  empty because we don't want instantiation
    }

    public static abstract class EventEntry implements BaseColumns {
        public static final String TABLE_NAME = "events";
        public static final String COLUMN_EVENT_ID = "eventid";
        public static final String COLUMN_EVENT_TRID = "eventtrid";
        public static final String COLUMN_EVENT_TITLE = "eventtitle";
        public static final String COLUMN_EVENT_SUBTITLE = "eventsubtitle";
        public static final String COLUMN_EVENT_DESCRIPTION = "eventdescription";
        public static final String COLUMN_EVENT_DATETIME_START = "eventdatetimestart";
        public static final String COLUMN_EVENT_DATETIME_END = "eventdatetimeend";
        public static final String COLUMN_EVENT_LOCATION_NAME = "locationname";
        public static final String COLUMN_EVENT_LOCATION_LAT = "locationlat";
        public static final String COLUMN_EVENT_LOCATION_LNG = "locationlng";
        public static final String COLUMN_EVENT_LOCATION_ADDR = "locationaddr";
        public static final String COLUMN_EVENT_CREATORS = "eventcreators";
        public static final String COLUMN_CREATOR_DESCR = "creatordescr";
        public static final String COLUMN_EVENT_IMGPATH = "eventimgpath";
    }

    public static abstract class FavouriteEntry implements BaseColumns {
        public static final String TABLE_NAME = "favourites";
        public static final String COLUMN_EVENT_ID = "id";
        public static final String COLUMN_EVENT_TR_ID = "trid";
    }

    public static abstract class Landmarks implements BaseColumns {
        public static final String TABLE_NAME = "landmarks";
        public static final String COLUMN_ID = "ID";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_ADDRESS = "address";
        public static final String COLUMN_LAT = "latitude";
        public static final String COLUMN_LNG = "longitude";
        public static final String COLUMN_CONTACT_INFO = "contactinfo";
        public static final String COLUMN_IMAGE_PATH = "imgpath";
    }
}
