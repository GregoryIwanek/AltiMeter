package pl.gregoryiwanek.altimeter.app.map;

import android.os.*;

import androidx.annotation.*;
import androidx.databinding.*;

import pl.gregoryiwanek.altimeter.app.R;

import butterknife.*;
import pl.gregoryiwanek.altimeter.app.*;
import pl.gregoryiwanek.altimeter.app.data.database.source.*;
import pl.gregoryiwanek.altimeter.app.data.database.source.local.*;
//import pl.gregoryiwanek.altimeter.app.data.database.SessionRepository;
//import pl.gregoryiwanek.altimeter.app.data.database.local.LocalDataSource;

/**
 * Consists main activity of the Map section.
 */
public class MapActivity extends BasicActivity {

    private MapFragment mMapFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewDataBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_map);
        //setContentView(R.layout.activity_map);

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
            addFragmentToActivityOnStart(
                    getSupportFragmentManager(), mMapFragment, R.id.contentFrame);
        }
    }

    //@SuppressWarnings("UnusedAssignment")
    private void setPresenter() {
        String id = getIntent().getStringExtra("sessionId");
        MapPresenter mMapPresenter = new MapPresenter(id,
                SessionRepository.getInstance(SessionLocalDataSource./*new*/getInstance(getApplicationContext())),
                mMapFragment);
    }
}
