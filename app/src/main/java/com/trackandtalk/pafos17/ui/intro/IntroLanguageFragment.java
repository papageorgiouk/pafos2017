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

package com.trackandtalk.pafos17.ui.intro;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.trackandtalk.pafos17.adapters.LanguagesSpinnerAdapter;
import com.trackandtalk.pafos17.CulturalCapitalApp;
import com.trackandtalk.pafos17.R;

import java.util.Locale;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */
public class IntroLanguageFragment extends Fragment {

    private Locale appLocale;
    private String langCode;
    private OnLanguageSelectListener listener;

    private static final int LANGUAGE_GREEK = 0;
    private static final int LANGUAGE_ENGLISH = 1;
    private static final String ARG_BG_COLOR = "bg_color";

    private int bgColor;
    TypedArray flags;

    private LinearLayout rootLayout;

    public static IntroLanguageFragment newInstance(@ColorInt int bgColor) {
        IntroLanguageFragment sampleSlide = new IntroLanguageFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_BG_COLOR, bgColor);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

//    private int layoutResId;

    public IntroLanguageFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bgColor = getArguments().getInt(ARG_BG_COLOR);

        appLocale = ((CulturalCapitalApp)getActivity().getApplication()).getLocale();
        flags = getResources().obtainTypedArray(R.array.flags);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        String[] languages = getResources().getStringArray(R.array.languages);

        View view = inflater.inflate(R.layout.fragment_intro_language, container, false);
        rootLayout = (LinearLayout)view.findViewById(R.id.intro_lang_background);
        setBackgroundColor(bgColor);
        Spinner spinner = (Spinner) view.findViewById(R.id.spinner);
        LanguagesSpinnerAdapter adapter = new LanguagesSpinnerAdapter(this.getActivity(), languages, flags);
        spinner.setAdapter(adapter);

        switch (appLocale.getLanguage()) {
            case "el":
                spinner.setSelection(LANGUAGE_GREEK);
                break;
            default:
                spinner.setSelection(LANGUAGE_ENGLISH);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Spinner position ", String.valueOf(position));

                switch (position) {
                    case LANGUAGE_GREEK:
                        langCode = "el";
                        Locale locale = new Locale(langCode);
                        compareLanguage(locale);
                        break;
                    case LANGUAGE_ENGLISH:
                        langCode = "en";
                        locale = new Locale(langCode);
                        compareLanguage(locale);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }

    /**
     * Compare the selected language with the app-wide locale.
     * If a different language is selected, do the callback
     *
     * @param locale : the selected locale/language
     */
    private void compareLanguage(Locale locale) {
        if (!locale.equals(appLocale)) {
            listener.onLanguageChange(langCode, locale);
            appLocale = locale;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnLanguageSelectListener) {
            listener = (OnLanguageSelectListener)context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnLanguageSelectListener");
        }
    }

    private void setBackgroundColor(@ColorInt int color) {
        rootLayout.setBackgroundColor(color);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flags.recycle();  //  typed array
    }

    public interface OnLanguageSelectListener {
        void onLanguageChange(String langCode, Locale locale);
    }
}
