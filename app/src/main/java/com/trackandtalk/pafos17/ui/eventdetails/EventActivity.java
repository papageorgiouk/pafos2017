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

package com.trackandtalk.pafos17.ui.eventdetails;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.transition.Slide;
import android.transition.Transition;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.trackandtalk.pafos17.CulturalCapitalApp;
import com.trackandtalk.pafos17.R;
import com.trackandtalk.pafos17.data.model.CulturalEvent;
import com.trackandtalk.pafos17.ui.signin.GoogleSignInActivity;
import com.trackandtalk.pafos17.ui.utils.DateFormatUtils;
import com.trackandtalk.pafos17.ui.widget.FavouriteFab;

import org.threeten.bp.LocalDateTime;

import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */
public class EventActivity extends AppCompatActivity implements EventView, OnMapReadyCallback {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private FavouriteFab fab;
    private ImageView titleImage;
    private TextView titleText;
    private TextView txtDatetime;
    private TextView txtLocation;
    private TextView eventDescription;
    private NestedScrollView contentView;
    private ImageButton btnGetZoom;

    //  maps
    SupportMapFragment supportMapFragment;
    private GoogleMap map;
    Marker marker;

    @Inject EventPresenter presenter;

    CulturalEvent event;

    @TargetApi(21)
    @Override
    public void startPostponedEnterTransition() {
        super.startPostponedEnterTransition();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_view);
        ((CulturalCapitalApp)getApplicationContext()).getComponent().inject(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        contentView = (NestedScrollView) findViewById(R.id.content_scroll_view);
        supportMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.support_map_fragment);

        fab = (FavouriteFab) findViewById(R.id.fab);

        titleImage = (ImageView) findViewById(R.id.title_image);
        titleText = (TextView) findViewById(R.id.title_text);
        txtDatetime = (TextView) findViewById(R.id.subtitle_text);
        txtLocation = (TextView) findViewById(R.id.location_text);
        eventDescription = (TextView)findViewById(R.id.event_description);
        btnGetZoom = (ImageButton)findViewById(R.id.get_zoom);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initMapView();
        initEnterTransition();

        event = getIntent().getParcelableExtra(CulturalEvent.INTENT_EXTRA_NAME);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onAddRemoveButtonClicked();
            }
        });

//        getWindow().setExitTransition(new Explode().setInterpolator(AnimationUtils.loadInterpolator(this, android.R.interpolator.fast_out_slow_in)));
//        //  Nice animations on activity enter
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            postponeEnterTransition();  //  start when Glide loads the image
//            setupEnterAnimation();
//            eventDescription.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//                @Override
//                public boolean onPreDraw() {
//                    eventDescription.getViewTreeObserver().removeOnPreDrawListener(this);  //  only run once
//
//                    enterAnimation();
////                    startPostponedEnterTransition();
//
//                    return true;
//                }
//            });
//        }

        btnGetZoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri zoomUri = Uri.parse(getString(R.string.zoom_play_store));
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(zoomUri);
                startActivity(i);
            }
        });


        presenter.attachView(this);
        presenter.setEvent(event);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    private RequestListener<Uri, GlideDrawable> getGlideRequestListener() {
        return new RequestListener<Uri, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, Uri model, Target<GlideDrawable> target, boolean isFirstResource) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startPostponedEnterTransition();
                }

                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, Uri model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                Log.d("Glide: ", "isFromMemoryCache " + String.valueOf(isFromMemoryCache).toUpperCase() + " for image " + event.getImagePath());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    startPostponedEnterTransition();
                }
                return false;
            }
        };
    }

    private void initEnterTransition() {
        Slide slide;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Transition.TransitionListener transitionListener = new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                }

                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onTransitionEnd(Transition transition) {
                    fab.show();
                    getWindow().getSharedElementEnterTransition().removeListener(this);
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            };

            slide = new Slide(Gravity.BOTTOM);
            slide.setInterpolator(new DecelerateInterpolator(1.5f));
            getWindow().setEnterTransition(slide);
            getWindow().getSharedElementEnterTransition().addListener(transitionListener);
        } else {
            enterAnimation();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (marker!= null) {
            marker.showInfoWindow();  //  show marker tooltip
        }
    }

    private void initMapView() {
        supportMapFragment.getMapAsync(this);
    }

    private boolean isPointInsideView(float x, float y, View view) {

        //  View's XY
        int location[] = new int[2];
        view.getLocationOnScreen(location);
        int viewX = location[0];
        int viewY = location[1];

        //  is inside bounds?
        return (x > viewX && x < (viewX + view.getWidth())) &&
                (y > viewY && y < (viewY + view.getHeight()));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        presenter.onMapReady();
    }

    private void startMapIntent(String locationName, double lat, double lon) {
        String s = String.format(Locale.US, "geo:0,0?q=%f,%f(%s)", lat, lon, locationName);
        Uri uri = Uri.parse(s);
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(i);
    }

    /**
     * Setup before onDraw
     */
    private void setupEnterAnimation() {
//        contentView.setTranslationY(756f);
        contentView.setAlpha(0.5f);
//        titleBox.setAlpha(0.5f);
        titleText.setAlpha(0.5f);
        txtDatetime.setAlpha(0.5f);
        txtLocation.setAlpha(0.5f);

    }

    /**
     * Animate the views we want upon activity start
     *
     * @param view  the view to be animated
     * @param offset  vertical starting position
     * @param interpolator  the interpolator
     */
    private void viewEnterAnimation(View view, float offset, Interpolator interpolator) {
        view.setTranslationY(offset);
//        view.setAlpha(0.2f);
        view.animate()
                .translationY(0)
                .alpha(1f)
                .setDuration(700)
                .setInterpolator(interpolator)
                .setListener(null)
                .start();
    }

    /**
     * The enter animation(s)
     */
    private void enterAnimation() {
        Interpolator interpolator = new FastOutSlowInInterpolator();

        Float offset = 56f;

//        viewEnterAnimation(titleBox, offset, interpolator);
        viewEnterAnimation(titleText, offset, interpolator);
        viewEnterAnimation(txtDatetime, offset, interpolator);
        viewEnterAnimation(txtLocation, offset, interpolator);
//        titleBox.setTranslationY(240);
        viewEnterAnimation(contentView, offset, interpolator);
        fab.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_itemview, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void onShareButtonClick(MenuItem item) {
        Locale locale = ((CulturalCapitalApp)getApplication()).getLocale();

        String subject = event.getEventTitle() + " - " + getString(R.string.pafos_event);

        String text = event.getEventTitle() + "\n"
                + event.getLocation().getLocationName() + " - " + DateFormatUtils.formatDateTimeLong(event.getBeginDatetime(), locale);
        String shareUrl = getString(R.string.get_the_app_link);
        String signature = getString(R.string.sent_from);
        String intentText = text + "\n\n" + signature;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, intentText);
//        shareIntent.putExtra(Intent.EXTRA_HTML_TEXT, intentText);
        shareIntent.setType("text/plain");

        startActivity(shareIntent);
    }

    /**** MVP View methods ****/

    @Override
    public void showTitle(Spanned title) {
        titleText.setText(title);
    }

    @Override
    public void showDate(LocalDateTime start, LocalDateTime end) {

        Locale locale = ((CulturalCapitalApp)getApplication()).getLocale();
        StringBuilder stringBuilder = new StringBuilder(DateFormatUtils.formatDateTimeLong(start, locale));

        if (end != null) {  //  if end time exists, append it
            String ending = DateFormatUtils.formatDateTimeLong(end, locale);
            stringBuilder.append(" - ")  //  separation between dates
                    .append(ending);
        }

        txtDatetime.setText(stringBuilder.toString());
    }

    @Override
    public void showDescription(Spanned description) {
        eventDescription.setText(description);
        eventDescription.setMovementMethod(LinkMovementMethod.getInstance());  //  apparently needed for HTML links to work
    }

    @Override
    public void showCreators(Spanned creators) {
        LinearLayout list = (LinearLayout)findViewById(R.id.empty_creators_list);
        LayoutInflater inflater = getLayoutInflater();
        final View creatorView = inflater.inflate(R.layout.creator_info, list, false);
        final TextView txtCreators = (TextView)creatorView.findViewById(R.id.creators);

        txtCreators.setText(creators);
        list.addView(creatorView);
    }

    @Override
    public void showImage(String imageUrl) {
        Uri uri = Uri.parse(imageUrl);
        Glide.with(getApplicationContext())
                .fromUri()
                .load(uri)
                .placeholder(R.drawable.placeholder_image)
                .dontTransform()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .listener(getGlideRequestListener())
                .into(titleImage);
    }

    @Override
    public void showFavouriteStatus(boolean favourite) {

        fab.setFavourite(favourite);
    }

    @Override
    public void notifyFavouriteStatus(boolean favourited) {

        String message = favourited ? getString(R.string.added_to_schedule) : getString(R.string.removed_from_schedule);

        Snackbar.make(this.contentView, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void startLoginActivity() {
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(EventActivity.this, fab, getString(R.string.transition_google_login));
        Intent login = new Intent(EventActivity.this, GoogleSignInActivity.class);
        startActivity(login, optionsCompat.toBundle());
    }

    @Override
    public void showMap(final double lat, final double lon, final String name) {
        GoogleMapOptions mapOptions = new GoogleMapOptions().liteMode(true).mapType(GoogleMap.MAP_TYPE_HYBRID);

        LatLng eventCoords = new LatLng(lat, lon);
        marker = map.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                .position(eventCoords)
                .title(name)
                .position(eventCoords)
                .flat(false)
                .draggable(false)
        );
        marker.showInfoWindow();  //  show marker tooltip
        map.moveCamera(CameraUpdateFactory.newLatLng(eventCoords));

        map.getUiSettings().setMapToolbarEnabled(false);
        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                startMapIntent(name, lat, lon);
            }
        });
        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                startMapIntent(name, lat, lon);
                return true;
            }
        });
    }
}
