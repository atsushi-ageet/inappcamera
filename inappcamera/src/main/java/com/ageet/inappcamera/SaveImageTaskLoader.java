package com.ageet.inappcamera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;

import com.wonderkiln.camerakit.Size;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class SaveImageTaskLoader extends AsyncTaskLoaderBase<Void> {
    private static String LOG_TAG = "SaveImage";

    private byte[] data;
    private InAppCameraOptions options;

    SaveImageTaskLoader(Context context, byte[] data, InAppCameraOptions options) {
        super(context);
        this.data = data;
        this.options = options;
    }

    @Override
    public Void loadInBackground() {
        InAppCamera.log(LOG_TAG, "loadInBackground()");
        OutputStream outputStream = null;
        try {
            outputStream = getContext().getContentResolver().openOutputStream(options.getOutputUri());
            if (outputStream != null) {
                byte[] data = resampleIfNeeded(this.data);
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

    private byte[] resampleIfNeeded(byte[] data) {
        Size originalSize = retrieveImageSize(data);
        if (!needsResize(originalSize)) {
            if (options.hasMaxSize()) {
                return getCompressedDataFromBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
            } else {
                return data;
            }
        }
        int originalWidth = originalSize.getWidth();
        int originalHeight = originalSize.getHeight();
        int maxWidth = options.getMaxSizeInPixel().getWidth();
        int maxHeight = options.getMaxSizeInPixel().getHeight();
        float scaleByWidth = originalWidth / (float) maxWidth;
        float scaleByHeight = originalHeight / (float) maxHeight;
        float scale = Math.max(scaleByWidth, scaleByHeight);
        int inSampleSize = (int) Math.floor(scale);
        InAppCamera.log(LOG_TAG, "originalSize = %s, maxSize = %s, scale = %s", originalSize, options.getMaxSizeInPixel(), scale);
        Bitmap intermediateBitmap = createIntermediateBitmap(data, inSampleSize);
        InAppCamera.log(LOG_TAG, "intermediateBitmap width = %s, height = %s", intermediateBitmap.getWidth(), intermediateBitmap.getHeight());
        if (Math.max(intermediateBitmap.getWidth(), intermediateBitmap.getHeight()) == Math.max(maxWidth, maxHeight)) {
            return getCompressedDataFromBitmap(intermediateBitmap);
        }
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                intermediateBitmap,
                Math.min((int) Math.floor(originalWidth / scale), maxWidth),
                Math.min((int) Math.floor(originalHeight / scale), maxHeight),
                false);
        InAppCamera.log(LOG_TAG, "scaledBitmap width = %s, height = %s", scaledBitmap.getWidth(), scaledBitmap.getHeight());
        return getCompressedDataFromBitmap(scaledBitmap);
    }

    private boolean needsResize(Size size) {
        return options.hasMaxSize()
                && (size.getWidth() > options.getMaxSizeInPixel().getWidth() || size.getHeight() > options.getMaxSizeInPixel().getHeight());
    }

    private Size retrieveImageSize(byte[] data) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, bitmapOptions);
        return new Size(bitmapOptions.outWidth, bitmapOptions.outHeight);
    }

    private Bitmap createIntermediateBitmap(byte[] data, int inSampleSize) {
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = inSampleSize;
        return BitmapFactory.decodeByteArray(data, 0, data.length, bitmapOptions);
    }

    private byte[] getCompressedDataFromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, options.getJpegQuality(), outputStream);
        return outputStream.toByteArray();
    }

    static SaveImageTaskLoader init(LoaderManager manager, int id, byte[] data, LoaderManager.LoaderCallbacks<Void> callback) {
        Bundle args = new Bundle();
        args.putByteArray("data", data);
        return (SaveImageTaskLoader) manager.initLoader(id, args, callback);
    }
}
