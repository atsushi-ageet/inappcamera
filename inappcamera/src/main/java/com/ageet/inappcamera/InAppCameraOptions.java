package com.ageet.inappcamera;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.View;

public class InAppCameraOptions implements Parcelable {
    private Uri outputUri;
    private boolean skipPreview;
    private int cameraOverlayViewLayoutId;

    private InAppCameraOptions(InAppCameraOptions.Builder builder) {
        outputUri = builder.outputUri;
        skipPreview = builder.skipPreview;
        cameraOverlayViewLayoutId = builder.cameraOverlayViewLayoutId;
    }

    private InAppCameraOptions(Parcel in) {
        outputUri = in.readParcelable(Uri.class.getClassLoader());
        skipPreview = in.readByte() != 0;
        cameraOverlayViewLayoutId = in.readInt();
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

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(outputUri, flags);
        dest.writeByte((byte) (skipPreview ? 1 : 0));
        dest.writeInt(cameraOverlayViewLayoutId);
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

        public InAppCameraOptions build() {
            return new InAppCameraOptions(this);
        }
    }
}
