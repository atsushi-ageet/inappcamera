package com.ageet.inappcamera;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

class LoadImageTaskLoader extends AsyncTaskLoaderBase<Drawable> {
    private static String LOG_TAG = "LoadImage";

    private Uri uri;

    LoadImageTaskLoader(Context context, Uri uri) {
        super(context);
        this.uri = uri;
    }

    @Override
    public Drawable loadInBackground() {
        InAppCamera.log(LOG_TAG, "loadInBackground() uri = %s", uri);
        InputStream inputStream = null;
        try {
            inputStream = getContext().getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                return Drawable.createFromStream(inputStream, null);
            }
        } catch (FileNotFoundException e) {
            InAppCamera.log(LOG_TAG, e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    InAppCamera.log(LOG_TAG, e);
                }
            }
        }
        return null;
    }
}
