package com.ageet.inappcamera;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

abstract class AsyncTaskLoaderBase<T> extends AsyncTaskLoader<T> {
    private T result;
    private boolean isFinished = false;

    AsyncTaskLoaderBase(final Context context) {
        super(context);
        onContentChanged();
    }

    @Override
    protected void onStartLoading() {
        if (takeContentChanged()) {
            forceLoad();
        } else if (isFinished) {
            deliverResult(result);
        }
    }

    @Override
    public void deliverResult(final T data) {
        result = data;
        isFinished = true;
        super.deliverResult(data);
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if (isFinished) {
            onReleaseResources(result);
            result = null;
            isFinished = false;
        }
    }

    void onReleaseResources(T data) {
    }

    boolean isFinished() {
        return isFinished;
    }
}