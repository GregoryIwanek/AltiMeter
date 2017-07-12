package pl.gregoryiwanek.altimeter.app.mainview;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import com.google.android.gms.ads.MobileAds;

import pl.gregoryiwanek.altimeter.app.BasicActivity;
import pl.gregoryiwanek.altimeter.app.R;
import pl.gregoryiwanek.altimeter.app.data.database.SessionRepository;
import pl.gregoryiwanek.altimeter.app.data.database.local.LocalDataSource;
import pl.gregoryiwanek.altimeter.app.utils.formatconventer.FormatAndValueConverter;
import pl.gregoryiwanek.altimeter.app.utils.VersionController;

/**
 * Launcher activity of the application.
 */
public class SessionActivity extends BasicActivity {

    private SessionFragment mSessionFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_altitude);
        initGoogleMobileAds();

        super.initiateUI();
        setSessionFragment();
        setPresenter();
        readPreferences();
    }

    private void setSessionFragment() {
        mSessionFragment =
                (SessionFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (mSessionFragment == null) {
            mSessionFragment = SessionFragment.newInstance();
            addFragmentToActivityOnStart(
                    getSupportFragmentManager(), mSessionFragment, R.id.contentFrame);
        }
    }

    private void setPresenter() {
        SessionPresenter mSessionPresenter = new SessionPresenter(
                SessionRepository.getInstance(LocalDataSource.newInstance(this)),
                mSessionFragment);
    }

    private void readPreferences() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String units = sharedPref.getString("pref_set_units", "KILOMETERS");
        FormatAndValueConverter.setUnitsFormat(units);
    }

    private void initGoogleMobileAds() {
        if (VersionController.isFreeVersion(this.getPackageName())) {
            MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");
        }
    }
}
