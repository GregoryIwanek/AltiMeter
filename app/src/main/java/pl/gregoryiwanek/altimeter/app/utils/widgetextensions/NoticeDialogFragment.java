package pl.gregoryiwanek.altimeter.app.utils.widgetextensions;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import pl.gregoryiwanek.altimeter.app.utils.Constants;

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
        private int mMessageCode;
        private NoticeDialogListener mListener;

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);

            // try attach listener to a parent Fragment, if this object is attached directly
            // to a parent activity it will return null
            try {
                mListener = (NoticeDialogListener) getParentFragment();
            } catch (ClassCastException e) {
                e.printStackTrace();
            }

            // try attach listener to parent Activity, only if this object ( window) is attached
            // directly to the parent activity, not a Fragment
            if (mListener == null) {
                try {
                    mListener = (NoticeDialogListener) getActivity();
                } catch (ClassCastException e) {
                    e.printStackTrace();
                }
            }

            getBundleArguments();
        }

        private void getBundleArguments() {
            Bundle args = getArguments();
            mTitle = args.getString("title");
            mMessageCode = args.getInt("code");
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(mTitle)
                    .setPositiveButton(Constants.POSITIVE, (dialog, which) -> {
                        setCallbackMessage(mMessageCode);
                        mListener.onDialogPositiveClick(mMessageCode);
                    })
                    .setNegativeButton(Constants.CANCEL, (dialog, which) -> {
                        //nothing happens, action canceled
                    });
            return builder.create();
        }

        private void setCallbackMessage(int messageCode) {
            this.mMessageCode = messageCode;
        }

        public interface NoticeDialogListener {

            void onDialogPositiveClick(int callbackCode);
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
