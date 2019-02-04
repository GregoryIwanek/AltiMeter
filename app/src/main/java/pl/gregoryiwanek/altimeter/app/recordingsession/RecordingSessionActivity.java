package pl.gregoryiwanek.altimeter.app.recordingsession;

import android.os.*;

import androidx.annotation.*;
import androidx.databinding.*;

import pl.gregoryiwanek.altimeter.app.*;
import pl.gregoryiwanek.altimeter.app.data.database.source.*;
import pl.gregoryiwanek.altimeter.app.data.database.source.local.*;
import pl.gregoryiwanek.altimeter.app.data.location.*;
import pl.gregoryiwanek.altimeter.app.databinding.*;

//import pl.gregoryiwanek.altimeter.app.data.database.local.*;

/**
 * Main activity of RecordingSession section.
 */
public class RecordingSessionActivity extends BasicActivity{

    private RecordingSessionFragment mRecordingSessionFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_graph);
        ActivityNewGraphBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_new_graph);

        super.initiateUI();
        super.setShareIcon();
        setRecordingSessionFragment();
        setPresenter();
    }

    private void setRecordingSessionFragment() {
        mRecordingSessionFragment =
                (RecordingSessionFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (mRecordingSessionFragment == null) {
            mRecordingSessionFragment = RecordingSessionFragment.newInstance();
            addFragmentToActivityOnStart(
                    getSupportFragmentManager(), mRecordingSessionFragment, R.id.contentFrame);
        }
    }

    //@SuppressWarnings("UnusedAssignment")
    private void setPresenter() {
        RecordingSessionPresenter mRecordingSessionPresenter = new RecordingSessionPresenter(
                SessionRepository.getInstance(SessionLocalDataSource./*new*/getInstance(getApplicationContext())),
                new LocationUpdateManager(getApplicationContext()), mRecordingSessionFragment);
    }

    /**
     * !!! Use FragmentManager instead of SupportFragmentManager !!!
     * !!! Outer fragments ( Settings and About) are app.Fragment class type, while layout fragments are v4.app.Fragment class type !!!
     * FragmentManager ignores instances of v4.app.Fragment on stack and returns null if top level fragment
     * is v4.app.Fragment type, means if == null we deal with layout fragment ( not Settings/About which are app.Fragment)
     * and we can save session data, otherwise we deal with outer fragment, and we should only "back";
     */
    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().findFragmentById(R.id.contentFrame) != null) {
            super.onBackPressed();
        } else {
            mRecordingSessionFragment.onBackButtonPressed(RecordingSessionActivity.super::onBackPressed);
        }
    }
}
