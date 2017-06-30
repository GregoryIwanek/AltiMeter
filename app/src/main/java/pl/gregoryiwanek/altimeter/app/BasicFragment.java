package pl.gregoryiwanek.altimeter.app;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;

import pl.gregoryiwanek.altimeter.app.upgradepro.UpgradeProActivity;
import pl.gregoryiwanek.altimeter.app.utils.NoticeDialogFragment;
import pl.gregoryiwanek.altimeter.app.utils.NoticeDialogFragment.NoticeDialogFragmentV4.NoticeDialogListener;
import pl.gregoryiwanek.altimeter.app.utils.ThemeManager;

public abstract class BasicFragment extends Fragment implements NoticeDialogListener {

    private ThemeManager themeManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themeManager = new ThemeManager();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRootViewBackgroundColor(view);
    }

    private void setRootViewBackgroundColor(View rootView) {
        Context context = getActivity();
        themeManager.applyColorToSingleView(rootView, R.attr.colorRootBackground, context);
    }

    protected void applyColorToMultipleViews(ViewGroup viewGroup, int attrId, Class<?> lookedType) {
        Context context = getActivity();
        themeManager.applyColorToMultipleViews(viewGroup, attrId, lookedType, context);
    }

    protected void applyColorToSingleView(View view, int attrId) {
        Context context = getActivity();
        themeManager.applyColorToSingleView(view, attrId, context);
    }

    /**
     * Implementation of NoticeDialog callback interface. It's optional whether  method will be overridden
     * in child Fragment or not, and depends on whether child Fragment needs pop up windows.
     * @param callbackCode callback code used to define specific action on positive click
     */
    @Override
    public void onDialogPositiveClick(String callbackCode) {}

    /**
     * Initiation of NoticeDialog with positive/cancel options ( pop up window with a message).
     * @param title title shown in the pop up window
     */
    protected void popUpNoticeDialog(String title) {
        Bundle args = new Bundle();
        args.putString("title", title);
        DialogFragment ndf = new NoticeDialogFragment.NoticeDialogFragmentV4();
        ndf.setArguments(args);
        ndf.show(getChildFragmentManager(), "NoticeDialogFragment");
    }

    protected void openUpgradePro() {
        Intent intent = new Intent(getActivity(), UpgradeProActivity.class);
        startActivity(intent);
    }
}
