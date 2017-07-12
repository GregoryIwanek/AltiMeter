package pl.gregoryiwanek.altimeter.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import pl.gregoryiwanek.altimeter.app.upgradepro.UpgradeProActivity;
import pl.gregoryiwanek.altimeter.app.utils.widgetextensions.NoticeDialogFragment;
import pl.gregoryiwanek.altimeter.app.utils.widgetextensions.NoticeDialogFragment.NoticeDialogFragmentV4.NoticeDialogListener;
import pl.gregoryiwanek.altimeter.app.utils.stylecontroller.StyleController;

public abstract class BasicFragment extends Fragment implements NoticeDialogListener {

    private StyleController styleController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        styleController = new StyleController(getActivity());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRootViewBackgroundColor(view);
    }

    private void setRootViewBackgroundColor(View rootView) {
        styleController.applyColorToSingleKnownView(rootView, R.attr.colorRootBackground);
    }

    protected void applyColorToMultipleViews(ViewGroup viewGroup, int attrId, Class<?> lookedType) {
        styleController.applyColorToMultipleViews(viewGroup, attrId, lookedType);
    }

    protected void applyColorToSingleView(View view, int attrId) {
        styleController.applyColorToSingleKnownView(view, attrId);
    }

    /**
     * Implementation of NoticeDialog callback interface. It's optional whether  method will be overridden
     * in child Fragment or not, and depends on whether child Fragment needs pop up windows.
     * @param callbackCode callback code used to define specific action on positive click
     */
    @Override
    public void onDialogPositiveClick(int callbackCode) {}

    protected void popUpNoticeDialog(String title) {
        popUpNoticeDialog(title, -1);
    }

    protected void popUpNoticeDialog(String title, int messageCode) {
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("code", messageCode);
        DialogFragment ndf = new NoticeDialogFragment.NoticeDialogFragmentV4();
        ndf.setArguments(args);
        ndf.show(getChildFragmentManager(), "NoticeDialogFragment");
    }

    protected void openUpgradePro() {
        Intent intent = new Intent(getActivity(), UpgradeProActivity.class);
        startActivity(intent);
    }
}
