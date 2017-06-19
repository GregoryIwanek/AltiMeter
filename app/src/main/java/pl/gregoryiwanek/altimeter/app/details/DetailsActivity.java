package pl.gregoryiwanek.altimeter.app.details;

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
 * Main activity of Details section.
 */
public class DetailsActivity extends BasicActivity {

    private DetailsFragment mDetailsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        super.initiateUI();
        readPreferences();
        setDetailsFragment();
        setPresenter();
    }

    private void setDetailsFragment() {
        mDetailsFragment =
                (DetailsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (mDetailsFragment == null) {
            mDetailsFragment = DetailsFragment.newInstance();
            addFragmentToActivityOnStart(
                    getSupportFragmentManager(), mDetailsFragment, R.id.contentFrame);
        }
    }

    @SuppressWarnings("UnusedAssignment")
    private void setPresenter() {
        String id = getIntent().getStringExtra("sessionId");
        DetailsPresenter detailsPresenter = new DetailsPresenter(id,
                SessionRepository.getInstance(SessionLocalDataSource.getInstance(this)),
                mDetailsFragment);
    }

    private void readPreferences() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String units = sharedPref.getString("pref_set_units", "KILOMETERS");
        FormatAndValueConverter.setUnitsFormat(units);
    }
}
