package com.kotlinx.utils

import android.os.Build
import android.os.Handler
import android.os.Looper
import com.kotlinx.Kotlinx
import com.kotlinx.extend.string
import com.kotlinx.extend.toFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

object Utils {

}

/**外部扩展文件*/
object ExternalFile {
    /**实际目录:Android/data/包名/files/
     * 返回的字符串末尾没有 /
     */
    fun getDir(): String {
        return Kotlinx.app.getExternalFilesDir("")?.absolutePath
            ?: throw IllegalStateException("无法访问外部文件目录")
    }

    /**外部储存读取文件成String*/
    /*
        var ip: String
        get() = ExternalFile.readFile("服务器IP.txt") ?: "127.0.0.1"
        set(value) = ExternalFile.writeFile(value, "服务器IP.txt")
    */
    fun readFile(fileName: String): String? {
        return File(getDir(), fileName).string()
    }

    /**写入文本到外部储存*/
    /*
        var port: Int
        get() = ExternalFile.readFile("端口.txt")?.toInt() ?: 8080
        set(value) = ExternalFile.writeFile(value.toString(), "端口.txt")
     */
    fun writeFile(value: String?, fileName: String) {
        (value ?: "").toFile(getDir() + "/" + fileName)
    }
}

/**
 * 主线程Handler
 */
val mainHandler: Handler by lazy { Handler(Looper.getMainLooper()) }

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
    CoroutineScope(Dispatchers.IO).launch { withContext(Dispatchers.IO, block) }
}

/**
 * 防抖延迟，默认每200毫秒，最多执行一次
 */
val debounceMap = mutableMapOf<String, Long>()
fun debounce(identifier: String? = null, millis: Long = 200, listener: () -> Unit) {
    //使用 listener.hashCode().toString() 作为唯一标识符，虽然在大多数情况下有效，但如果 listener 是匿名函数或者 lambda 表达式，它们的哈希码可能会重复。
    val key = identifier ?: listener.hashCode().toString()
    val lastTime = debounceMap[key] ?: 0L
    val currentTime = System.currentTimeMillis()
    if (currentTime - lastTime >= millis) {
        listener.invoke()
        debounceMap[key] = currentTime
    }
}