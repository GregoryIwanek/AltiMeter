package pl.gregoryiwanek.altimeter.app.about;

import android.os.*;
import android.view.*;

import androidx.annotation.*;
import pl.gregoryiwanek.altimeter.app.R;

import butterknife.*;
import pl.gregoryiwanek.altimeter.app.*;

public class InnerFragmentStats extends BasicInnerFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_stats, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.about_labels_stats)
    public void onFieldLabelsStatsClick() {
        showUpDialog(getString(R.string.labels_stats));
    }

    @OnClick(R.id.about_values_stats)
    public void onFieldValuesStatsClick() {
        showUpDialog(getString(R.string.label_stats_values));
    }

    @Override
    protected void onFieldActionsClick() {
        showUpDialog(getString(R.string.about_actions_stats));
    }
}

