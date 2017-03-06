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

package com.trackandtalk.pafos17.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.trackandtalk.pafos17.R;
import com.trackandtalk.pafos17.helper.StringUtils;

/**
 * <p>Custom view that indicates whether an event happens today</p>
 *
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */

public class IndicatorView extends LinearLayout {

    private Animation anim;
    private ImageView imgDot;

    //  attrs
    private int bgColor;
    private int textSize;
    private float scaleTo;
    private float cornerRadius;
    private long animDuration;  //  milliseconds

    //  def values
    private static final int DEFAULT_PADDING_TOP_BOTTOM = 2;  //  dp
    private static final int DEFAULT_PADDING_LEFT_RIGHT = 4;  //  dp
    private static final String DEFAULT_COLOR = "#43A047";
    private static final int DEFAULT_DURATION = 1000;
    private static final float DEFAULT_SCALE_TO = 0.4F;
    private static final int DEFAULT_TEXT_SIZE = 8;  //  sp

    public IndicatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public IndicatorView(Context context) {
        super(context);
        init(null);
    }

    public IndicatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public void init(@Nullable AttributeSet attrs) {
        if (attrs != null) setupCustomAttributes(attrs);

        setBackground(setupBackground());

        //  layout attrs
        setGravity(Gravity.CENTER);
        setPadding(dpToPx(DEFAULT_PADDING_LEFT_RIGHT),
                dpToPx(DEFAULT_PADDING_TOP_BOTTOM),
                dpToPx(DEFAULT_PADDING_LEFT_RIGHT),
                dpToPx(DEFAULT_PADDING_TOP_BOTTOM));

        //  blinking dot
        setupBlinkingDot();
        this.addView(imgDot);

        TextView textView = new TextView(getContext(), null);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(dpToPx(2), dpToPx(2), dpToPx(2), dpToPx(2));
        textView.setLayoutParams(params);

        //  indicator text
        String text = getResources().getString(R.string.today);
        text = StringUtils.removeAccents(text);  //  remove accents
        textView.setText(text.toUpperCase());  //  make uppercase
        textView.setTypeface(textView.getTypeface(), Typeface.BOLD);  //  make bold
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        textView.setTextColor(ResourcesCompat.getColor(getResources(), android.R.color.white, null));
        textView.setGravity(Gravity.CENTER_HORIZONTAL);
        addView(textView);

        setupAnimation(1f, scaleTo);
    }

    private void setupCustomAttributes(AttributeSet attributeSet) {
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attributeSet, R.styleable.IndicatorView, 0, 0);
        try {
            bgColor = a.getColor(R.styleable.IndicatorView_bgColor, Color.parseColor(DEFAULT_COLOR));
            textSize = a.getDimensionPixelSize(R.styleable.IndicatorView_textSize, spToPx(DEFAULT_TEXT_SIZE));
            scaleTo = a.getFloat(R.styleable.IndicatorView_scaleTo, DEFAULT_SCALE_TO);
            cornerRadius = a.getDimensionPixelSize(R.styleable.IndicatorView_cornerRadius, dpToPx(2));
            animDuration = a.getInt(R.styleable.IndicatorView_animDuration, DEFAULT_DURATION);
        } finally {
            a.recycle();
        }
    }

    /**
     * Setup the background's color and corner rounding
     *
     * @return the bg as a drawable
     */
    private Drawable setupBackground() {
        Drawable dr = ResourcesCompat.getDrawable(getResources(), R.drawable.indicator_bg, null);
        ((GradientDrawable)dr).setColor(bgColor);
        ((GradientDrawable)dr).setCornerRadius(cornerRadius);

        return dr;
    }

    private void setupBlinkingDot() {
        Drawable dot = ResourcesCompat.getDrawable(getResources(), R.drawable.dot, null);

        imgDot = new ImageView(getContext());
        imgDot.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        imgDot.setImageDrawable(dot);
    }

    /**
     * The dot's scale animation
     *
     * @param startScale  "scale from" factor, original size is 1.0
     * @param endScale  "scale to" factor
     */
    private void setupAnimation(@FloatRange(from = 0.0, to = 1.0) float startScale,
                                @FloatRange(from = 0.0, to = 1.0) float endScale) {
        anim = new ScaleAnimation(
                startScale, endScale,
                startScale, endScale,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setFillAfter(false);
        anim.setDuration(animDuration);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setRepeatCount(Animation.INFINITE);
    }

    /**
     * Converts DPs to pixels
     *
     * @param dp the DP value
     * @return the size in pixels
     */
    private int dpToPx(int dp) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    /**
     * Converts SPs to pixels
     *
     * @param sp  the SP value
     * @return  the size in pixels
     */
    private int spToPx(int sp) {

        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }

    /**
     * Plays the dot animation, useful in lifecycle events
     */
    public void startAnimation() {
        imgDot.startAnimation(anim);
    }
}
