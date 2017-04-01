package pl.gregoryiwanek.altimeter.app.about;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.gregoryiwanek.altimeter.app.R;
import pl.gregoryiwanek.altimeter.app.utils.NoticeDialogFragment;

public class AboutFragmentGraphs extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_graphs, container, false);

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

    @OnClick(R.id.about_menu_button_main)
    public void onFieldMenuClick() {
        showUpDialog(getString(R.string.about_menu_description));
    }

    @OnClick(R.id.about_title_label_main)
    public void onFieldTitleClick() {
        showUpDialog(getString(R.string.about_title));
    }

    @OnClick(R.id.about_actions_button_main)
    public void onFieldActionsClick() {
        showUpDialog(getString(R.string.about_actions_main));
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
