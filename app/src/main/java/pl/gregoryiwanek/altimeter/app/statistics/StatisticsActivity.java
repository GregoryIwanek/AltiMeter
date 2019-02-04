package pl.gregoryiwanek.altimeter.app.statistics;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.databinding.*;

import pl.gregoryiwanek.altimeter.app.BasicActivity;
import pl.gregoryiwanek.altimeter.app.R;
import pl.gregoryiwanek.altimeter.app.data.statistics.StatisticsManager;
import pl.gregoryiwanek.altimeter.app.databinding.*;

/**
 * Main activity class of Statistics section.
 */
public class StatisticsActivity extends BasicActivity {

    private StatisticsFragment mStatisticsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        ActivityStatisticsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_statistics);

        super.initiateUI();
        setStatisticsFragment();
        setPresenter();
    }

    private void setStatisticsFragment() {
        mStatisticsFragment =
                (StatisticsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (mStatisticsFragment == null) {
            mStatisticsFragment = new StatisticsFragment();
            addFragmentToActivityOnStart(
                    getSupportFragmentManager(), mStatisticsFragment, R.id.contentFrame);
        }
    }

    //@SuppressWarnings("UnusedAssignment")
    private void setPresenter() {
        StatisticsPresenter mStatisticsPresenter = new StatisticsPresenter(mStatisticsFragment,
                new StatisticsManager(this));
    }
}
