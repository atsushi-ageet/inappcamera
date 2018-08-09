package com.ageet.inappcamera;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;

import com.wonderkiln.camerakit.Size;

public class InAppCameraOptions implements Parcelable {
    private Uri outputUri;
    private boolean skipPreview;
    private int cameraOverlayViewLayoutId;
    private Size maxSizeInPixel;
    private int jpegQuality;

    private InAppCameraOptions(InAppCameraOptions.Builder builder) {
        outputUri = builder.outputUri;
        skipPreview = builder.skipPreview;
        cameraOverlayViewLayoutId = builder.cameraOverlayViewLayoutId;
        maxSizeInPixel = builder.maxSizeInPixel;
        jpegQuality = builder.jpegQuality;
    }

    private InAppCameraOptions(Parcel in) {
        outputUri = in.readParcelable(Uri.class.getClassLoader());
        skipPreview = in.readByte() != 0;
        cameraOverlayViewLayoutId = in.readInt();
        maxSizeInPixel = new Size(
                in.readInt(),
                in.readInt());
        jpegQuality = in.readInt();
    }

    public @NonNull Uri getOutputUri() {
        return outputUri;
    }

    public boolean isSkipPreview() {
        return skipPreview;
    }

    public int getCameraOverlayViewLayoutId() {
        return cameraOverlayViewLayoutId;
    }

    public Size getMaxSizeInPixel() {
        return maxSizeInPixel;
    }

    public int getJpegQuality() {
        return jpegQuality;
    }

    public boolean hasMaxSize() {
        return maxSizeInPixel != null;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(outputUri, flags);
        dest.writeByte((byte) (skipPreview ? 1 : 0));
        dest.writeInt(cameraOverlayViewLayoutId);
        dest.writeInt(maxSizeInPixel.getWidth());
        dest.writeInt(maxSizeInPixel.getHeight());
        dest.writeInt(jpegQuality);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<InAppCameraOptions> CREATOR = new Creator<InAppCameraOptions>() {
        @Override
        public InAppCameraOptions createFromParcel(Parcel in) {
            return new InAppCameraOptions(in);
        }

        @Override
        public InAppCameraOptions[] newArray(int size) {
            return new InAppCameraOptions[size];
        }
    };

    public static class Builder {
        private Uri outputUri;
        private boolean skipPreview = false;
        private int cameraOverlayViewLayoutId = View.NO_ID;
        private Size maxSizeInPixel;
        private int jpegQuality = 100;

        public Builder(@NonNull Uri outputUri) {
            this.outputUri = outputUri;
        }

        public Builder skipPreview(boolean skipPreview) {
            this.skipPreview = skipPreview;
            return this;
        }

        public Builder cameraOverlayViewLayoutId(@LayoutRes int cameraOverlayViewLayoutId) {
            this.cameraOverlayViewLayoutId = cameraOverlayViewLayoutId;
            return this;
        }

        public Builder maxSizeInPixel(int width, int height) {
            this.maxSizeInPixel = new Size(width, height);
            return this;
        }

        public Builder jpegQuality(int jpegQuality) {
            this.jpegQuality = jpegQuality;
            return this;
        }

        public InAppCameraOptions build() {
            return new InAppCameraOptions(this);
        }
    }
}
