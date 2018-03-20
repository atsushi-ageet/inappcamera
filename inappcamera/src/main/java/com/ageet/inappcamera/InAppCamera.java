package com.ageet.inappcamera;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.Locale;

public class InAppCamera {
    public interface LogCallback {
        void onLog(String message);
    }

    private static LogCallback logCallback = null;

    public static void setLogCallback(LogCallback callback) {
        InAppCamera.logCallback = callback;
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context, @NonNull Uri outputUri) {
        return createIntent(context, outputUri, false);
    }

    @NonNull
    public static Intent createIntent(@NonNull Context context, @NonNull Uri outputUri, boolean skipPreview) {
        return new Intent(context, CameraActivity.class)
                .putExtra(EXTRA_OUTPUT, outputUri)
                .putExtra(EXTRA_SKIP_PREVIEW, skipPreview);
    }

    @NonNull
    public static Uri getOutputFromResult(Intent intent) {
        Uri output = intent.getParcelableExtra(EXTRA_OUTPUT);
        return output != null ? output : Uri.EMPTY;
    }

    static String EXTRA_OUTPUT = "output";
    static String EXTRA_SKIP_PREVIEW = "skipPreview";

    static void log(String tag, Throwable t) {
        log(tag, "%s", Log.getStackTraceString(t));
    }

    static void log(String tag, String message, Object... args) {
        if (logCallback != null) {
            String prefix = String.format(Locale.US, "[%s] ", tag);
            logCallback.onLog(prefix + String.format(Locale.US, message, args));
        }
    }
}
