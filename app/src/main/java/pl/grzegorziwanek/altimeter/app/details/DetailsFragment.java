package pl.grzegorziwanek.altimeter.app.details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.grzegorziwanek.altimeter.app.R;

/**
 * Created by Grzegorz Iwanek on 09.02.2017.
 */

public class DetailsFragment extends Fragment implements DetailsContract.View {
    @BindView(R.id.title_value_label) EditText mTitleTV;
    @BindView(R.id.description_value_label) EditText mDescriptionTV;
    @BindView(R.id.id_value_label) TextView mIdTV;
    @BindView(R.id.points_count_value_label) TextView mNumPointsTV;
    @BindView(R.id.time_start_value_label) TextView mTimeStartTV;
    @BindView(R.id.time_end_value_label) TextView mTimeEndTV;
    @BindView(R.id.distance_value_label) TextView mDistanceTV;

    private DetailsContract.Presenter mPresenter;

    public DetailsFragment() {}

    public static DetailsFragment newInstance() {
        return new DetailsFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, view);
        return view;
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

    @OnClick(R.id.title_value_label)
    public void onTitleClick() {

    }

    @OnClick(R.id.description_value_label)
    public void onDescriptionClick() {

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
}