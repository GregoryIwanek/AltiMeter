package pl.gregoryiwanek.altimeter.app.about;

import android.os.*;

import androidx.fragment.app.*;
import pl.gregoryiwanek.altimeter.app.R;

import butterknife.*;
import pl.gregoryiwanek.altimeter.app.*;
import pl.gregoryiwanek.altimeter.app.utils.widgetextensions.NoticeDialogFragment.*;

public abstract class BasicInnerFragment extends Fragment {

    protected void showUpDialog(String title) {
        Bundle args = new Bundle();
        args.putString("title", title);
        NoticeDialogFragmentApp ndf = new NoticeDialogFragmentApp();
        ndf.setArguments(args);
        ndf.show(getFragmentManager(), "NoticeDialogFragment");
    }

    @OnClick(R.id.about_menu_button)
    protected void onFieldMenuClick() {
        showUpDialog(getString(R.string.about_menu_description));
    }

    @OnClick(R.id.about_title_label)
    protected void onFieldTitleClick() {
        showUpDialog(getString(R.string.about_title));
    }

    @OnClick(R.id.about_actions_button)
    protected void onFieldActionsClick() {
        showUpDialog(getString(R.string.about_actions_main));
    }
}
