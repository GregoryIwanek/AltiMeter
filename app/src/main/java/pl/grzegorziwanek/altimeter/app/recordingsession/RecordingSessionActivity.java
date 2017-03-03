package pl.grzegorziwanek.altimeter.app.recordingsession;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import java.io.File;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.grzegorziwanek.altimeter.app.BasicActivity;
import pl.grzegorziwanek.altimeter.app.R;
import pl.grzegorziwanek.altimeter.app.data.database.source.SessionRepository;
import pl.grzegorziwanek.altimeter.app.data.database.source.local.SessionLocalDataSource;
import pl.grzegorziwanek.altimeter.app.data.location.LocationUpdateManager;
import pl.grzegorziwanek.altimeter.app.utils.ActivityUtils;

/**
 * Created by Grzegorz Iwanek on 21.01.2017.
 */

public class RecordingSessionActivity extends BasicActivity{

    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.nav_view) NavigationView mNavigationView;

    private RecordingSessionFragment mRecordingSessionFragment;
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_graph);

        super.initiateUI();
        super.setShareIcon();
        ButterKnife.bind(this);
        setAddNewGraphFragment();
        setPresenter();
    }

    private void setAddNewGraphFragment() {
        mRecordingSessionFragment =
                (RecordingSessionFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (mRecordingSessionFragment == null) {
            mRecordingSessionFragment = RecordingSessionFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mRecordingSessionFragment, R.id.contentFrame);
        }
    }

    private void setPresenter() {
        RecordingSessionPresenter mRecordingSessionPresenter = new RecordingSessionPresenter(
                SessionRepository.getInstance(SessionLocalDataSource.getInstance(getApplicationContext())),
                LocationUpdateManager.getInstance(getApplicationContext()), mRecordingSessionFragment);
    }
}
