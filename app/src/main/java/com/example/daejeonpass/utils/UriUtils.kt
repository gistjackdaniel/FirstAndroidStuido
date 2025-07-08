package com.example.daejeonpass.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

fun Context.drawablePngToUri(drawableResId: Int, fileName: String): Uri {
    val file = File(cacheDir, fileName)

    if (!file.exists()) {
        val inputStream = resources.openRawResource(drawableResId)
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
    }

    return FileProvider.getUriForFile(
        this,
        "com.example.daejeonpass.fileprovider",
        file
    )
}