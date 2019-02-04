package pl.gregoryiwanek.altimeter.app.details;

import android.content.*;
import android.os.*;
import android.preference.*;

import androidx.annotation.*;
import androidx.databinding.*;

import pl.gregoryiwanek.altimeter.app.*;
import pl.gregoryiwanek.altimeter.app.data.database.source.*;
import pl.gregoryiwanek.altimeter.app.data.database.source.local.*;
import pl.gregoryiwanek.altimeter.app.databinding.ActivityDetailsBinding;
import pl.gregoryiwanek.altimeter.app.utils.formatconventer.*;

//import pl.gregoryiwanek.altimeter.app.data.database.SessionDataSource;
//import pl.gregoryiwanek.altimeter.app.data.database.SessionRepository;
//import pl.gregoryiwanek.altimeter.app.data.database.local.LocalDataSource;

/**
 * Main activity of Details section.
 */
public class DetailsActivity extends BasicActivity {

    private DetailsFragment mDetailsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ActivityDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_details);

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

    //@SuppressWarnings("UnusedAssignment")
    private void setPresenter() {
        String id = getIntent().getStringExtra("sessionId");
        DetailsPresenter detailsPresenter = new DetailsPresenter(id,
                SessionRepository.getInstance(SessionLocalDataSource./*new*/getInstance(this)),
                mDetailsFragment);
    }

    private void readPreferences() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String units = sharedPref.getString("pref_set_units", "KILOMETERS");
        FormatAndValueConverter.setUnitsFormat(units);
    }
}
