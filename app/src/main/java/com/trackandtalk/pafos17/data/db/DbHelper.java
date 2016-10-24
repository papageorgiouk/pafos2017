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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.trackandtalk.pafos17.data.model.CulturalEvent;
import com.trackandtalk.pafos17.data.model.EventLocation;
import com.trackandtalk.pafos17.data.model.Landmark;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
  * Created by Konstantinos Papageorgiou and Charalambos Xinaris
  */
@Singleton
public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Pafos2017.db";
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + DbContract.EventEntry.TABLE_NAME
            + " (" + DbContract.EventEntry.COLUMN_EVENT_ID + " INTEGER PRIMARY KEY,"
            + DbContract.EventEntry.COLUMN_EVENT_TRID + " INTEGER,"
            + DbContract.EventEntry.COLUMN_EVENT_TITLE + " TEXT,"
            + DbContract.EventEntry.COLUMN_EVENT_SUBTITLE + " TEXT,"
            + DbContract.EventEntry.COLUMN_EVENT_DESCRIPTION + " TEXT,"
            + DbContract.EventEntry.COLUMN_EVENT_DATETIME_START + " INTEGER,"  //  unix time, seconds
            + DbContract.EventEntry.COLUMN_EVENT_DATETIME_END + " INTEGER,"  //  unix time, seconds
            + DbContract.EventEntry.COLUMN_EVENT_LOCATION_NAME + " TEXT,"
            + DbContract.EventEntry.COLUMN_EVENT_LOCATION_ADDR + " TEXT,"
            + DbContract.EventEntry.COLUMN_EVENT_LOCATION_LAT + " TEXT,"
            + DbContract.EventEntry.COLUMN_EVENT_LOCATION_LNG + " TEXT,"
            + DbContract.EventEntry.COLUMN_EVENT_CREATORS + " TEXT,"
            + DbContract.EventEntry.COLUMN_CREATOR_DESCR + " TEXT,"
            + DbContract.EventEntry.COLUMN_EVENT_IMGPATH + " TEXT"
            + ")";

    private static final String CREATE_FAVOURITES =
            "CREATE TABLE " + DbContract.FavouriteEntry.TABLE_NAME
            + " (" + DbContract.FavouriteEntry.COLUMN_EVENT_TR_ID + " INTEGER UNIQUE,"
            + " FOREIGN KEY(" + DbContract.FavouriteEntry.COLUMN_EVENT_TR_ID + ") REFERENCES " + DbContract.EventEntry.TABLE_NAME + "(" + DbContract.EventEntry.COLUMN_EVENT_TRID + ")"
            + ")";

    private static final String CREATE_LANDMARKS =
            "CREATE TABLE " + DbContract.Landmarks.TABLE_NAME
            + " (" + DbContract.Landmarks.COLUMN_ID + " INTEGER PRIMARY KEY,"
            + DbContract.Landmarks.COLUMN_NAME + " TEXT,"
            + DbContract.Landmarks.COLUMN_DESCRIPTION + " TEXT,"
            + DbContract.Landmarks.COLUMN_ADDRESS + " TEXT,"
            + DbContract.Landmarks.COLUMN_LAT + " TEXT,"
            + DbContract.Landmarks.COLUMN_LNG + " TEXT,"
            + DbContract.Landmarks.COLUMN_CONTACT_INFO + " TEXT,"
            + DbContract.Landmarks.COLUMN_IMAGE_PATH + " TEXT"
            + ")";


    @Inject
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ENTRIES);
        db.execSQL(CREATE_FAVOURITES);
        db.execSQL(CREATE_LANDMARKS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    @WorkerThread
    public synchronized CulturalEvent getEventById(int id) {
        Cursor c = getWritableDatabase().query(DbContract.EventEntry.TABLE_NAME,
                null,
                DbContract.EventEntry.COLUMN_EVENT_TRID + "=" + String.valueOf(id),
                null,
                null,
                null,
                null,
                "1"
                );

        CulturalEvent e = null;
        while (c.moveToNext()) {
            e = getEventFromCursor(c);
            break;  //  we only want one
        }

        c.close();

        return e;
    }

    @WorkerThread
    public synchronized List<CulturalEvent> getEvents() {
        List<CulturalEvent> list = new ArrayList<>();

        Cursor c = getWritableDatabase().query(false, DbContract.EventEntry.TABLE_NAME, null, null, null, null, null, DbContract.EventEntry.COLUMN_EVENT_DATETIME_START + " ASC", null);

        while (c.moveToNext()) {
            CulturalEvent e = getEventFromCursor(c);
            list.add(e);
        }
        c.close();

        return list;
    }

    @WorkerThread
    public synchronized List<CulturalEvent> getNextEvents() {
        List<CulturalEvent> events = new ArrayList<>();
        long currentTime = System.currentTimeMillis() / 1000;  //  current unixtime in seconds

        String query = "SELECT * FROM events"
                + " WHERE " + DbContract.EventEntry.COLUMN_EVENT_DATETIME_START + ">=" + currentTime
                + " OR " + currentTime + " BETWEEN " + DbContract.EventEntry.COLUMN_EVENT_DATETIME_START + " AND " + DbContract.EventEntry.COLUMN_EVENT_DATETIME_END
                + " ORDER BY " + DbContract.EventEntry.COLUMN_EVENT_DATETIME_START + " ASC";

        Cursor c = getWritableDatabase().rawQuery(query, null);

        while (c.moveToNext()) {
            CulturalEvent e = getEventFromCursor(c);
            events.add(e);
        }

        c.close();
        return events;
    }

    @WorkerThread
    public synchronized List<CulturalEvent> getLastXEvents(int number) {
        List<CulturalEvent> events = new ArrayList<>();

        Cursor c = getWritableDatabase().query(DbContract.EventEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                DbContract.EventEntry.COLUMN_EVENT_DATETIME_START + " DESC",
                String.valueOf(number)
                );

        while (c.moveToNext()) {
            CulturalEvent e = getEventFromCursor(c);
            events.add(e);
        }
        c.close();

        return events;
    }

    @WorkerThread
    public void insertEventsToDb(List<CulturalEvent> events) {
        SQLiteDatabase database = getWritableDatabase();
        for (CulturalEvent event : events) {
            ContentValues values = valuesFromEvent(event);

            database.insertWithOnConflict(DbContract.EventEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    /**
     * In cases like language change etc etc
     */
    public void deleteAll() {
        getWritableDatabase().delete(DbContract.EventEntry.TABLE_NAME, null, null);
    }

    public void deleteFavourites() {
        getWritableDatabase().delete(DbContract.FavouriteEntry.TABLE_NAME, null, null);
    }

    private static ContentValues valuesFromEvent(CulturalEvent event) {

        ContentValues values = new ContentValues();
        values.put(DbContract.EventEntry.COLUMN_EVENT_ID, event.getEventId());
        values.put(DbContract.EventEntry.COLUMN_EVENT_TRID, event.getTrId());
        values.put(DbContract.EventEntry.COLUMN_EVENT_TITLE, event.getEventTitle());
        values.put(DbContract.EventEntry.COLUMN_EVENT_SUBTITLE, event.getEventSubtitle());
        values.put(DbContract.EventEntry.COLUMN_EVENT_DESCRIPTION, event.getEventDescription());
        values.put(DbContract.EventEntry.COLUMN_EVENT_DATETIME_START, event.getStartDate());
        values.put(DbContract.EventEntry.COLUMN_EVENT_DATETIME_END, event.getEndDate());
        values.put(DbContract.EventEntry.COLUMN_EVENT_LOCATION_NAME, event.getLocation().getLocationName());
        values.put(DbContract.EventEntry.COLUMN_EVENT_LOCATION_ADDR, event.getLocation().getLocationAddress());
        values.put(DbContract.EventEntry.COLUMN_EVENT_LOCATION_LAT, event.getLocation().getLatitude());
        values.put(DbContract.EventEntry.COLUMN_EVENT_LOCATION_LNG, event.getLocation().getLongitude());
        values.put(DbContract.EventEntry.COLUMN_EVENT_CREATORS, event.getEventCreators());
        values.put(DbContract.EventEntry.COLUMN_EVENT_IMGPATH, event.getImagePath());

        return values;
    }

    private static ContentValues valuesFromLandmark(Landmark landmark) {
        ContentValues values = new ContentValues();
        values.put(DbContract.Landmarks.COLUMN_ID, landmark.getId());
        values.put(DbContract.Landmarks.COLUMN_NAME, landmark.getName());
        values.put(DbContract.Landmarks.COLUMN_DESCRIPTION, landmark.getDescription());
        values.put(DbContract.Landmarks.COLUMN_ADDRESS, landmark.getAddress());
        values.put(DbContract.Landmarks.COLUMN_LAT, landmark.getLatitude());
        values.put(DbContract.Landmarks.COLUMN_LNG, landmark.getLongitude());
        values.put(DbContract.Landmarks.COLUMN_CONTACT_INFO, landmark.getContactInfo());
        values.put(DbContract.Landmarks.COLUMN_IMAGE_PATH, landmark.getImgPaths());

        return values;
    }

    public void addEventToFavourites(int trId) {
        final String query = "INSERT INTO "
                + DbContract.FavouriteEntry.TABLE_NAME + "(" + DbContract.FavouriteEntry.COLUMN_EVENT_TR_ID + ") "
                + "VALUES (" + String.valueOf(trId) + ")";

        try {
            getWritableDatabase().execSQL(query);
        } catch (SQLiteConstraintException e) {
            Log.d("DbHelper: ", "Event already exists in favourites");
            Log.d("DbHelper: ", e.getMessage());
        }
    }

    public  void removeEventFromFavourites(int id) {
        final String query = "DELETE FROM " + DbContract.FavouriteEntry.TABLE_NAME
                + " WHERE " + DbContract.FavouriteEntry.COLUMN_EVENT_TR_ID + " = " + id;

        getWritableDatabase().execSQL(query);
    }

    public boolean isEventFavourite(int trId) {
        final String query = "SELECT * FROM "
                + DbContract.FavouriteEntry.TABLE_NAME
                + " WHERE " + DbContract.FavouriteEntry.COLUMN_EVENT_TR_ID + "=" + String.valueOf(trId)
                + " LIMIT 1";
        Cursor c = getWritableDatabase().rawQuery(query, null);

        boolean exists = c.moveToFirst(); //  true if "first" exists - less expensive than getCount()
        c.close();

        return exists;
    }

    @WorkerThread
    public List<CulturalEvent> getFavourites() {
        List<CulturalEvent> list = new ArrayList<>();

        String query = "SELECT * FROM " + DbContract.FavouriteEntry.TABLE_NAME
                + " JOIN " + DbContract.EventEntry.TABLE_NAME
                + " WHERE " + DbContract.FavouriteEntry.COLUMN_EVENT_TR_ID + " = " + DbContract.EventEntry.COLUMN_EVENT_TRID
                + " ORDER BY " + DbContract.EventEntry.COLUMN_EVENT_DATETIME_START + " ASC";

        Cursor c = getWritableDatabase().rawQuery(query, null);

        while (c.moveToNext()) {
            try {
                CulturalEvent e = getEventFromCursor(c);
                list.add(e);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
        c.close();

        return list;
    }

    @WorkerThread
    public List<Landmark> getLandmarks() {
        List<Landmark> list = new ArrayList<>();

        Cursor c = getWritableDatabase().query(DbContract.Landmarks.TABLE_NAME, null, null, null, null, null, null);
        while (c.moveToNext()) {
            Landmark l = getLandmarkFromCursor(c);
            list.add(l);
        }

        c.close();

        return list;
    }

    @WorkerThread
    public void insertLandmarksToDb(List<Landmark> landmarks) {
        SQLiteDatabase database = getWritableDatabase();
        for (Landmark landmark : landmarks) {
            ContentValues values = valuesFromLandmark(landmark);

            database.insertWithOnConflict(DbContract.Landmarks.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        }
    }

    @WorkerThread
    public Set<Integer> getFavouriteIds() {
        Set<Integer> set = new HashSet<>();

        Cursor c = getWritableDatabase().query(DbContract.FavouriteEntry.TABLE_NAME, null, null, null, null, null, null);
        while (c.moveToNext()) {
            try {
                set.add(c.getInt(c.getColumnIndex(DbContract.FavouriteEntry.COLUMN_EVENT_TR_ID)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        c.close();

        return set;
    }

    public List<CulturalEvent> getEventsAtLocation(double latitude, double longitude) {
        List<CulturalEvent> events = new ArrayList<>();

        String query = "SELECT * FROM " + DbContract.EventEntry.TABLE_NAME
                + " WHERE " + DbContract.EventEntry.COLUMN_EVENT_LOCATION_LAT + "=" + String.valueOf(latitude) + ","
                +" AND " + DbContract.EventEntry.COLUMN_EVENT_LOCATION_LNG + "=" + String.valueOf(longitude);

        Cursor c = getWritableDatabase().rawQuery(query, null);

        while (c.moveToNext()) {
            CulturalEvent event = getEventFromCursor(c);
            events.add(event);
        }
        c.close();

        return events;
    }

    /**
     * Helper method to convert sql cursor line to event object
     *
     * @param c the cursor with the event data
     * @return a CulturalEvent object
     */
    /**/
    private static CulturalEvent getEventFromCursor(Cursor c) {
        //  Get the location first
        EventLocation loc = new EventLocation();
        loc.setLocationName(c.getString(c.getColumnIndex(DbContract.EventEntry.COLUMN_EVENT_LOCATION_NAME)));
        loc.setLocationAddress(c.getString(c.getColumnIndex(DbContract.EventEntry.COLUMN_EVENT_LOCATION_ADDR)));
        loc.setLatitude(c.getDouble(c.getColumnIndex(DbContract.EventEntry.COLUMN_EVENT_LOCATION_LAT)));
        loc.setLongitude(c.getDouble(c.getColumnIndex(DbContract.EventEntry.COLUMN_EVENT_LOCATION_LNG)));


        CulturalEvent e = new CulturalEvent(c.getInt(c.getColumnIndex(DbContract.EventEntry.COLUMN_EVENT_ID)),
                c.getInt(c.getColumnIndex(DbContract.EventEntry.COLUMN_EVENT_TRID)),
                c.getString(c.getColumnIndex(DbContract.EventEntry.COLUMN_EVENT_TITLE)),
                c.getString(c.getColumnIndex(DbContract.EventEntry.COLUMN_EVENT_SUBTITLE)),
                c.getString(c.getColumnIndex(DbContract.EventEntry.COLUMN_EVENT_DESCRIPTION)),
                c.getLong(c.getColumnIndex(DbContract.EventEntry.COLUMN_EVENT_DATETIME_START)),
                c.getLong(c.getColumnIndex(DbContract.EventEntry.COLUMN_EVENT_DATETIME_END)),
                loc,
                c.getString(c.getColumnIndex(DbContract.EventEntry.COLUMN_EVENT_CREATORS)),
                c.getString(c.getColumnIndex(DbContract.EventEntry.COLUMN_EVENT_IMGPATH))
        );

        return e;
    }

    private static Landmark getLandmarkFromCursor(Cursor c) {
        Landmark landmark = new Landmark.Builder()
                .setId(c.getInt(c.getColumnIndex(DbContract.Landmarks.COLUMN_ID)))
                .setName(c.getString(c.getColumnIndex(DbContract.Landmarks.COLUMN_NAME)))
                .setAddress(c.getString(c.getColumnIndex(DbContract.Landmarks.COLUMN_ADDRESS)))
                .setDescription(c.getString(c.getColumnIndex(DbContract.Landmarks.COLUMN_DESCRIPTION)))
                .setLatitude(c.getDouble(c.getColumnIndex(DbContract.Landmarks.COLUMN_LAT)))
                .setLongitude(c.getDouble(c.getColumnIndex(DbContract.Landmarks.COLUMN_LNG)))
                .setContactInfo(c.getString(c.getColumnIndex(DbContract.Landmarks.COLUMN_CONTACT_INFO)))
                .setImagePaths(c.getString(c.getColumnIndex(DbContract.Landmarks.COLUMN_IMAGE_PATH)))
                .build();

        return landmark;
    }
}
