package com.ageet.inappcamera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.io.File;

public class PreviewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Drawable> {
    private static String LOG_TAG = "Preview";

    private Uri outputUri = null;
    private ImageView imageView = null;
    private ContentLoadingProgressBar progressBar = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.inappcamera_activity_preview);

        Intent intent = getIntent();
        outputUri = intent != null ? intent.<Uri>getParcelableExtra(InAppCamera.EXTRA_OUTPUT) : null;
        if (outputUri == null) {
            InAppCamera.log(LOG_TAG, "outputUri is null");
            cancel();
            return;
        }
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);

        LoadImageTaskLoader loader = (LoadImageTaskLoader) getSupportLoaderManager().initLoader(0, null, this);
        if (!loader.isFinished()) {
            progressBar.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        imageView.setImageDrawable(null);
    }

    @NonNull
    @Override
    public Loader<Drawable> onCreateLoader(int id, @Nullable Bundle args) {
        InAppCamera.log(LOG_TAG, "onCreateLoader()");
        return new LoadImageTaskLoader(this, outputUri);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Drawable> loader, Drawable data) {
        InAppCamera.log(LOG_TAG, "onLoadFinished()");
        progressBar.hide();
        imageView.setImageDrawable(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Drawable> loader) {
    }

    public void onOkClick(View view) {
        InAppCamera.log(LOG_TAG, "onOkClick()");
        ok();
    }

    public void onDeleteClick(View view) {
        InAppCamera.log(LOG_TAG, "onDeleteClick()");
        File file = new File(outputUri.getPath());
        if (file.exists() && !file.delete()) {
            InAppCamera.log(LOG_TAG, "Could not delete file(path = %s)", outputUri.getPath());
        }
        cancel();
    }

    private void ok() {
        Intent intent = new Intent();
        intent.putExtra(InAppCamera.EXTRA_OUTPUT, outputUri);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    private void cancel() {
        setResult(Activity.RESULT_CANCELED);
        finish();
    }
}
