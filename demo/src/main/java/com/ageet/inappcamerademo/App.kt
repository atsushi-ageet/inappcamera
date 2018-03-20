package com.ageet.inappcamerademo

import android.app.Application
import com.ageet.inappcamera.InAppCamera
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        InAppCamera.setLogCallback { message ->
            Timber.tag("InAppCamera").d(message)
        }
    }
}
