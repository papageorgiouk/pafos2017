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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.trackandtalk.pafos17.helper.MyDateUtils;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.ZoneId;

import java.util.ArrayList;
import java.util.List;


/**
  * Created by Konstantinos Papageorgiou and Charalambos Xinaris
  */
public class CulturalEvent implements Comparable<CulturalEvent>, Parcelable {

    @SerializedName("event_id") private int eventId;
    @SerializedName("tr_id") private int trId;  //  common in all translations
    @SerializedName("event_title") private String eventTitle;
    @SerializedName("event_subtitle") private String eventSubtitle;
    @SerializedName("event_description") private String eventDescription;
    @SerializedName("date") private List<Long> dates = new ArrayList<>();
    @SerializedName("location") private EventLocation location;
    @SerializedName("creators") private String eventCreators;
    @SerializedName("image") private String imagePath;

    public static String INTENT_EXTRA_NAME = "cultural_event";

    /**
     * <p><strong>DON'T USE THIS, it's only for internal purposes</strong></p>
     *
     * <p>Work with {@link CulturalEvent#trId} and {@link CulturalEvent#getTrId()} instead for everything that requires ID</p>
     *
     */
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int id) {
        this.eventId = id;
    }

    /**
     * @return trId, the common ID for all translations (actually ptrid, our own COMMON id for translations)
     */
    public int getTrId() {
        return trId;
    }

    public void setTrId(int trId) {
        this.trId = trId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public String getEventSubtitle() {
        return eventSubtitle;
    }

    public void setEventSubtitle(String eventSubtitle) {
        this.eventSubtitle = eventSubtitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDescription() {
        return eventDescription;
    }

    public void setEventDescription(String eventDescription) {
        this.eventDescription = eventDescription;
    }

    /**
     * <p>Unixtime, in seconds</p>
     * <p>Start date (dates[0]) and end date (dates[1])</p>
     *
     * @return  a pair of dates
     */
    private List<Long> getDates() {
        return dates;
    }

    public void setDates(List<Long> dates) {
        this.dates = dates;
    }

    /**
     * @return the event's starting time (unixtime, in seconds)
     */
    public long getStartDate() {
        return dates.get(0);
    }

    /**
     * @return  the event's end time if it exists (unixtime, in seconds)
     */
    @Nullable
    public Long getEndDate() {
        if (dates.size() > 1) {  //  end date exists
            return dates.get(1) == 0 ? null : dates.get(1);  //  if endDate == 0, return null as well
        }
     return null;
    }

    public LocalDateTime getBeginDatetime() {
        return Instant.ofEpochSecond(getStartDate())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    @Nullable
    public LocalDateTime getFinishDatetime() {
        if (getEndDate() != null) {
            return Instant.ofEpochSecond(getEndDate())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }

        return null;
    }

    /**
     * Whether the event starts/ends/is ongoing today
     *
     * @return
     */
    public boolean isToday() {
        LocalDate start = getBeginDatetime().toLocalDate();
        LocalDate finish = getFinishDatetime().toLocalDate();

        return MyDateUtils.happensToday(start, finish);
    }

    public EventLocation getLocation() {
        return location;
    }

    public void setLocation(EventLocation location) {
        this.location = location;
    }

    public String getEventCreators() {
        return eventCreators;
    }

    public void setEventCreators(String eventCreators) {
        this.eventCreators = eventCreators;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public CulturalEvent() {}

    public CulturalEvent(@NonNull Integer eventId,
                         Integer trId,
                         String title,
                         String subtitle,
                         String description,
                         long dateStart,
                         long dateEnd,
                         EventLocation location,
                         String creators,
                         String imagePath) {

        this.eventId = eventId;
        this.trId = trId;
        this.eventTitle = title;
        this.eventSubtitle = subtitle;
        this.eventDescription = description;
        this.location = location;
        this.eventCreators = creators;
        this.imagePath = imagePath;

        //  dates
        List<Long> dates = new ArrayList<>();
        dates.add(dateStart);
        dates.add(dateEnd);
        this.dates = dates;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CulturalEvent && ((CulturalEvent) o).getTrId() == (this.getTrId());
    }

    @Override
    public int compareTo(@NonNull CulturalEvent another) {
        return (int) (this.getStartDate() - another.getStartDate());
    }

    @Override
    public int hashCode() {
        return this.eventTitle.hashCode();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.eventId);
        dest.writeInt(this.trId);
        dest.writeString(this.eventTitle);
        dest.writeString(this.eventSubtitle);
        dest.writeString(this.eventDescription);
        dest.writeList(this.dates);
        dest.writeParcelable(this.location, flags);
        dest.writeString(this.eventCreators);
        dest.writeString(this.imagePath);
    }

    protected CulturalEvent(Parcel in) {
        this.eventId = in.readInt();
        this.trId = in.readInt();
        this.eventTitle = in.readString();
        this.eventSubtitle = in.readString();
        this.eventDescription = in.readString();
        this.dates = new ArrayList<Long>();
        in.readList(this.dates, Long.class.getClassLoader());
        this.location = in.readParcelable(EventLocation.class.getClassLoader());
        this.eventCreators = in.readString();
        this.imagePath = in.readString();
    }

    public static final Creator<CulturalEvent> CREATOR = new Creator<CulturalEvent>() {
        @Override
        public CulturalEvent createFromParcel(Parcel source) {
            return new CulturalEvent(source);
        }

        @Override
        public CulturalEvent[] newArray(int size) {
            return new CulturalEvent[size];
        }
    };
}
