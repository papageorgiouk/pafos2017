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

package com.trackandtalk.pafos17.ui.utils.calendar;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.LineBackgroundSpan;

/**
 * <p>Span to draw a line under a decorated day on a {@link com.prolificinteractive.materialcalendarview.MaterialCalendarView}</p>
 *
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

public class LineSpan implements LineBackgroundSpan {

    private int color;
    private int position;
    private float stroke;

    public LineSpan(int color, int position, float stroke) {
        this.color = color;
        this.position = position;
        this.stroke = stroke;
    }

    @Override
    public void drawBackground(Canvas canvas, Paint paint, int left, int right, int top, int baseline, int bottom, CharSequence charSequence, int start, int end, int lineNum) {

        int startLeft = (left + right) / 5;
        int stopRight = (left + right) * 4 / 5;

        int oldColor = paint.getColor();
        float oldStroke = paint.getStrokeWidth();

        paint.setColor(color);
        paint.setStrokeWidth(stroke);
        canvas.drawLine(startLeft, bottom + position, stopRight, bottom + position, paint);

        //  restore
        paint.setColor(oldColor);
        paint.setStrokeWidth(oldStroke);

    }
}
