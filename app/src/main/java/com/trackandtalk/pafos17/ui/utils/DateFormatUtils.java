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

package com.trackandtalk.pafos17.ui.utils;

import android.support.annotation.NonNull;

import org.threeten.bp.LocalDateTime;
import org.threeten.bp.format.DateTimeFormatter;

import java.util.Locale;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */
public class DateFormatUtils {

    /**
     * <p>Format a datetime in a dd MMM pattern according to current locale</p>
     * <p>E.g: 19 Oct, 02 Jun, 05 Φεβ</p>
     *
     * @param dateTime  datetime to format
     * @param locale  current locale
     * @return  the formatted String
     */
    public static String formatDateShort(@NonNull LocalDateTime dateTime, @NonNull Locale locale) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLL", locale);

        return dateTime.format(formatter);
    }

    /**
     * <p>Format a datetime in an EEEE dd/M HH:mm pattern, using current locale</p>
     * <p>E.g.: Friday 31/12 07:30</p>
     *
     * @param dateTime
     * @param locale  system locale
     * @return  formatted String
     */
    public static String formatDateTimeLong(@NonNull LocalDateTime dateTime, @NonNull Locale locale) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd'/'M HH':'mm ", locale);

        return dateTime.format(formatter);
    }
}
