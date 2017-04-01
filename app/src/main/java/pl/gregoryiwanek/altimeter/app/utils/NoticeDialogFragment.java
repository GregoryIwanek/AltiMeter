package pl.gregoryiwanek.altimeter.app.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Consists class responsible for delivering popup notice message.
 * Depending on used type of Fragment ({@link android.support.v4.app.Fragment}
 * or {@link android.app.Fragment}), there is requirement of use different subclass of this class.
 */
public class NoticeDialogFragment {

    /**
     * Notice dialog to use with fragments "import android.support.v4.app.Fragment" (support.V4 !!!)
     */
    public static class NoticeDialogFragmentV4 extends DialogFragment {

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

        private void setCallbackMessage(String message) {
            mMessage = message;
        }

        public interface NoticeDialogListener {

            void onDialogPositiveClick(String callbackCode);
        }
    }

    /**
     * Notice Dialog for use with fragments "import android.app.Fragment" (not support.V4, just Fragment!!!)
     */
    public static class NoticeDialogFragmentApp extends android.app.DialogFragment {

        private String mTitle;

        @SuppressWarnings("deprecation")
        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
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
            builder.setMessage(mTitle);
            return builder.create();
        }
    }
}
