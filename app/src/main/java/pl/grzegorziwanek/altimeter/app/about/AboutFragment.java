package pl.grzegorziwanek.altimeter.app.about;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.grzegorziwanek.altimeter.app.R;
import pl.grzegorziwanek.altimeter.app.utils.NoticeDialogFragment;

/**
 * Created by Grzegorz Iwanek on 21.01.2017.
 */

public final class AboutFragment extends Fragment {
    @BindView(R.id.about_menu_button) RelativeLayout mFieldMenuButton;
    @BindView(R.id.about_title_label) RelativeLayout mFieldTitleLabel;
    @BindView(R.id.about_share_button) RelativeLayout mFieldShareButton;

    @BindView(R.id.about_address) RelativeLayout mFieldAddress;
    @BindView(R.id.about_current_elevation) RelativeLayout mFieldCurrentElevation;
    @BindView(R.id.about_recording_buttons) RelativeLayout mFieldRecordingButtons;

    @BindView(R.id.about_graph) LinearLayout mFieldGraph;

    @BindView(R.id.about_button_gps) RelativeLayout mFieldButtonGps;
    @BindView(R.id.about_button_network) RelativeLayout mFieldButtonNetwork;
    @BindView(R.id.about_button_pressure) RelativeLayout mFieldButtonPressure;

    @BindView(R.id.about_min_altitude) RelativeLayout mFieldMinAltitude;
    @BindView(R.id.about_distance_label) RelativeLayout mFieldDistanceLabel;
    @BindView(R.id.about_max_altitude) RelativeLayout mFieldMaxAltitude;

    @BindView(R.id.about_latitude_label) RelativeLayout mFieldLatitude;
    @BindView(R.id.about_longitude_label) RelativeLayout mFieldLongitude;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        if (view != null) {
            view.setBackgroundColor(ContextCompat.getColor(this.getActivity(), R.color.colorBlack));
        }

        ButterKnife.bind(this, view);
        return view;
    }

    private void showUpDialog(String title) {
        Bundle args = new Bundle();
        args.putString("title", title);
        DialogFragment ndf = new NoticeDialogFragment.NoticeDialogFragmentApp();
        ndf.setArguments(args);
        ndf.show(getFragmentManager(), "NoticeDialogFragment");
    }

    @OnClick(R.id.about_menu_button)
    public void onFieldMenuClick() {
        showUpDialog(getString(R.string.about_menu_description));
    }

    @OnClick(R.id.about_title_label)
    public void onFieldTitleClick() {
        showUpDialog(getString(R.string.about_title));
    }

    @OnClick(R.id.about_share_button)
    public void onFieldShareClick() {
        showUpDialog(getString(R.string.about_share_button));
    }

    @OnClick(R.id.about_address)
    public void onFieldAddressClick() {
        showUpDialog(getString(R.string.about_address_label));
    }

    @OnClick(R.id.about_current_elevation)
    public void onFieldCurrentElevationClick() {
        showUpDialog(getString(R.string.about_current_elevation));
    }

    @OnClick(R.id.about_recording_buttons)
    public void onFieldRecordingButtonsClick() {
       showUpDialog(getString(R.string.about_recording_buttons));
    }

    @OnClick(R.id.about_graph)
    public void onFieldGraphClick() {
        showUpDialog(getString(R.string.about_graph));
    }

    @OnClick(R.id.about_button_gps)
    public void onFieldButtonGpsClick() {
        showUpDialog(getString(R.string.about_gps_button));
    }

    @OnClick(R.id.about_button_network)
    public void onFieldButtonNetworkClick() {
        showUpDialog(getString(R.string.about_network_button));
    }

    @OnClick(R.id.about_button_pressure)
    public void onFieldButtonPressureClick() {
        showUpDialog(getString(R.string.about_pressure_button));
    }

    @OnClick(R.id.about_min_altitude)
    public void onFieldMinAltitudeClick() {
        showUpDialog(getString(R.string.about_min_height));
    }

    @OnClick(R.id.about_distance_label)
    public void onFieldDistanceClick() {
        showUpDialog(getString(R.string.about_distance));
    }

    @OnClick(R.id.about_max_altitude)
    public void onFieldMaxAltitudeClick() {
        showUpDialog(getString(R.string.about_max_height));
    }

    @OnClick(R.id.about_latitude_label)
    public void onFieldLatitudeClick() {
        showUpDialog(getString(R.string.about_latitude));
    }

    @OnClick(R.id.about_longitude_label)
    public void onFieldLongitudeClick() {
        showUpDialog(getString(R.string.about_longitude));
    }
}

