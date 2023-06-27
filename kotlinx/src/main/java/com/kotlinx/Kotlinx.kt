package com.kotlinx

import android.annotation.SuppressLint
import android.app.Application
import android.widget.Toast
import com.kotlinx.utils.TTS

object Kotlinx {
    @JvmStatic
    @SuppressLint("StaticFieldLeak")
    var app: Application = Application()
        get() {
            if (!isInit) throw RuntimeException("Kotlinx.app未初始化，请调用Kotlinx.init(application)")
            return field
        }

    //当前app是否已经初始
    private var isInit = false

    @JvmStatic
    @SuppressLint("StaticFieldLeak")
    var toast: Toast? = null

    @JvmStatic
    fun init(application: Application) {
        app = application
        isInit = true
    }

    @JvmStatic
    fun destroy() {
        toast = null
        TTS.destroy()
    }
}