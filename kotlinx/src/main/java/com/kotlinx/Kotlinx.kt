package com.kotlinx

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast

object Kotlinx {
    @SuppressLint("StaticFieldLeak")
    var app: Application? = null

    @SuppressLint("StaticFieldLeak")
    var toast: Toast? = null

    fun init(application: Application) {
        app = application
    }
}