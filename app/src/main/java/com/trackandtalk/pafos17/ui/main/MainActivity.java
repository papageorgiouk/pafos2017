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

package com.trackandtalk.pafos17.ui.main;

import android.animation.Animator;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.trackandtalk.pafos17.CulturalCapitalApp;
import com.trackandtalk.pafos17.R;
import com.trackandtalk.pafos17.adapters.EventsAdapter;
import com.trackandtalk.pafos17.adapters.ImageSlidePagerAdapter;
import com.trackandtalk.pafos17.data.AccountManager;
import com.trackandtalk.pafos17.data.model.Account;
import com.trackandtalk.pafos17.data.model.CulturalEvent;
import com.trackandtalk.pafos17.slideshow.ImageSlideFragment;
import com.trackandtalk.pafos17.ui.SocialActivity;
import com.trackandtalk.pafos17.ui.eventdetails.EventActivity;
import com.trackandtalk.pafos17.ui.explore.ExploreActivity;
import com.trackandtalk.pafos17.ui.feedback.FeedbackActivity;
import com.trackandtalk.pafos17.ui.schedule.ScheduleActivity;
import com.trackandtalk.pafos17.ui.settings.AboutActivity;
import com.trackandtalk.pafos17.ui.settings.SettingsActivity;
import com.trackandtalk.pafos17.ui.signin.GoogleSignInActivity;
import com.trackandtalk.pafos17.ui.widget.MaterialDrawerImageLoader;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


/**
 * Created by Konstantinos Papageorgiou and Charalambos Xinaris
 */
public class MainActivity extends AppCompatActivity implements MainView {

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    private Toolbar toolbar;
    private ViewGroup root;
    private LinearLayout progressBarLayout;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeLayout;

    private RecyclerView eventsRecycler;
    private EventsAdapter eventsAdapter;

    //  slideshow
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private CardView cardView;
    private static final int NUMBER_OF_SLIDING_EVENTS = 4;
    private static final long SLIDESHOW_DELAY = 4000;
    private static final long SLIDESHOW_PERIOD = 3000;

    private Activity mActivity;
    @Inject MainPresenter presenter;
    @Inject AccountManager accountManager;

    //  material drawer
    private Drawer drawer;
    private AccountHeader accountHeader;

    android.os.Handler handler;
    Runnable slideShowRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((CulturalCapitalApp)getApplicationContext()).getComponent().inject(this);

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        root = (ViewGroup) findViewById(R.id.root);
        swipeLayout = (SwipeRefreshLayout)findViewById(R.id.swipe);
        eventsRecycler = (RecyclerView)findViewById(R.id.events_list);
        viewPager = (ViewPager)findViewById(R.id.images_viewpager);
        cardView = (CardView)findViewById(R.id.card_view);
        progressBarLayout = (LinearLayout)findViewById(R.id.progressLayout);
        progressBar = (ProgressBar)findViewById(R.id.progress_bar);

        //  toolbar, title and subtitle
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.app_name);
        String subtitle = getString(R.string.european_capital_of_culture);
        getSupportActionBar().setSubtitle(subtitle);

//        populate();
        setupMaterialdrawer();

        mActivity = this;
        handler = new Handler();


        swipeLayout.findViewById(R.id.events_list);
        swipeLayout.setColorSchemeColors(
                ContextCompat.getColor(this, R.color.colorPrimary),
                ContextCompat.getColor(this, R.color.colorPrimaryLight),
                ContextCompat.getColor(this, R.color.colorAccent),
                ContextCompat.getColor(this, R.color.colorAccentLight)
        );
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.onRefresh(true);
            }
        });

        if (((CulturalCapitalApp)getApplication()).shouldshowRateDialog())  {
            showRateDialog();
        }

        presenter.attachView(this);
    }

    private void setProgressBarRunning(boolean run) {

        final int DURATION = 1000;

        if (run) {
            progressBarLayout.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
            progressBarLayout.animate()
                    .alpha(0)
                    .setDuration(DURATION)
                    .setInterpolator(new DecelerateInterpolator())
                    .setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            progressBarLayout.setVisibility(View.GONE);

                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    })
                    .start();
        }
    }

    private void setupRecyclerView(List<CulturalEvent> events) {
        String listHeader = getString(R.string.whats_next);
        eventsAdapter = new EventsAdapter(this, events, listHeader);
        eventsRecycler.setAdapter(eventsAdapter);
        RecyclerView.LayoutManager recyclerLayout = new LinearLayoutManager(this);
        eventsRecycler.setLayoutManager(recyclerLayout);

        eventsAdapter.setOnEventClickedListener(new EventsAdapter.OnEventClickedListener() {
            @Override
            public void onEventClicked(EventsAdapter.EventsVH holder, CulturalEvent event) {
                Intent intent = new Intent(getBaseContext(), EventActivity.class);
                intent.putExtra(CulturalEvent.INTENT_EXTRA_NAME, event);
                View imageView = holder.itemView.findViewById(R.id.event_image);
                View titleView = holder.itemView.findViewById(R.id.event_title);
                View indicatorView = holder.itemView.findViewById(R.id.now_indicator);

                Pair<View, String> imgPair = Pair.create(imageView, getString(R.string.image_transition));
                ArrayList<Pair<View, String>> pairs = new ArrayList<Pair<View, String>>();
                pairs.add(imgPair);
                if (indicatorView.getVisibility() == View.VISIBLE) {
                    Pair<View, String> indicatorPair = Pair.create(indicatorView, getString(R.string.indicator_transition));
                    pairs.add(indicatorPair);
                }

                Pair[] pairArray = pairs.toArray(new Pair[pairs.size()]);
                @SuppressWarnings("unchecked")
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, pairArray);
                startActivity(intent, optionsCompat.toBundle());
            }
        });
    }

    private void setupViewPager(List<CulturalEvent> events) {
        //  get the first X events
        List<CulturalEvent> slideEvents = new ArrayList<>();
        int eventsCounter = 0;

        if (events != null) {
            for (CulturalEvent event : events) {
                if (event.getImagePath() != null && !event.getImagePath().isEmpty()) {
                    slideEvents.add(event);
                    eventsCounter++;
                }
                if (eventsCounter == NUMBER_OF_SLIDING_EVENTS) {  //  we got our X events
                    break;
                }
            }

            pagerAdapter = new ImageSlidePagerAdapter(getSupportFragmentManager(), slideEvents);
            viewPager.setAdapter(pagerAdapter);
            ((ImageSlidePagerAdapter)pagerAdapter).setOnItemClickedListener(new ImageSlidePagerAdapter.OnItemClickedListener() {
                @Override
                public void onItemClicked(ImageSlideFragment fragment, final CulturalEvent clickedItem) {
                    if (clickedItem != null) {
                        Intent intent = new Intent(getBaseContext(), EventActivity.class);
                        intent.putExtra(CulturalEvent.INTENT_EXTRA_NAME, clickedItem);
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(mActivity,
                                cardView, getString(R.string.image_transition));
                        startActivity(intent, optionsCompat.toBundle());


                    }
                }
            });
        }

        startSlideShow(true);
    }

    /**
     * Start or stop the image slideshow
     *
     * @param start  start or pause
     */
    private void startSlideShow(boolean start) {
        handler.removeCallbacksAndMessages(null);  //  reset

        if (start
                && pagerAdapter != null
                && pagerAdapter.getCount() > 0) {

            final int totalPages = pagerAdapter.getCount();

            slideShowRunnable = new Runnable() {
                int page = viewPager.getCurrentItem();
                @Override
                public void run() {
                    if (page == totalPages) {
                        page = 0;
                    }
                    viewPager.setCurrentItem(page, true);
                    page++;
                    handler.postDelayed(this, SLIDESHOW_PERIOD);
                }
            };
            handler.postDelayed(slideShowRunnable, SLIDESHOW_DELAY);
        } else {
            slideShowRunnable = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (pagerAdapter != null && pagerAdapter.getCount() > 0) {
            startSlideShow(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        startSlideShow(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    private void setupMaterialdrawer() {
        Drawer.OnDrawerItemClickListener listener = new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                Intent i = null;
                switch ((int) drawerItem.getIdentifier()) {
                    case R.id.nav_my_schedule:
                        i = new Intent(MainActivity.this, ScheduleActivity.class);
                        break;
                    case R.id.nav_explore:
                        i = new Intent(MainActivity.this, ExploreActivity.class);
                        break;
                    case R.id.nav_social:
                        i = new Intent(MainActivity.this, SocialActivity.class);
                        break;
                    case R.id.nav_settings:
                        i = new Intent(MainActivity.this, SettingsActivity.class);
                        break;
                    case R.id.nav_about:
                        i = new Intent(MainActivity.this, AboutActivity.class);
                        break;
                }

                drawer.deselect();

                if (i != null) {
                    final Intent intent = i;
                    Runnable start = new Runnable() {
                        @Override
                        public void run() {
                            startActivity(intent);
                        }
                    };
                    handler.postDelayed(start, 250);
                }

                return false;
            }
        };

        drawer = new DrawerBuilder()
                .withActivity(this)
                .inflateMenu(R.menu.navigation_drawer_items)
                .withMultiSelect(false)
                .withCloseOnClick(true)
                .withInnerShadow(false)
                .withToolbar(toolbar)
                .withTranslucentStatusBar(true)
                .withActionBarDrawerToggleAnimated(true)
                .withOnDrawerItemClickListener(listener)
                .withSelectedItem(-1)
                .withFooterDivider(false)
                .withStickyFooter(R.layout.nav_drawer_footer)
                .build();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void displayProfileInfo(@Nullable Account account) {
        //  image loader for the nav drawer header (required)
        MaterialDrawerImageLoader loaderImpl = new MaterialDrawerImageLoader();
        DrawerImageLoader.init(loaderImpl);

        //  set profile header if account exists
        if (account != null && !account.isNull()) {
            //  profile image
            final Uri profilePhotoUri = account.getPhotoUri(this);

            //  entire header
            accountHeader = new AccountHeaderBuilder()
                    .withActivity(this)
                    .addProfiles(new ProfileDrawerItem()
                            .withName(account.getProfileName())
                            .withEmail(account.getProfileEmail())
                            .withIcon(profilePhotoUri),

                            new ProfileSettingDrawerItem()
                                    .withIcon(R.drawable.close)
                                    .withName(getString(R.string.logout))
                                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                        @Override
                                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                            accountManager.logout();
                                            return true;
                                        }
                                    })
                    )
                    .withHeaderBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.logo_faded, null))
                    .withHeaderBackgroundScaleType(ImageView.ScaleType.CENTER_CROP)
                    .withCurrentProfileHiddenInList(true)
                    .withDrawer(drawer)
                    .build();

        } else {  //  load blank header
            accountHeader = new AccountHeaderBuilder()
                    .withActivity(this)
                    .addProfiles(new ProfileDrawerItem()
                            .withIcon(R.drawable.person_image_empty)
                            .withName(getString(R.string.disconnected))
                            .withEmail(getString(R.string.click_to_connect)),
                        new ProfileSettingDrawerItem().withName(getString(R.string.connect)).withIcon(R.drawable.plus_black)
                            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                @Override
                                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                    startActivity(new Intent(MainActivity.this, GoogleSignInActivity.class));
                                    return true;
                                }
                            })
                    )
                    .withHeaderBackground(R.drawable.logo_faded)
                    .withHeaderBackgroundScaleType(ImageView.ScaleType.CENTER_CROP)
                    .withCurrentProfileHiddenInList(true)
                    .withDrawer(drawer)
                    .build();
        }
    }

    private void showRateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.rate_app)
                .setMessage(R.string.rate_text)
                .setNeutralButton(R.string.rate_later, null)  //  leave as is, we'll ask again later
                .setNegativeButton(R.string.rate_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((CulturalCapitalApp)getApplication()).setRated(true);  //  don't bother again
                    }
                })
                .setPositiveButton(R.string.rate_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((CulturalCapitalApp)getApplication()).setRated(true);
                        Uri appUrl = Uri.parse(String.valueOf(getText(R.string.app_play_store)));
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(appUrl);
                        startActivity(intent);
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    public void onSendFeedbackClicked(View view) {
        Intent i = new Intent(this, FeedbackActivity.class);
        startActivity(i);
        drawer.closeDrawer();
    }

    @Override
    public void showLoading(boolean loading) {
        swipeLayout.setRefreshing(loading);
        setProgressBarRunning(loading);
    }

    @Override
    public void displayEvents(@NonNull List<CulturalEvent> events, boolean fresh) {
        setupRecyclerView(events);
        setupViewPager(events);
        startSlideShow(true);

        if (!fresh) showNetworkError();
    }

    @Override
    public void displayNoEvents() {
        showLoading(false);
        eventsAdapter = new EventsAdapter(this, new ArrayList<CulturalEvent>(0), null);
        eventsRecycler.setAdapter(eventsAdapter);
        eventsRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void showNetworkError() {
        Snackbar snackbar = Snackbar.make(swipeLayout, getString(R.string.internet_problems), Snackbar.LENGTH_LONG);
        snackbar.getView().setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.error));
        snackbar.show();
    }
}
