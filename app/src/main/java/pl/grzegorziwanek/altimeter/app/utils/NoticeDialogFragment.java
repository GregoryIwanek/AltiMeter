package pl.grzegorziwanek.altimeter.app.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import pl.grzegorziwanek.altimeter.app.data.Constants;

/**
 * Created by Grzegorz Iwanek on 10.02.2017.
 */

public class NoticeDialogFragment extends DialogFragment {

    private String mTitle;
    private String mMessage;
    private NoticeDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (NoticeDialogListener) getParentFragment();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }

        getBundleArguments();
    }

    private void getBundleArguments() {
        Bundle args = getArguments();
        mTitle = args.getString("title");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(mTitle)
                .setPositiveButton(Constants.POSITIVE, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setCallbackMessage(mTitle);
                        mListener.onDialogPositiveClick(mMessage);
                    }
                })
                .setNegativeButton(Constants.CANCEL, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //nothing happens, action canceled
                    }
                });
        return builder.create();
    }

    public void setCallbackMessage(String message) {
        mMessage = message;
    }

    public interface NoticeDialogListener {

        void onDialogPositiveClick(String callbackCode);
    }
}
