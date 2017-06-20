package pl.gregoryiwanek.altimeter.app;

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
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.gregoryiwanek.altimeter.app.about.AboutFragmentMainWindow;
import pl.gregoryiwanek.altimeter.app.details.DetailsActivity;
import pl.gregoryiwanek.altimeter.app.mainview.SessionActivity;
import pl.gregoryiwanek.altimeter.app.map.MapActivity;
import pl.gregoryiwanek.altimeter.app.recordingsession.RecordingSessionActivity;
import pl.gregoryiwanek.altimeter.app.settings.SettingsFragment;
import pl.gregoryiwanek.altimeter.app.statistics.StatisticsActivity;
import pl.gregoryiwanek.altimeter.app.upgradepro.UpgradeProActivity;
import pl.gregoryiwanek.altimeter.app.utils.Constants;
import pl.gregoryiwanek.altimeter.app.utils.NoticeDialogFragment;
import pl.gregoryiwanek.altimeter.app.utils.NoticeDialogFragment.NoticeDialogFragmentV4.NoticeDialogListener;

/**
 * Superclass for all activities used in a project.
 * Defines behaviour of NavigationDrawer and switch between activities and fragments.
 * Important: call method {@method initiateUI()} from child activity after layout is assigned
 * in {@method onCreate()};
 * All layouts of the child activities have to have elements:
 * - toolbar with id toolbar
 * - drawerLayout with id drawer_layout
 * - navigationView with id nav_view
 */
public abstract class BasicActivity extends AppCompatActivity implements NoticeDialogListener {
    // TODO: 19.06.2017 change access operator to protected and remove thsese from children activities??
    @BindView(R.id.toolbar) protected Toolbar mToolbar;
    @BindView(R.id.drawer_layout) protected DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) protected NavigationView mNavigationView;

    private Class<?> type;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        type = SessionActivity.class;
    }

    /**
     * Called by children class to bind UI elements with child's layout
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
                menuItem -> {
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
                        case R.id.upgrade_pro_menu_item:
                            navigateToActivity(UpgradeProActivity.class);
                            break;
                        case R.id.about_navigation_menu_item:
                            navigateToFragment(AboutFragmentMainWindow.class);
                            break;
                        default:
                            break;
                    }
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    return true;
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
        tryStartActivity(intent);
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
        System.out.println("class name is: " + getClassName() + " and recording session is: " + RecordingSessionActivity.class.getSimpleName());
        return getClassName().equals(RecordingSessionActivity.class.getSimpleName())
                || getClassName().equals(DetailsActivity.class.getSimpleName());
    }

    private String getSessionIdDrawerMapGeneration() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return preferences.getString("sessionId", Constants.DEFAULT_TEXT);
    }

    private void tryStartActivity(Intent intent) {
        if (type == RecordingSessionActivity.class) {
            if (isBelowMaxSavedSessions()) {
                startActivity(intent);
            } else {
                popUpNoticeDialog(Constants.MESSAGE_UPGRADE_TO_PRO_MAX_SAVED);
            }
        } else {
            startActivity(intent);
        }
    }

    public void popUpNoticeDialog(String title) {
        Bundle args = new Bundle();
        args.putString("title", title);
        DialogFragment ndf = new NoticeDialogFragment.NoticeDialogFragmentV4();
        ndf.setArguments(args);
        ndf.show(getSupportFragmentManager(), "NoticeDialogFragment");
    }

    @Override
    public void onDialogPositiveClick(String callbackCode) {
        Intent intent = new Intent(this, UpgradeProActivity.class);
        startActivity(intent);
    };

    private boolean isBelowMaxSavedSessions() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return Constants.MAX_NUMBER_SESSIONS >= (preferences.getInt("numSavedSessions", Constants.MAX_NUMBER_SESSIONS));
    }

    public static void addFragmentToActivityOnStart(@NonNull android.support.v4.app.FragmentManager fragmentManager,
                                                    @NonNull android.support.v4.app.Fragment fragment, int frameId) {
        android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }
}