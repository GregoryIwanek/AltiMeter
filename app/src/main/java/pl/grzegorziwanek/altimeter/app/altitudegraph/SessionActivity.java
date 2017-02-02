package pl.grzegorziwanek.altimeter.app.altitudegraph;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.grzegorziwanek.altimeter.app.BasicActivity;
import pl.grzegorziwanek.altimeter.app.R;
import pl.grzegorziwanek.altimeter.app.model.database.source.SessionRepository;
import pl.grzegorziwanek.altimeter.app.model.database.source.local.SessionLocalDataSource;
import pl.grzegorziwanek.altimeter.app.utils.ActivityUtils;

/**
 * Created by Grzegorz Iwanek on 18.01.2017.
 */
public class SessionActivity extends BasicActivity {

    //private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";
    private static final String LOG_TAG = SessionActivity.class.getSimpleName();

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView mNavigationView;

    private SessionFragment mSessionFragment;
    private SessionPresenter mSessionPresenter;
    private GoogleApiClient client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_altitude);

        super.initiateUI();
        ButterKnife.bind(this);
        setSessionFragment();
        setPresenter();
        setGoogleApiClient();
    }

    private void setSessionFragment() {
        mSessionFragment =
                (SessionFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (mSessionFragment == null) {
            mSessionFragment = SessionFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mSessionFragment, R.id.contentFrame);
            System.out.println("CALL FROM MAIN ACTIVITY IF FRAGMENT IS ACTIVE after initiation from null: " + mSessionFragment.isActive());
        }
    }

    private void setPresenter() {
        mSessionPresenter = new SessionPresenter(
                SessionRepository.getInstance(SessionLocalDataSource.getInstance(this.getApplicationContext())),
                mSessionFragment);
    }

    private void setGoogleApiClient() {
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onStart() {
        super.onStart();
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    //TODO-> learn WTF is that and why was it generated after updating Android Studio to new version
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("AltitudeGraph Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}
