package com.ageet.inappcamerademo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.ageet.inappcamera.InAppCamera
import com.ageet.inappcamera.InAppCameraOptions

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
    }

    fun onCameraClick(view: View) {
        val file = createTempFile(prefix = "capture", directory = cacheDir)
        val uri = Uri.fromFile(file)
        val options = InAppCameraOptions.Builder(uri)
                .cameraOverlayViewLayoutId(R.layout.frame)
                .build()
        val intent = InAppCamera.createIntent(this, options)
        startActivityForResult(intent, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Timber.d("requestCode = $requestCode, resultCode = $resultCode, data = $data")
        if (resultCode == Activity.RESULT_OK && data != null) {
            val imageUri = InAppCamera.getOutputFromResult(data)
            Timber.d("imageUri = $imageUri")
            imageView.setImageDrawable(createDrawableFromUri(imageUri))
        }
    }
}
