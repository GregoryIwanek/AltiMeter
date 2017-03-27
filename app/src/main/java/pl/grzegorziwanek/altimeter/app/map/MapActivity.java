package pl.grzegorziwanek.altimeter.app.map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.grzegorziwanek.altimeter.app.BasicActivity;
import pl.grzegorziwanek.altimeter.app.R;
import pl.grzegorziwanek.altimeter.app.data.database.source.SessionRepository;
import pl.grzegorziwanek.altimeter.app.data.database.source.local.SessionLocalDataSource;
import pl.grzegorziwanek.altimeter.app.utils.ActivityUtils;

/**
 * Created by Grzegorz Iwanek on 21.01.2017.
 */
public class MapActivity extends BasicActivity {

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView mNavigationView;

    private MapFragment mMapFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        super.initiateUI();
        super.setShareIcon();
        setMapFragment();
        setPresenter();
        ButterKnife.bind(this);
    }

    private void setMapFragment() {
        mMapFragment =
                (MapFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (mMapFragment == null) {
            mMapFragment = new MapFragment();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mMapFragment, R.id.contentFrame);
        }
    }

    private void setPresenter() {
        String id = getIntent().getStringExtra("sessionId");
        MapPresenter mMapPresenter = new MapPresenter(id,
                SessionRepository.getInstance(SessionLocalDataSource.getInstance(getApplicationContext())),
                mMapFragment);
    }
}
