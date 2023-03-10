package com.kotlinx

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import com.kotlinx.utils.TTS

object Kotlinx {
    @JvmStatic
    @SuppressLint("StaticFieldLeak")
    var app: Application? = null

    @JvmStatic
    @SuppressLint("StaticFieldLeak")
    var toast: Toast? = null

    @JvmStatic
    fun init(application: Application) {
        app = application
    }

    @JvmStatic
    fun destroy() {
        app = null
        toast = null
        TTS.destroy()
    }
}