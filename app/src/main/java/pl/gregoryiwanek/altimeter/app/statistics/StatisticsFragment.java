package pl.gregoryiwanek.altimeter.app.statistics;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.gregoryiwanek.altimeter.app.BasicFragment;
import pl.gregoryiwanek.altimeter.app.R;

import static pl.gregoryiwanek.altimeter.app.utils.NoticeDialogFragment.NoticeDialogFragmentV4;


/**
 * Consists view class of Statistics section.
 */
public class StatisticsFragment extends BasicFragment implements StatisticsContract.View {

    @BindView(R.id.stats_sessions_numb_label_value) TextView mNumSessionTV;
    @BindView(R.id.stats_points_numb_label_value) TextView mNumPointsTV;
    @BindView(R.id.stats_distance_label_value) TextView mDistanceTV;
    @BindView(R.id.stats_max_alt_label_value) TextView mMaxAltTV;
    @BindView(R.id.stats_min_alt_label_value) TextView mMinAltTV;
    @BindView(R.id.stats_longest_label_value) TextView mLongSessionTV;

    private StatisticsContract.Presenter mPresenter;

    public StatisticsFragment() {}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        ButterKnife.bind(this, view);

        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu_statistics, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String title = "Reset statistics?";
        popUpNoticeDialog(title);
        return true;
    }

    @Override
    public void onDialogPositiveClick(String callbackCode) {
        mPresenter.resetStatistics();
    }

    @Override
    public void setPresenter(StatisticsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setNumSessionsTextView(String str) {
        mNumSessionTV.setText(str);
    }

    @Override
    public void setNumPointsTextView(String str) {
        mNumPointsTV.setText(str);
    }

    @Override
    public void setDistanceTextView(String str) {
        mDistanceTV.setText(str);
    }

    @Override
    public void setMaxAltTextView(String str) {
        mMaxAltTV.setText(str);
    }

    @Override
    public void setMinAltTextView(String str) {
        mMinAltTV.setText(str);
    }

    @Override
    public void setLongSessionTextView(String str) {
        mLongSessionTV.setText(str);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void showIsResetSuccess(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }
}
