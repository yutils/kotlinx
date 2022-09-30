package com.kotlinx.utils

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object Utils {

}

/**
 * 主线程Handler
 */
val mainHandler: Handler = lazy { Handler(Looper.getMainLooper()) }.value

/**
 * 判断是否在UI线程
 */
fun isUI(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) mainHandler.looper.isCurrentThread else Looper.getMainLooper().thread === Thread.currentThread()
}

/**
 * 主线程中运行,协程
 */
fun ui(runnable: Runnable) {
    CoroutineScope(Dispatchers.Main).launch { withContext(Dispatchers.Main) { runnable.run() } }
}

/**
 * 主线程中运行,协程
 */
fun ui(block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.Main).launch { withContext(Dispatchers.Main, block) }
}


/**
 * 子线程中运行,协程
 */
fun io(runnable: Runnable) {
    CoroutineScope(Dispatchers.IO).launch { withContext(Dispatchers.IO) { runnable.run() } }
}

/**
 * 子线程中运行,协程，睡眠不要用Thread.sleep()，应该用delay()
 */
fun io(block: suspend CoroutineScope.() -> Unit) {
    CoroutineScope(Dispatchers.IO).launch { withContext(Dispatchers.IO,block) }
}