package pl.grzegorziwanek.altimeter.app.newgraph;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.grzegorziwanek.altimeter.app.BasicActivity;
import pl.grzegorziwanek.altimeter.app.R;
import pl.grzegorziwanek.altimeter.app.model.database.source.SessionRepository;
import pl.grzegorziwanek.altimeter.app.model.database.source.local.SessionLocalDataSource;
import pl.grzegorziwanek.altimeter.app.model.location.LocationCollector;
import pl.grzegorziwanek.altimeter.app.utils.ActivityUtils;

/**
 * Created by Grzegorz Iwanek on 21.01.2017.
 */

public class AddNewGraphActivity extends BasicActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView mNavigationView;

    private AddNewGraphFragment mAddNewGraphFragment;
    private AddNewGraphPresenter mAddNewGraphPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_graph);

        super.initiateUI();
        ButterKnife.bind(this);
        setAddNewGraphFragment();
        setPresenter();
    }

    private void setAddNewGraphFragment() {
        mAddNewGraphFragment =
                (AddNewGraphFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (mAddNewGraphFragment == null) {
            mAddNewGraphFragment = AddNewGraphFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mAddNewGraphFragment, R.id.contentFrame);
        }
    }

    private void setPresenter() {
        mAddNewGraphPresenter = new AddNewGraphPresenter(
                SessionRepository.getInstance(SessionLocalDataSource.getInstance(getApplicationContext())),
                LocationCollector.getInstance(getApplicationContext()), mAddNewGraphFragment);
    }
}
