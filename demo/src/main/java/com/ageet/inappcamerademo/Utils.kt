package com.ageet.inappcamerademo

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri

fun Context.createDrawableFromUri(uri: Uri): Drawable? {
    val inputStream = contentResolver.openInputStream(uri)
    inputStream?.use {
        return Drawable.createFromStream(inputStream, null)
    }
    return null
}
