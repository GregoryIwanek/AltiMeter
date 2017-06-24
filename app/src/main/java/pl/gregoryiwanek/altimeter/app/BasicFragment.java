package pl.gregoryiwanek.altimeter.app;

import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.View;

import pl.gregoryiwanek.altimeter.app.upgradepro.UpgradeProActivity;
import pl.gregoryiwanek.altimeter.app.utils.NoticeDialogFragment;
import pl.gregoryiwanek.altimeter.app.utils.NoticeDialogFragment.NoticeDialogFragmentV4.NoticeDialogListener;
import pl.gregoryiwanek.altimeter.app.utils.ThemeAttributesPicker;

public abstract class BasicFragment extends Fragment implements NoticeDialogListener {

    private ThemeAttributesPicker themePicker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        themePicker = new ThemeAttributesPicker();
    }

    protected int getThemeAttrColor(int attrId) {
        return themePicker.getColor(getActivity(), attrId);
    }


    /**
     * Sets {@link ColorFilter} to a {@link View}. {@link View} has to be non transparent in order to change a color.
     * Method recommended if {@link PorterDuff.Mode} is desired as default and won't be modified.
     * @param view {@link View} to set color filter for;
     * @param colorId representation of color as {@link int}
     */
    protected void setViewColorFilter(View view, int colorId) {
        setViewColorFilter(view, colorId, PorterDuff.Mode.MULTIPLY);
    }
    /**
     * Sets {@link ColorFilter} to a {@link View}. {@link View} has to be non transparent in order to change a color.
     * Method recommended if {@link PorterDuff.Mode} will be modified.
     * @param view {@link View} to set color filter for;
     * @param colorId representation of color as {@link int}
     * @param mode {@link PorterDuff.Mode} of the color change; {@link PorterDuff.Mode.MULTIPLY} recommended;
     */
    protected void setViewColorFilter(View view, int colorId, PorterDuff.Mode mode) {
            view.getBackground().setColorFilter(colorId, mode);
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
