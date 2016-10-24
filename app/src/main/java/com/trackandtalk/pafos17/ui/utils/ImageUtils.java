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

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.support.annotation.FloatRange;

/**
  * Created by Konstantinos Papageorgiou and Charalambos Xinaris
  */
public class ImageUtils {

    private static final String TAG = "ImageUtils";

    public static Bitmap getRoundedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                Math.min(bitmap.getWidth(), bitmap.getHeight()) / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return trimAlpha(output);  //  also remove the unnecessary transparency left over
    }

    public static Bitmap trimAlpha(Bitmap bitmap) {
        int minX = bitmap.getWidth();
        int minY = bitmap.getHeight();
        int maxX = -1;
        int maxY = -1;

        for(int y = 0; y < bitmap.getHeight(); y++) {
            for(int x = 0; x < bitmap.getWidth(); x++) {
                int alpha = (bitmap.getPixel(x, y) >> 24) & 255;
                if(alpha > 0) {  // pixel is not 100% transparent
                    if(x < minX)
                        minX = x;
                    if(x > maxX)
                        maxX = x;
                    if(y < minY)
                        minY = y;
                    if(y > maxY)
                        maxY = y;
                }
            }
        }
        if((maxX < minX) || (maxY < minY))
            return null; // Bitmap is entirely transparent

        // crop bitmap to non-transparent area and return:
        return Bitmap.createBitmap(bitmap, minX, minY, (maxX - minX) + 1, (maxY - minY) + 1);
    }

    /**
     * Tint the bitmap with the color and alpha provided
     *
     * @param bitmap the source bitmap
     * @param color  RGB packed int
     * @param alpha  alpha, range is [0,1] (e.g. 0,6)
     * @return  the tinted bitmap
     */
    public static Bitmap tintBitmap(Bitmap bitmap, int color, @FloatRange(from=0, to=1) float alpha) {
        Paint paint = new Paint();
        int colorWithAlpha = applyAlpha(color, alpha);
        paint.setColorFilter(new PorterDuffColorFilter(colorWithAlpha, PorterDuff.Mode.SRC_IN));
        Bitmap result = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(result);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return result;
    }

    /**
     * Apply alpha to a color
     *
     * @param color  the color
     * @param alphaFactor  the factor to multiply the existing alpha, range is [0,1]
     * @return  the alpha'd color
     */
    static int applyAlpha(int color, @FloatRange(from=0, to=1) float alphaFactor) {
        int alpha = Math.round(Color.alpha(color) * alphaFactor);

        return Color.argb(
                alpha,
                Color.red(color),
                Color.green(color),
                Color.blue(color));
    }
}
