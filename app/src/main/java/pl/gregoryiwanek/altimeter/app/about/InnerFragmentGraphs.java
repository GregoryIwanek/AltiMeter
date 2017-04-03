package pl.gregoryiwanek.altimeter.app.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.gregoryiwanek.altimeter.app.R;

public class InnerFragmentGraphs extends BasicInnerFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_graphs, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.about_checkbox_main)
    public void onFieldCheckboxClick() {
        showUpDialog(getString(R.string.about_checkbox_main));
    }

    @OnClick(R.id.about_rows_main)
    public void onFieldRowsSessionClick() {
        showUpDialog(getString(R.string.about_rows_main));
    }

    @OnClick(R.id.about_button_add_main)
    public void onFieldButtonAddClick() {
        showUpDialog(getString(R.string.about_button_add_main));
    }
}
