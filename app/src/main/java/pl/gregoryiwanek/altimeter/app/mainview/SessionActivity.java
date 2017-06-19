package pl.gregoryiwanek.altimeter.app.mainview;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.gregoryiwanek.altimeter.app.BasicActivity;
import pl.gregoryiwanek.altimeter.app.R;
import pl.gregoryiwanek.altimeter.app.data.database.source.SessionRepository;
import pl.gregoryiwanek.altimeter.app.data.database.source.local.SessionLocalDataSource;
import pl.gregoryiwanek.altimeter.app.utils.FormatAndValueConverter;

/**
 * Launcher activity of the application.
 */
public class SessionActivity extends BasicActivity {

    private SessionFragment mSessionFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_altitude);

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
                SessionRepository.getInstance(SessionLocalDataSource.getInstance(this)),
                mSessionFragment);
    }

    private void readPreferences() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String units = sharedPref.getString("pref_set_units", "KILOMETERS");
        FormatAndValueConverter.setUnitsFormat(units);
    }
}
