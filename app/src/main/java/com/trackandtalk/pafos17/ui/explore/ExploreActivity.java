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

package com.trackandtalk.pafos17.ui.explore;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.trackandtalk.pafos17.CulturalCapitalApp;
import com.trackandtalk.pafos17.R;
import com.trackandtalk.pafos17.adapters.LandmarkImagesAdapter;
import com.trackandtalk.pafos17.adapters.MapCarouselAdapter;
import com.trackandtalk.pafos17.dagger.AppComponent;
import com.trackandtalk.pafos17.data.model.CulturalEvent;
import com.trackandtalk.pafos17.data.model.EventLocation;
import com.trackandtalk.pafos17.data.model.Landmark;
import com.trackandtalk.pafos17.helper.NestedScrollViewHelper;
import com.trackandtalk.pafos17.ui.eventdetails.EventActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */
public class ExploreActivity extends AppCompatActivity implements ExploreView, OnMapReadyCallback {

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    List<CulturalEvent> events;
    List<Marker> venueMarkers = new ArrayList<>();
    private Activity mActivity;

    private SlidingUpPanelLayout slidingUpPanelLayout;
    int screenHeight;
    int screenWidth;

    //  landmarks
    private TextView landmarkTitle;
    private TextView landmarkDescription;
    private TextView landmarkAddress;
    private TextView landmarkContact;
    private View landmarkScrollView;
    private List<Landmark> landmarks;
    private Landmark mLandmark;
    private List<Marker> landmarkMarkers;
    private RecyclerView landmarkImagesRecycler;
    private ExplorePresenter.LandmarkViewState panelState;


    //  carousel
    private RecyclerView carouselRecycler;
    private MapCarouselAdapter carouselAdapter;
    private LinearLayoutManager carouselLayoutManager;

    @Inject
    ExplorePresenter presenter;

    private static final String STATE_LANDMARK = "mLandmark";
    private static final String STATE_EVENTS = "events";
    private static final String STATE_LANDMARKS = "landmarks";
    private static final String STATE_PANEL_STATE = "panel_state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppComponent component = getAppComponent();
        component.inject(this);

        //  restore state
        if (savedInstanceState != null) {
            mLandmark = savedInstanceState.getParcelable(STATE_LANDMARK);
            events = savedInstanceState.getParcelableArrayList(STATE_EVENTS);
            landmarks = savedInstanceState.getParcelableArrayList(STATE_LANDMARKS);
            panelState = (ExplorePresenter.LandmarkViewState) savedInstanceState.getSerializable(STATE_PANEL_STATE);
        }

        mActivity = this;

        setContentView(R.layout.activity_explore);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.setRetainInstance(true);

        //  landmark sheet
        initSlidingPanel();
        if (mLandmark != null) {
            showLandmarkDetails(mLandmark, panelState);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        //  useful for recyclerview items snapping
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenHeight = displayMetrics.heightPixels;
        screenWidth = displayMetrics.widthPixels;

        presenter.attachView(this);
    }

    private void initSlidingPanel() {
        landmarkTitle = (TextView)findViewById(R.id.landmark_title);
        landmarkDescription = (TextView)findViewById(R.id.landmark_description);
        landmarkAddress = (TextView)findViewById(R.id.landmark_address);
        landmarkContact = (TextView)findViewById(R.id.landmark_contact);
        landmarkImagesRecycler = (RecyclerView)findViewById(R.id.images_recycler);
        landmarkScrollView = findViewById(R.id.scroll_view);
        slidingUpPanelLayout = (SlidingUpPanelLayout)findViewById(R.id.sliding_layout);
        slidingUpPanelLayout.setScrollableViewHelper(new NestedScrollViewHelper());

        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                landmarkScrollView.setTranslationY(landmarkImagesRecycler.getHeight() * slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                if (newState == SlidingUpPanelLayout.PanelState.EXPANDED) {
                    //  nested scrollview is buggy as fuck, the translationY (above) messes with the scroll position
                    //  compensate for it by adding the height of the images as padding at the bottom

                    //  retain all existing padding except bottom
                    int left = landmarkScrollView.getPaddingLeft();
                    int top = landmarkScrollView.getPaddingTop();
                    int right = landmarkScrollView.getPaddingRight();

                    landmarkScrollView.setPadding(left, top, right, landmarkImagesRecycler.getHeight());
                }
            }
        });
    }

    /**
     * <p>Calculates the dimensions of provided views and figures out the largest</p>
     *
     * <p>Used for the snapping items in the carousel/recyclerview. When the user scrolls, we snap to the biggest visible item</p>
     *
     * @param viewMap  the views currently visible
     * @return  the index of the largest visible view
     */
    private int getLargestView(Map<Integer, View> viewMap) {
        //  set the first as the largest, so we can compare
        Integer largestViewKey = viewMap.keySet().iterator().next();  //  first key
        Map.Entry<Integer, View> largeView = viewMap.entrySet().iterator().next();


        //  find the key of the View with the biggest width
        for (Map.Entry<Integer, View> entry : viewMap.entrySet()) {
            Rect largestViewVisibleRect = new Rect();
            largeView.getValue().getGlobalVisibleRect(largestViewVisibleRect);

            Rect currentViewVisibleRect = new Rect();
            entry.getValue().getGlobalVisibleRect(currentViewVisibleRect);
            Log.d("View " + entry.getKey() + " width: ", String.valueOf(currentViewVisibleRect.width()));

            if (currentViewVisibleRect.width() > largestViewVisibleRect.width()) {
                largestViewKey = entry.getKey();
            }
        }

        return largestViewKey;
    }

    private void centerMapOnItem(CulturalEvent event) {
        for (Marker marker : venueMarkers) {
            double eventLat = event.getLocation().getLatitude();
            double eventLng = event.getLocation().getLongitude();
            if (eventLat == marker.getPosition().latitude && eventLng == marker.getPosition().longitude) {
                if (mMap != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 16));
                    marker.setTitle(event.getLocation().getLocationName());
                    marker.showInfoWindow();
                }
                break;
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add venueMarkers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 5);
        } else {
            mMap.setMyLocationEnabled(true);
        }

        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        presenter.onMapReady(events, landmarks);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (slidingUpPanelLayout.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                }
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                if (marker.getTag() instanceof Landmark) {
                    Landmark lm = (Landmark)marker.getTag();
                    presenter.onLandmarkMarkerClicked(lm);
                }
                return true;
            }
        });
    }

    @Override
    public void displayLandmarks(List<Landmark> landmarks) {
        this.landmarks = landmarks;

        landmarkMarkers = new ArrayList<>();

        for (Landmark landmark : landmarks) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .title(landmark.getName())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_landmark2))
                    .position(landmark.getPosition()));
            marker.setTag(landmark);
            landmarkMarkers.add(marker);
        }
    }

    @Override
    public void showLandmarkDetails(final Landmark landmark, @Nullable ExplorePresenter.LandmarkViewState state) {
        this.mLandmark = landmark;

        if (state != null) {
            switch (state) {
                case EXPANDED:
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    break;
                case COLLAPSED:
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    break;
                case HIDDEN:
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                    break;
                default:
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        } else {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }

        ImageButton map = (ImageButton)findViewById(R.id.map_button);
        if (map != null) {
            map.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.onLandmarkMapButtonClicked(landmark);
                }
            });
        }

        landmarkTitle.setText(landmark.getName());
        landmarkDescription.setText(Html.fromHtml(landmark.getDescription()));
        landmarkAddress.setText(landmark.getAddress());
        landmarkContact.setText(landmark.getContactInfo());

        //  images
        LandmarkImagesAdapter adapter = new LandmarkImagesAdapter(ExploreActivity.this, landmark);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ExploreActivity.this, LinearLayoutManager.HORIZONTAL, false);
        landmarkImagesRecycler.setAdapter(adapter);
        landmarkImagesRecycler.setLayoutManager(layoutManager);
        adapter.setImageClickListener(new LandmarkImagesAdapter.LandmarkImageClickListener() {
            @Override
            public void onClick(View v, int position) {
                Intent fullScreenPhoto = new Intent(ExploreActivity.this, ImageViewerActivity.class);
                fullScreenPhoto.putExtra(ImageViewerActivity.EXTRA_LANDMARK, landmark);
                fullScreenPhoto.putExtra(ImageViewerActivity.EXTRA_IMAGE_POSITION, position);

                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity, v, getString(R.string.image_transition));
                startActivity(fullScreenPhoto, optionsCompat.toBundle());

            }
        });

        landmarkScrollView.scrollTo(0, 0);
        landmarkScrollView.setTranslationY(0);
    }

    @Override
    public void onBackPressed() {
        if (slidingUpPanelLayout.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList(STATE_EVENTS, (ArrayList<CulturalEvent>)events);
        outState.putParcelableArrayList(STATE_LANDMARKS, (ArrayList<Landmark>)landmarks);
        outState.putParcelable(STATE_LANDMARK, mLandmark);

        //  landmark display state
        switch (slidingUpPanelLayout.getPanelState()) {
            case EXPANDED:
                outState.putSerializable(STATE_PANEL_STATE, ExplorePresenter.LandmarkViewState.EXPANDED);
                break;
            case COLLAPSED:
                outState.putSerializable(STATE_PANEL_STATE, ExplorePresenter.LandmarkViewState.COLLAPSED);
                break;
            case HIDDEN:
                outState.putSerializable(STATE_PANEL_STATE, ExplorePresenter.LandmarkViewState.HIDDEN);
                break;
            default:
                outState.putSerializable(STATE_PANEL_STATE, ExplorePresenter.LandmarkViewState.HIDDEN);
        }
    }

    AppComponent getAppComponent() {
        return ((CulturalCapitalApp)getApplicationContext()).getComponent();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 5:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
        }
    }

    /**
     * <p>When the landmark info panel is peeking, clicking on it expands the panel</p>
     *
     * @param view  the view occupying the top of the panel
     */
    public void onPanelTitleClick(View view) {
        if (slidingUpPanelLayout.getPanelState() != SlidingUpPanelLayout.PanelState.EXPANDED) {
            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
        }
    }

    @Override
    public void setupMap() {
        mapFragment.getMapAsync(this);
    }

    @Override
    public void displayEventsList(final List<CulturalEvent> eventList) {
        this.events = eventList;

        //  carousel
        carouselRecycler = (RecyclerView) findViewById(R.id.carousel_recycler);
        if (eventList != null && eventList.size() > 0) {
            carouselRecycler.setVisibility(View.VISIBLE);
            carouselLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            carouselRecycler.setLayoutManager(carouselLayoutManager);
            carouselAdapter = new MapCarouselAdapter(this, eventList);
            carouselRecycler.setAdapter(carouselAdapter);
            carouselRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && carouselAdapter.getItemCount() > 0) {
                        //  calculate the largest visible view and snap to it
                        int first = carouselLayoutManager.findFirstVisibleItemPosition();  //  first visible child
                        int last = carouselLayoutManager.findLastVisibleItemPosition();  //  last visible child
                        Log.i("First visible item:", String.valueOf(first));
                        Log.i("Last visible item:", String.valueOf(last));
                        Map<Integer, View> views = new HashMap<Integer, View>();
                        for (int i = first; i <= last; i++) {
                            views.put(i, carouselLayoutManager.findViewByPosition(i));  //  position - view
                        }

                        int largestChild = getLargestView(views);

                        //  calculate offset so the item is centered when we snap to it
                        //  offset = (screenWidth - viewWidth - viewMarginLeft) / 2
                        int viewMarginLeft = (int) getResources().getDimension(R.dimen.card_margin_start);
                        View view = views.get(largestChild);
                        int offset = (screenWidth - view.getWidth() - viewMarginLeft) / 2;
                        carouselLayoutManager.scrollToPositionWithOffset(largestChild, offset);

                        //  move the map to focus on currently selected event
                        centerMapOnItem(eventList.get(largestChild));

                        //  avoid memory leaks
                        views.clear();
                        views = null;

                    }
                }
            });

            carouselAdapter.setOnItemClickListener(new MapCarouselAdapter.OnItemClickListener() {
                @Override
                public void onItemClicked(MapCarouselAdapter.ViewHolder holder, View view, CulturalEvent event) {
                    Intent i = new Intent(getBaseContext(), EventActivity.class);
                    i.putExtra(CulturalEvent.INTENT_EXTRA_NAME, event);
                    View image = holder.image;

                    Pair<View, String> pair = new Pair<View, String>(image, getString(R.string.image_transition));
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(ExploreActivity.this, pair);
                    startActivity(i, options.toBundle());
                }
            });
        }
    }

    @Override
    public void displayEventLocations(Set<EventLocation> locations) {
        //  bounds include all events so the map zooms to fit
        LatLngBounds.Builder locationsBounds = new LatLngBounds.Builder();

        if (locations != null && locations.size() > 0) {
            for (EventLocation loc : locations) {
                LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());
                locationsBounds.include(latLng);

                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(loc.getLocationName())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));

                venueMarkers.add(marker);
            }
        } else {

            //  Pafos bounds
            LatLng northwest = new LatLng(34.779409, 32.405956);
            LatLng northeast = new LatLng(34.780406, 32.443190);
            LatLng southwest = new LatLng(34.750475, 32.402288);
            LatLng southeast = new LatLng(34.756116, 32.443787);

            locationsBounds.include(northwest)
                    .include(northeast)
                    .include(southwest)
                    .include(southeast);
        }

        //  map view includes all bounds, else just default to Pafos area
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(locationsBounds.build(), 300));
    }

    @Override
    public void openLandmarkMap(String uriString) {
        Uri uri = Uri.parse(uriString);
        Intent i = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(i);
    }
}
