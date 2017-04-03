package pl.gregoryiwanek.altimeter.app.about;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.gregoryiwanek.altimeter.app.R;

public class InnerFragmentDetail extends BasicInnerFragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_details, container, false);

        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onFieldActionsClick() {}

    @OnClick(R.id.about_labels_details)
    public void onFieldLabelsClick() {
        showUpDialog(getString(R.string.about_labels_details));
    }

    @OnClick(R.id.about_edit_details)
    public void onFieldEditClick() {
        showUpDialog(getString(R.string.about_edit_details));
    }

    @OnClick(R.id.about_save_details)
    public void onFieldButtonSaveClick() {
        showUpDialog(getString(R.string.about_button_save_details));
    }

    @OnClick(R.id.about_info_details)
    public void onFieldInfoClick() {
        showUpDialog(getString(R.string.about_info_details));
    }
}
