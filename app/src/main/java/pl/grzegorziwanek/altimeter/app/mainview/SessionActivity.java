package pl.grzegorziwanek.altimeter.app.mainview;

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import pl.grzegorziwanek.altimeter.app.data.database.source.SessionRepository;
import pl.grzegorziwanek.altimeter.app.data.database.source.local.SessionLocalDataSource;
import pl.grzegorziwanek.altimeter.app.utils.FormatAndValueConverter;

/**
 * Created by Grzegorz Iwanek on 18.01.2017.
 */
public class SessionActivity extends BasicActivity {
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView mNavigationView;

    private SessionFragment mSessionFragment;
    private GoogleApiClient client;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_altitude);

        super.initiateUI();
        ButterKnife.bind(this);
        setSessionFragment();
        setPresenter();
        readPreferences();
        setGoogleApiClient();
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

    private Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("AltitudeGraph Page")
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }
}
