package pl.grzegorziwanek.altimeter.app.statistics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.grzegorziwanek.altimeter.app.BasicActivity;
import pl.grzegorziwanek.altimeter.app.R;
import pl.grzegorziwanek.altimeter.app.data.statistics.StatisticsManager;
import pl.grzegorziwanek.altimeter.app.utils.ActivityUtils;

/**
 * Created by Grzegorz Iwanek on 21.01.2017.
 */

public class StatisticsActivity extends BasicActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView mNavigationView;

    private StatisticsFragment mStatisticsFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        super.initiateUI();
        ButterKnife.bind(this);
        setStatisticsFragment();
        setPresenter();
    }

    private void setStatisticsFragment() {
        mStatisticsFragment =
                (StatisticsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (mStatisticsFragment == null) {
            mStatisticsFragment = new StatisticsFragment();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mStatisticsFragment, R.id.contentFrame);
        }
    }

    private void setPresenter() {
        StatisticsPresenter mStatisticsPresenter = new StatisticsPresenter(mStatisticsFragment,
                new StatisticsManager(this));
    }
}
