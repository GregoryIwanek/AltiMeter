package pl.gregoryiwanek.altimeter.app.recordingsession;

import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.gregoryiwanek.altimeter.app.BasicFragment;
import pl.gregoryiwanek.altimeter.app.R;
import pl.gregoryiwanek.altimeter.app.data.GraphPoint;
import pl.gregoryiwanek.altimeter.app.map.MapActivity;
import pl.gregoryiwanek.altimeter.app.utils.Constants;
import pl.gregoryiwanek.altimeter.app.utils.ThemeManager;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * View of the RecordingSession fragment.
 * Consists number of views and inner layouts. Presents altitude graph with recorded values.
 */
public class RecordingSessionFragment extends BasicFragment implements RecordingSessionContract.View {

    @BindView(R.id.current_elevation_label) TextView mCurrElevationTextView;
    @BindView(R.id.current_latitude_value) TextView mCurrLatitudeTextView;
    @BindView(R.id.current_longitude_value) TextView mCurrLongitudeTextView;
    @BindView(R.id.max_height_numbers) TextView mMaxElevTextView;
    @BindView(R.id.min_height_numbers) TextView mMinElevTextView;
    @BindView(R.id.location_label) TextView mCurrAddressTextView;
    @BindView(R.id.distance_numbers) TextView mDistanceTextView;
    @BindView(R.id.reset_button) ImageButton mRefreshButton;
    @BindView(R.id.pause_button) ImageButton mPlayPauseButton;
    @BindView(R.id.lock_button) ImageButton mLockButton;
    @BindView(R.id.map_button) ImageButton mMapButton;
    @BindView(R.id.graph_view) GraphViewWidget mGraphViewWidget;
    @BindView(R.id.gps_button) ImageButton mGpsButton;
    @BindView(R.id.network_button) ImageButton mNetworkButton;
    @BindView(R.id.barometer_button) ImageButton mBarometerButton;
    @BindView(R.id.gps_value_label) TextView mGpsValueTextView;
    @BindView(R.id.network_value_label) TextView mNetworkValueTextView;
    @BindView(R.id.barometer_value_label) TextView mBarometerValueTextView;

    private ThemeManager themeManager;
    private RecordingSessionContract.Presenter mPresenter;

    public static RecordingSessionFragment newInstance() {
        return new RecordingSessionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_graph, container, false);
        ButterKnife.bind(this, view);
        initGraphViewDefault();
        initButtonsDefault();
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setButtonColorsByTheme();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.onActivityPaused();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onActivityDestroyedUnsubscribeRx();
    }

    @Override
    public void setPresenter(@NonNull RecordingSessionContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu_share, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share_facebook:
                shareButtonClicked();
                break;
        }
        return true;
    }

    private void shareButtonClicked() {
        ContentResolver cr = this.getActivity().getContentResolver();
        Window window = getActivity().getWindow();
        String[] textViewContent = getTextContentToShareMessage();
        mPresenter.shareScreenShot(window, cr, textViewContent);
    }

    private String[] getTextContentToShareMessage() {
        return new String[]{
                mCurrAddressTextView.getText().toString(),
                mCurrElevationTextView.getText().toString(),
                mDistanceTextView.getText().toString()
        };
    }

    @Override
    public void drawGraph(ArrayList<GraphPoint> graphPoints) {
        mGraphViewWidget.deliverGraph(graphPoints);
    }

    @Override
    public void resetGraph() {
        mGraphViewWidget.clearData();
    }

    @OnClick(R.id.reset_button)
    public void onResetButtonClick() {
        super.popUpNoticeDialog(Constants.MESSAGE_RESET_SESSION);
    }

    @OnClick(R.id.lock_button)
    public void onLockButtonCLick() {
        super.popUpNoticeDialog(Constants.MESSAGE_LOCK_SESSION);
    }

    @OnClick(R.id.map_button)
    public void onMapButtonClick() {
        mPresenter.checkIsSessionEmpty();
    }

    @OnClick(R.id.pause_button)
    public void onPlayPauseButtonClick() {
        int tag = getButtonTagAsInt(mPlayPauseButton);
        switch (tag) {
            case R.drawable.ic_play_arrow_black_24dp:
                mPresenter.callStartLocationRecording();
                super.applyColorToSingleView(mPlayPauseButton, R.attr.colorButtonSecondary);
                break;
            case R.drawable.ic_pause_black_24dp:
                mPresenter.pauseLocationRecording();
                super.applyColorToSingleView(mPlayPauseButton, R.attr.colorButtonPrimary);
                break;
            default:
                break;
        }
    }

    @OnClick(R.id.gps_button)
    public void onGpsButtonClick() {
        int tag = getButtonTagAsInt(mGpsButton);
        switch (tag) {
            case R.drawable.ic_gps_lock_24dp:
                if (isRecordingRunning()) {
                    showStopSession();
                } else {
                    mPresenter.enableGps();
                    super.applyColorToSingleView(mGpsButton, R.attr.colorButtonSecondary);
                }
                break;
            case R.drawable.ic_gps_open_24dp:
                if (isRecordingRunning()) {
                    showStopSession();
                } else {
                    mPresenter.disableGps();
                    super.applyColorToSingleView(mGpsButton, R.attr.colorButtonPrimary);
                }
                break;
        }
    }

    @OnClick(R.id.network_button)
    public void onNetworkButtonClick() {
        int tag = getButtonTagAsInt(mNetworkButton);
        switch (tag) {
            case R.drawable.ic_network_lock_24dp:
                if (isRecordingRunning()) {
                    showStopSession();
                } else {
                    mPresenter.enableNetwork();
                    super.applyColorToSingleView(mNetworkButton, R.attr.colorButtonSecondary);
                }
                break;
            case R.drawable.ic_network_open_24dp:
                if (isRecordingRunning()) {
                    showStopSession();
                } else {
                    mPresenter.disableNetwork();
                    super.applyColorToSingleView(mNetworkButton, R.attr.colorButtonPrimary);
                }
                break;
        }
    }

    @OnClick(R.id.barometer_button)
    public void onBarometerButtonClick() {
        int tag = getButtonTagAsInt(mBarometerButton);
        switch (tag) {
            case R.drawable.ic_barometer_lock_24dp:
                if (isRecordingRunning()) {
                    showStopSession();
                } else {
                    mPresenter.enableBarometer();
                    super.applyColorToSingleView(mBarometerButton, R.attr.colorButtonSecondary);
                }
                break;
            case R.drawable.ic_barometer_open_24dp:
                if (isRecordingRunning()) {
                    showStopSession();
                } else {
                    mPresenter.disableBarometer();
                    super.applyColorToSingleView(mBarometerButton, R.attr.colorButtonPrimary);
                }
                break;
        }
    }

    private boolean isRecordingRunning() {
        return getButtonTagAsInt(mPlayPauseButton)
                == R.drawable.ic_pause_black_24dp;
    }

    private void showStopSession() {
        showMessage(Constants.TOAST_MUST_STOP_SESSION);
    }

    @Override
    public void onDialogPositiveClick(String callbackCode) {
        switch (callbackCode) {
            case Constants.MESSAGE_RESET_SESSION:
                mPresenter.resetSessionData();
                break;
            case Constants.MESSAGE_LOCK_SESSION:
                mPresenter.lockSession();
                break;
            case Constants.MESSAGE_GENERATE_MAP:
                mPresenter.openMapOfSession();
                break;
        }
    }

    private void setButtonColorsByTheme() {
        ViewGroup viewContainer =  (ViewGroup) getView();
        Class<?> lookedType = mGpsButton.getClass();
        super.applyColorToMultipleViews(viewContainer, R.attr.colorButtonPrimary, lookedType);
    }

    private void initGraphViewDefault() {
        mGraphViewWidget.initGraphViewDefault();
    }

    private void initButtonsDefault() {
        initiateButtonsTags();
    }

    private void initiateButtonsTags() {
        mPlayPauseButton.setTag(R.drawable.ic_play_arrow_black_24dp);
        mGpsButton.setTag(R.drawable.ic_gps_lock_24dp);
        mNetworkButton.setTag(R.drawable.ic_network_lock_24dp);
        mBarometerButton.setTag(R.drawable.ic_barometer_lock_24dp);
    }

    private int getButtonTagAsInt(ImageButton imageButton) {
        return Integer.parseInt(imageButton.getTag().toString());
    }

    @Override
    public void setButtonTagAndPicture(int pictureId) {
        switch (pictureId) {
            case R.drawable.ic_play_arrow_black_24dp:
            case R.drawable.ic_pause_black_24dp:
                mPlayPauseButton.setTag(pictureId);
                mPlayPauseButton.setBackgroundResource(pictureId);
                break;
            case R.drawable.ic_gps_lock_24dp:
            case R.drawable.ic_gps_open_24dp:
                mGpsButton.setTag(pictureId);
                mGpsButton.setBackgroundResource(pictureId);
                break;
            case R.drawable.ic_network_lock_24dp:
            case R.drawable.ic_network_open_24dp:
                mNetworkButton.setTag(pictureId);
                mNetworkButton.setBackgroundResource(pictureId);
                break;
            case R.drawable.ic_barometer_lock_24dp:
            case R.drawable.ic_barometer_open_24dp:
                mBarometerButton.setTag(pictureId);
                mBarometerButton.setBackgroundResource(pictureId);
                break;
        }
    }

    @Override
    public void checkDataSourceOpen() {
        if (isAnyDataSourceOpen()) {
            mPresenter.startLocationRecording();
        } else {
            showMessage(Constants.TOAST_TURN_ON_SOURCE);
        }
    }

    private boolean isAnyDataSourceOpen() {
        return getButtonTagAsInt(mGpsButton) == R.drawable.ic_gps_open_24dp
                || getButtonTagAsInt(mNetworkButton) == R.drawable.ic_network_open_24dp
                || getButtonTagAsInt(mBarometerButton) == R.drawable.ic_barometer_open_24dp;
    }

    @Override
    public void showSessionLocked() {
        showMessage(Constants.TOAST_SESSION_LOCKED);
    }

    @Override
    public void showRecordingPaused() {
        showMessage(Constants.TOAST_SESSION_PAUSED);
    }

    @Override
    public void showRecordingData() {
        showMessage(Constants.TOAST_SESSION_RECORDING);
    }

    @Override
    public void askGenerateMap() {
        super.popUpNoticeDialog(Constants.MESSAGE_GENERATE_MAP);
    }

    @Override
    public void showShareMenu(Intent screenshotIntent) {
        startActivity(Intent.createChooser(screenshotIntent, Constants.MESSAGE_SEND_TO));
    }

    @Override
    public void showSessionMap(@NonNull String sessionId) {
        Intent intent = new Intent(getContext(), MapActivity.class);
        intent.putExtra("sessionId", sessionId);
        startActivity(intent);
    }

    @Override
    public void showMapEmpty() {
        showMessage(Constants.TOAST_EMPTY_MAP);
    }

    @SuppressWarnings("ConstantConditions")
    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void setAddressTextView(String address) {
        mCurrAddressTextView.setText(address);
    }

    @Override
    public void setElevationTextView(String elevation) {
        mCurrElevationTextView.setText(elevation);
    }

    @Override
    public void setMinHeightTextView(String minHeight) {
        mMinElevTextView.setText(minHeight);
    }

    @Override
    public void setDistanceTextView(String distance) {
        mDistanceTextView.setText(distance);
    }

    @Override
    public void setMaxHeightTextView(String maxHeight) {
        mMaxElevTextView.setText(maxHeight);
    }

    @Override
    public void setLatTextView(String latitude) {
        mCurrLatitudeTextView.setText(latitude);
    }

    @Override
    public void setLongTextView(String longitude) {
        mCurrLongitudeTextView.setText(longitude);
    }

    @Override
    public void setGpsTextView(String gpsAlt) {
        mGpsValueTextView.setText(gpsAlt);
    }

    @Override
    public void setNetworkTextView(String networkAlt) {
        mNetworkValueTextView.setText(networkAlt);
    }

    @Override
    public void setBarometerTextView(String barometerAlt) {
        mBarometerValueTextView.setText(barometerAlt);
    }
}

