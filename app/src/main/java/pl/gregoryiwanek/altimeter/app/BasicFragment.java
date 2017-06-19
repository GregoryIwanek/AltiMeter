package pl.gregoryiwanek.altimeter.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;

import pl.gregoryiwanek.altimeter.app.upgradepro.UpgradeProActivity;
import pl.gregoryiwanek.altimeter.app.utils.NoticeDialogFragment;
import pl.gregoryiwanek.altimeter.app.utils.NoticeDialogFragment.NoticeDialogFragmentV4.NoticeDialogListener;

public class BasicFragment extends Fragment implements NoticeDialogListener{
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
