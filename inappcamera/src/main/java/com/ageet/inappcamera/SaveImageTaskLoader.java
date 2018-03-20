package com.ageet.inappcamera;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import java.io.IOException;
import java.io.OutputStream;

class SaveImageTaskLoader extends AsyncTaskLoaderBase<Void> {
    private static String LOG_TAG = "SaveImage";

    private byte[] data;
    private Uri uri;

    SaveImageTaskLoader(Context context, byte[] data, Uri uri) {
        super(context);
        this.data = data;
        this.uri = uri;
    }

    @Override
    public Void loadInBackground() {
        InAppCamera.log(LOG_TAG, "loadInBackground()");
        OutputStream outputStream = null;
        try {
            outputStream = getContext().getContentResolver().openOutputStream(uri);
            if (outputStream != null) {
                outputStream.write(data);
                outputStream.flush();
            }
        } catch (IOException e) {
            InAppCamera.log(LOG_TAG, e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    InAppCamera.log(LOG_TAG, e);
                }
            }
        }
        return null;
    }

    static SaveImageTaskLoader init(LoaderManager manager, int id, byte[] data, LoaderManager.LoaderCallbacks<Void> callback) {
        Bundle args = new Bundle();
        args.putByteArray("data", data);
        return (SaveImageTaskLoader) manager.initLoader(id, args, callback);
    }
}
