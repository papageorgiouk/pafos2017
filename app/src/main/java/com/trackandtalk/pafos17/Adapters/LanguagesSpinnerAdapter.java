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

package com.trackandtalk.pafos17.adapters;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.trackandtalk.pafos17.R;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */
public class LanguagesSpinnerAdapter extends ArrayAdapter<String> {

    private Context context;
    private String[] languages;
    private TypedArray flags;

    public LanguagesSpinnerAdapter(Context context, String[] languages, TypedArray flags) {
        super(context, R.layout.spinner_language_row, R.id.language, languages);

        this.context = context;
        this.languages = languages;
        this.flags = flags;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View rowView = inflater.inflate(R.layout.spinner_language_row, parent, false);
        TextView textView = (TextView)rowView.findViewById(R.id.language);
        ImageView flagView = (ImageView)rowView.findViewById(R.id.flag);

        textView.setText(languages[position]);
        flagView.setImageResource(flags.getResourceId(position, -1));

        return rowView;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {

        View dropDownView = LayoutInflater.from(context).inflate(R.layout.spinner_language_row, parent, false);
        TextView textView = (TextView)dropDownView.findViewById(R.id.language);
        ImageView flagView = (ImageView)dropDownView.findViewById(R.id.flag);

        textView.setText(languages[position]);
        flagView.setImageResource(flags.getResourceId(position, -1));

        return dropDownView;
    }


}
