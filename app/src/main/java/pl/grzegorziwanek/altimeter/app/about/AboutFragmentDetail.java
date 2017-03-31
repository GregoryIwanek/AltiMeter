package pl.grzegorziwanek.altimeter.app.about;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.grzegorziwanek.altimeter.app.R;
import pl.grzegorziwanek.altimeter.app.utils.NoticeDialogFragment;

public class AboutFragmentDetail extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_details, container, false);

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

    @OnClick(R.id.about_actions_button)
    public void onFieldActionsClick() {
        showUpDialog(getString(R.string.about_actions_main));
    }

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
