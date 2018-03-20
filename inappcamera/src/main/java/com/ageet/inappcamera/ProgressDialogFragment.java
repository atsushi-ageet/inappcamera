package com.ageet.inappcamera;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;

public class ProgressDialogFragment extends DialogFragment {
    private static String DEFAULT_FRAGMENT_TAG = "progress";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getActivity(), getTheme());
        dialog.setMessage(getString(R.string.inappcamera_processing));
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        return dialog;
    }

    @NonNull
    public static ProgressDialogFragment getOrShow(FragmentManager manager) {
        return getOrShow(manager, DEFAULT_FRAGMENT_TAG);
    }

    @NonNull
    public static ProgressDialogFragment getOrShow(FragmentManager manager, String tag) {
        ProgressDialogFragment progress = (ProgressDialogFragment) manager.findFragmentByTag(tag);
        if (progress == null) {
            progress = new ProgressDialogFragment();
            progress.show(manager, tag);
        }
        return progress;
    }
}
