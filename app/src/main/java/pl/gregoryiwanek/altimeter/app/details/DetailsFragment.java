package pl.gregoryiwanek.altimeter.app.details;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.gregoryiwanek.altimeter.app.BasicFragment;
import pl.gregoryiwanek.altimeter.app.R;
import pl.gregoryiwanek.altimeter.app.utils.Constants;
import pl.gregoryiwanek.altimeter.app.utils.VersionController;

/**
 * View class of Details section.
 */
public class DetailsFragment extends BasicFragment implements DetailsContract.View {

    @BindView(R.id.title_value_label) EditText mTitleTV;
    @BindView(R.id.description_value_label) EditText mDescriptionTV;
    @BindView(R.id.id_value_label) TextView mIdTV;
    @BindView(R.id.points_count_value_label) TextView mNumPointsTV;
    @BindView(R.id.time_start_value_label) TextView mTimeStartTV;
    @BindView(R.id.time_end_value_label) TextView mTimeEndTV;
    @BindView(R.id.distance_value_label) TextView mDistanceTV;
    @BindView(R.id.save_button) Button mSaveButton;

    private DetailsContract.Presenter mPresenter;

    public DetailsFragment() {}

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu_details, menu);
    }

    // todo programme behaviour of the menu "export" button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_export_session_data:
                String packageName = this.getClass().getPackage().toString();
                if (!VersionController.isFreeVersion(packageName)) {
                    popUpNoticeDialog(Constants.MESSAGE_UPGRADE_TO_PRO);
                } else {
                    // todo code responsible for exporting data from database to file
                }
                break;
        }
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(DetailsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @OnClick(R.id.save_button)
    public void onSaveButtonClick() {
        mPresenter.saveTextChanges();
    }

    @Override
    public void sendChanges() {
        Map<String, String> changesMap = new ArrayMap<>();
        changesMap.put("id", mIdTV.getText().toString());
        changesMap.put("title", mTitleTV.getText().toString());
        changesMap.put("description", mDescriptionTV.getText().toString());
        mPresenter.saveChangesInRepository(changesMap);
    }

    @Override
    public void showChangesSaved() {
        showMessage("Changes saved");
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setTitleTextView(String title) {
        mTitleTV.setText(title);
    }

    @Override
    public void setDescriptionTextView(String description) {
        mDescriptionTV.setText(description);
    }

    @Override
    public void setIdTextView(String id) {
        mIdTV.setText(id);
    }

    @Override
    public void setNumPointsTextView(String numOfPoints) {
        mNumPointsTV.setText(numOfPoints);
    }

    @Override
    public void setTimeStartTextView(String timeStart) {
        mTimeStartTV.setText(timeStart);
    }

    @Override
    public void setTimeEndTextView(String timeEnd) {
        mTimeEndTV.setText(timeEnd);
    }

    @Override
    public void setDistanceTextView(String distance) {
        mDistanceTV.setText(distance);
    }

    // // TODO: 17.06.2017
    @Override
    public void onDialogPositiveClick(String callbackCode) {

    }
}