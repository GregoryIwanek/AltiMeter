package pl.gregoryiwanek.altimeter.app;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;

import java.util.List;

import pl.gregoryiwanek.altimeter.app.upgradepro.UpgradeProActivity;
import pl.gregoryiwanek.altimeter.app.utils.NoticeDialogFragment;
import pl.gregoryiwanek.altimeter.app.utils.NoticeDialogFragment.NoticeDialogFragmentV4.NoticeDialogListener;
import pl.gregoryiwanek.altimeter.app.utils.ThemeManager;

public abstract class BasicFragment extends Fragment implements NoticeDialogListener {

    private ThemeManager themePicker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themePicker = new ThemeManager();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setRootViewBackgroundColor(view);
    }

    private void setRootViewBackgroundColor(View rootView) {
        applyThemeColorToSingleView(rootView, R.attr.colorRootBackground);
    }

    protected int getThemeAttrColor(int attrId) {
        return themePicker.getColor(getActivity(), attrId);
    }

    /**
     * Apply color filter to a multiple views.
     * @param viewList list of views to set color filter on;
     * @param attrId color attribute id; R.attr.name;
     */
    protected void applyThemeColorsToMultipleViews(List<View> viewList, int attrId) {
        int colorId = getThemeAttrColor(attrId);
        for (View view : viewList) {
            setViewColor(view, colorId);
        }
    }

    /**
     * Apply color filter to a single view.
     * @param view view to set color filter on;
     * @param attrId color attribute id; format R.attr.name;
     */
    protected void applyThemeColorToSingleView(View view, int attrId) {
        int colorId = getThemeAttrColor(attrId);
        setViewColor(view, colorId);
    }


    private void setViewColor(View view, int colorId) {
        setViewColor(view, colorId, PorterDuff.Mode.MULTIPLY);
    }

    private void setViewColor(View view, int colorId, PorterDuff.Mode mode) {
        if (isBackgroundInvisible(view)) {
            setViewBackgroundColor(view, colorId);
        } else {
            setViewColorFilter(view, colorId, mode);
        }
    }

    private void setViewBackgroundColor(View view, int colorId) {
        view.setBackgroundColor(colorId);
    }

    private void setViewColorFilter(View view, int colorId, PorterDuff.Mode mode) {
        view.getBackground().setColorFilter(colorId, mode);
    }

    private boolean isBackgroundInvisible(View view) {
        return view.getBackground() == null || !view.getBackground().isVisible();
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
