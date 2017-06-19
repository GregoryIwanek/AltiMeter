package pl.gregoryiwanek.altimeter.app.recordingsession;

import android.os.Bundle;
import android.support.annotation.Nullable;

import pl.gregoryiwanek.altimeter.app.BasicActivity;
import pl.gregoryiwanek.altimeter.app.R;
import pl.gregoryiwanek.altimeter.app.data.database.source.SessionRepository;
import pl.gregoryiwanek.altimeter.app.data.database.source.local.SessionLocalDataSource;
import pl.gregoryiwanek.altimeter.app.data.location.LocationUpdateManager;

/**
 * Main activity of RecordingSession section.
 */
public class RecordingSessionActivity extends BasicActivity{

    private RecordingSessionFragment mRecordingSessionFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_graph);

        super.initiateUI();
        super.setShareIcon();
        setAddNewGraphFragment();
        setPresenter();
    }

    private void setAddNewGraphFragment() {
        mRecordingSessionFragment =
                (RecordingSessionFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (mRecordingSessionFragment == null) {
            mRecordingSessionFragment = RecordingSessionFragment.newInstance();
            addFragmentToActivityOnStart(
                    getSupportFragmentManager(), mRecordingSessionFragment, R.id.contentFrame);
        }
    }

    @SuppressWarnings("UnusedAssignment")
    private void setPresenter() {
        RecordingSessionPresenter mRecordingSessionPresenter = new RecordingSessionPresenter(
                SessionRepository.getInstance(SessionLocalDataSource.getInstance(getApplicationContext())),
                new LocationUpdateManager(getApplicationContext()), mRecordingSessionFragment);
    }
}
