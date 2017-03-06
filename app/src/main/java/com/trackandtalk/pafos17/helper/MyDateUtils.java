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

import org.threeten.bp.LocalDate;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

public class MyDateUtils {

    /**
     * Whether today falls in the range of start-finish
     *
     * @param startDate  starting date
     * @param finishDate  finish date
     * @return true or false
     */
    public static boolean happensToday(LocalDate startDate, LocalDate finishDate) {
        LocalDate today = LocalDate.now();

        return isBetween(startDate, finishDate, today);
    }

    /**
     * Whether a date is between two others
     *
     * @param start  start of range
     * @param end  end of range
     * @param target  the query
     * @return  true or false
     */
    public static boolean isBetween(LocalDate start, LocalDate end, LocalDate target) {
        return !target.isBefore(start) && !target.isAfter(end);
    }
}
