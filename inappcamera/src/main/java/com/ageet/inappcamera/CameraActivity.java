package com.ageet.inappcamera;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewStub;
import android.view.Window;
import android.view.WindowManager;

import com.wonderkiln.camerakit.CameraKitEventCallback;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraView;

public class CameraActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Void>, CameraKitEventCallback<CameraKitImage> {
    static {
        System.loadLibrary("yuvOperator");
    }
    private static String LOG_TAG = "Camera";

    private static class NonConfigurationInstances {
        private byte[] jpeg;
    }

    private boolean isRetaining = false;
    private byte[] jpeg = null;
    private ProgressDialogFragment progress = null;
    private CameraView cameraView = null;
    private InAppCameraOptions options;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.inappcamera_activity_camera);

        options = getIntent().getParcelableExtra(InAppCamera.EXTRA_OPTIONS);
        cameraView = findViewById(R.id.cameraView);
        if (options.getCameraOverlayViewLayoutId() != View.NO_ID) {
            ViewStub cameraOverlayViewStub = findViewById(R.id.cameraOverlayViewStub);
            cameraOverlayViewStub.setLayoutResource(options.getCameraOverlayViewLayoutId());
            cameraOverlayViewStub.inflate();
        }

        byte[] jpeg = restoreJpeg();
        if (getSupportLoaderManager().getLoader(0) != null || jpeg != null) {
            SaveImageTaskLoader loader = SaveImageTaskLoader.init(getSupportLoaderManager(), 0, jpeg, this);
            if (!loader.isFinished()) {
                progress = ProgressDialogFragment.getOrShow(getSupportFragmentManager());
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
    }

    @Override
    protected void onPause() {
        cameraView.stop();
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_CANCELED) {
            setResult(resultCode, data);
            finish();
        }
    }

    @Override
    public NonConfigurationInstances onRetainCustomNonConfigurationInstance() {
        InAppCamera.log(LOG_TAG, "onRetainCustomNonConfigurationInstance()");
        isRetaining = true;
        NonConfigurationInstances nci = new NonConfigurationInstances();
        nci.jpeg = jpeg;
        return nci;
    }

    @Override
    public void callback(final CameraKitImage cameraKitImage) {
        InAppCamera.log(LOG_TAG, "Took a picture.");
        cameraView.stop();
        jpeg = cameraKitImage.getJpeg();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isRetaining) {
                    SaveImageTaskLoader.init(getSupportLoaderManager(), 0, jpeg, CameraActivity.this);
                    try {
                        progress = ProgressDialogFragment.getOrShow(getSupportFragmentManager());
                    } catch (IllegalStateException e) {
                        InAppCamera.log(LOG_TAG, e);
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public Loader<Void> onCreateLoader(int id, @Nullable Bundle args) {
        InAppCamera.log(LOG_TAG, "onCreateLoader()");
        byte[] data = new byte[0];
        if (args != null) {
            data = args.getByteArray("data");
        }
        return new SaveImageTaskLoader(this, data, options.getOutputUri());
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Void> loader, Void result) {
        InAppCamera.log(LOG_TAG, "onLoadFinished()");
        if (progress != null) {
            progress.dismiss();
        }
        if (options.isSkipPreview()) {
            ok();
        } else {
            preview();
        }
        jpeg = null;
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Void> loader) {
        InAppCamera.log(LOG_TAG, "onLoaderReset()");
    }

    public void onCaptureClick(View view) {
        cameraView.captureImage(this);
    }

    private byte[] restoreJpeg() {
        NonConfigurationInstances nci = (NonConfigurationInstances) getLastCustomNonConfigurationInstance();
        if (nci != null && nci.jpeg != null) {
            InAppCamera.log(LOG_TAG, "Restore jpeg");
            return nci.jpeg;
        }
        return null;
    }

    private void preview() {
        Intent intent = new Intent(this, PreviewActivity.class)
                .putExtra(InAppCamera.EXTRA_OPTIONS, options);
        startActivityForResult(intent, 0);
        getSupportLoaderManager().destroyLoader(0);
    }

    private void ok() {
        Intent intent = new Intent()
                .putExtra(InAppCamera.EXTRA_OUTPUT, options.getOutputUri());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
