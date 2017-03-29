package pl.grzegorziwanek.altimeter.app;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.grzegorziwanek.altimeter.app.about.AboutFragmentMain;
import pl.grzegorziwanek.altimeter.app.details.DetailsActivity;
import pl.grzegorziwanek.altimeter.app.mainview.SessionActivity;
import pl.grzegorziwanek.altimeter.app.map.MapActivity;
import pl.grzegorziwanek.altimeter.app.recordingsession.RecordingSessionActivity;
import pl.grzegorziwanek.altimeter.app.settings.SettingsFragment;
import pl.grzegorziwanek.altimeter.app.statistics.StatisticsActivity;
import pl.grzegorziwanek.altimeter.app.utils.Constants;

/**
 * Created by Grzegorz Iwanek on 21.01.2017.
 */
public abstract class BasicActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView mNavigationView;

    private Class<?> type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        type = SessionActivity.class;
    }

    /**
     * Called by children classes to bind UI elements with layout
     */
    protected void initiateUI() {
        ButterKnife.bind(this);
        setToolbar();
        setNavigationDrawer();
    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setNavigationDrawer() {
        mDrawerLayout.setStatusBarBackground(R.color.colorBlack);
        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }
    }

    protected void setShareIcon() {
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_share_24dp);
        mToolbar.setOverflowIcon(drawable);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Open navigation drawer when home clicked on toolbar
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(final NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.graphs_list_navigation_menu_item:
                                navigateToActivity(SessionActivity.class);
                                break;
                            case R.id.new_graph_navigation_menu_item:
                                navigateToActivity(RecordingSessionActivity.class);
                                break;
                            case R.id.map_navigation_menu_item:
                                navigateToActivity(MapActivity.class);
                                break;
                            case R.id.statistics_navigation_menu_item:
                                navigateToActivity(StatisticsActivity.class);
                                break;
                            case R.id.settings_navigation_menu_item:
                                navigateToFragment(SettingsFragment.class);
                                break;
                            case R.id.about_navigation_menu_item:
                                navigateToFragment(AboutFragmentMain.class);
                                break;
                            default:
                                break;
                        }
                        menuItem.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                }
        );
    }

    protected void navigateToActivity(Class<?> type) {
        if (!getClassName().equals(type.getSimpleName())) {
            setType(type);
            runActivity();
        }
    }

    private void navigateToFragment(Class<?> type) {
        if (!getClassName().equals(type.getSimpleName())) {
            setType(type);
            runFragment();
        }
    }

    private String getClassName() {
        return this.getClass().getSimpleName();
    }

    private void setType(Class<?> type) {
        this.type = type;
    }

    private void runActivity() {
        Intent intent = new Intent(BasicActivity.this, type);
        putIntentExtra(intent);
        startActivity(intent);
    }

    private void runFragment() {
        Fragment fragment = getFragmentToShow();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contentFrame, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private Fragment getFragmentToShow() {
        Fragment fragment = null;
        try {
            fragment = (Fragment) this.type.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return fragment;
    }

    private void putIntentExtra(Intent intent) {
        if (isMapCreationPossible()) {
            intent.putExtra("sessionId", getSessionIdDrawerMapGeneration());
        } else {
            intent.putExtra("sessionId", Constants.DEFAULT_TEXT);
        }
    }

    private boolean isMapCreationPossible() {
        return getClassName().equals(RecordingSessionActivity.class.getSimpleName())
                || getClassName().equals(DetailsActivity.class.getSimpleName());
    }

    private String getSessionIdDrawerMapGeneration() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return preferences.getString("sessionId", Constants.DEFAULT_TEXT);
    }
}