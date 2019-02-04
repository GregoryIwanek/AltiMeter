package pl.gregoryiwanek.altimeter.app;

import android.content.*;
import android.content.pm.*;
import android.graphics.drawable.*;
import android.os.*;
import android.preference.*;
import androidx.annotation.*;
import androidx.core.content.*;
import androidx.core.view.*;
import androidx.appcompat.app.*;
import androidx.appcompat.widget.Toolbar;
import android.view.*;
import android.widget.*;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

//import com.google.android.gms.ads.*;
import com.google.android.material.navigation.NavigationView;

import butterknife.*;
import pl.gregoryiwanek.altimeter.app.about.*;
import pl.gregoryiwanek.altimeter.app.details.*;
import pl.gregoryiwanek.altimeter.app.mainview.*;
import pl.gregoryiwanek.altimeter.app.map.*;
import pl.gregoryiwanek.altimeter.app.recordingsession.*;
import pl.gregoryiwanek.altimeter.app.settings.*;
import pl.gregoryiwanek.altimeter.app.statistics.*;
import pl.gregoryiwanek.altimeter.app.upgradepro.*;
import pl.gregoryiwanek.altimeter.app.utils.*;
import pl.gregoryiwanek.altimeter.app.utils.stylecontroller.*;
import pl.gregoryiwanek.altimeter.app.utils.widgetextensions.*;
import pl.gregoryiwanek.altimeter.app.utils.widgetextensions.NoticeDialogFragment.NoticeDialogFragmentV4.*;

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

    @BindView(R.id.toolbar) protected Toolbar mToolbar;
    @BindView(R.id.drawer_layout) protected DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) protected NavigationView mNavigationView;
//    @BindView(R.id.adView) protected AdView mAdView;

    private Class<?> type;
    private StyleController styleController = new StyleController(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setPreferredTheme();
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
//        setMobileAds();
    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    private void setNavigationDrawer() {
        setNavigationDrawerState();
        setNavigationDrawerColors();
    }

    private void setNavigationDrawerState() {
        mDrawerLayout.setStatusBarBackground(R.color.colorBlack);
        if (mNavigationView != null) {
            setupDrawerContent(mNavigationView);
        }
    }

    private void setNavigationDrawerColors() {
        ViewGroup navHeader = (ViewGroup) mNavigationView.getHeaderView(0);
        styleController.applyColorToSingleUnknownNestedView(navHeader,
                R.attr.colorButtonPrimary, ImageView.class);
        styleController.applyColorAsBackground(
                mNavigationView, R.attr.colorRootNavigation);
    }

    private void setMobileAds() {
        if (VersionController.isFreeVersion(this.getPackageName())) {
//            AdRequest adRequest = new AdRequest.Builder().build();
//            mAdView.loadAd(adRequest);
//            mAdView.setScaleX(0.9f);
//            mAdView.setScaleY(1.2f);
        }
    }

    protected void setShareIcon() {
        Drawable drawable = ContextCompat.getDrawable(this, R.drawable.ic_share_24dp);
        mToolbar.setOverflowIcon(drawable);
    }

    public void setPreferredTheme() {
        String preferredThemeName = getPreferredTheme();
        int currThemeId = styleController.getStyleAsInteger(preferredThemeName);
        this.setTheme(currThemeId);
    }

    private String getPreferredTheme() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getString("pref_theme_key", "Light");
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

    private void navigateToActivity(Class<?> type) {
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
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.contentFrame, fragment, "settings");
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

    private void tryStartActivity(Intent intent) {
        if (type == RecordingSessionActivity.class) {
            if (isLessThanMaxSavedSessions()) {
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
    public void onDialogPositiveClick(int callbackCode) {
        Intent intent = new Intent(this, UpgradeProActivity.class);
        startActivity(intent);
    }

    private boolean isLessThanMaxSavedSessions() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return Constants.MAX_NUMBER_SESSIONS >= (preferences.getInt("numSavedSessions", Constants.MAX_NUMBER_SESSIONS));
    }

    public static void addFragmentToActivityOnStart(@NonNull FragmentManager fragmentManager,
                                                    @NonNull Fragment fragment, int frameId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(frameId, fragment);
        transaction.commit();
    }
}