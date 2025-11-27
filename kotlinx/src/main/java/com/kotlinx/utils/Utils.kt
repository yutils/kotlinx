package com.kotlinx.utils

import android.os.Build
import android.os.Handler
import android.os.Looper
import com.kotlinx.Kotlinx
import com.kotlinx.extend.string
import com.kotlinx.extend.toFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File

object Utils {

}

/**外部扩展文件*/
object ExternalFile {
    // 缓存 HashMap，存储文件名和内容的映射
    private val cache = HashMap<String, String?>()

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

        var CameraMode: String
            get() = ExternalFile.readFile("摄像头模式.txt")?.ifEmpty { "1" } ?: let {//如果为null就保存一份到硬盘，如果为空串就返回默认值 1
                val default = "1"
                CameraMode =  default
                default
            }
            set(value) = ExternalFile.writeFile(value, "摄像头模式.txt")
    */
    fun readFile(fileName: String): String? {
        // 优先从缓存读取
        if (cache.containsKey(fileName)) {
            return cache[fileName]
        }
        // 缓存不存在，从文件读取
        val content = File(getDir(), fileName).string()
        // 如果 content 为 null，移除缓存，否则更新缓存
        if (content == null) {
            cache.remove(fileName)
        } else {
            cache[fileName] = content
        }
        return content
    }

    /**写入文本到外部储存*/
    /*
        var port: Int
        get() = ExternalFile.readFile("端口.txt")?.toInt() ?: 8080
        set(value) = ExternalFile.writeFile(value.toString(), "端口.txt")
     */
    fun writeFile(value: String?, fileName: String) {
        // 写入文件
        (value ?: "").toFile(getDir() + "/" + fileName)
        // 如果 value 为 null，从缓存中删除，否则更新缓存
        if (value == null) {
            cache.remove(fileName)
        } else {
            cache[fileName] = value
        }
    }

    /**清除缓存*/
    fun clearCache() {
        cache.clear()
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

//创建一个默认作用域
var defaultScope: CoroutineScope? = null
    get() {
        // 检查当前作用域是否有效（非空且未取消）
        if (field != null && field!!.coroutineContext[Job]?.isCancelled == false) {
            return field
        }
        // 无效则创建新作用域（添加默认调度器，如Dispatchers.Default）
        field = CoroutineScope(SupervisorJob() + Dispatchers.Default)
        return field
    }

/**
 * 主线程中运行,协程
 */
/*
//activity 生命周期自动管理作用域，退出时自动销毁
ui(activity.lifecycleScope){  }

//可以取消
val job = ui{  }
job.cancel()

//创建一个自定义作用域
val scope = CoroutineScope(SupervisorJob())
ui(scope){  }

//取消自定义作用域中全部协程
scope.cancel()
 */
fun ui(
    scope: CoroutineScope = defaultScope!!,
    block: suspend CoroutineScope.() -> Unit,
): Job = scope.launch(Dispatchers.Main) { block() }

/**
 * 子线程中运行,协程，睡眠不要用Thread.sleep()，应该用delay()
 */
/*
//activity 生命周期自动管理作用域，退出时自动销毁
io(activity.lifecycleScope){  }

//可以取消
val job = io{  }
job.cancel()

//有内循环
val job = io {
    while (this.isActive) {
        delay(1000)
        "协程:${Thread.currentThread().name}".logI()
    }
}
job.cancel()

//创建一个作用域
val scope = CoroutineScope(SupervisorJob())
io(scope){  }

//取消自定义作用域中全部协程
scope.cancel()
 */
fun io(
    scope: CoroutineScope = defaultScope!!,
    block: suspend CoroutineScope.() -> Unit,
): Job = scope.launch(Dispatchers.IO) { block() }


/**
 * 防抖延迟，默认每200毫秒，最多执行一次
 */
val debounceMap = mutableMapOf<String, Long>()
fun debounce(identifier: String? = null, millis: Long = 200, listener: () -> Unit) {
    val key = identifier ?: try {
        val stackTrace = Throwable().stackTrace
        val stack = stackTrace[2]
        "${stack.fileName}:${stack.lineNumber}"//类名+行号如：AbcActivity.kt:84
    } catch (_: Exception) {
        // 兜底用 hashCode（但这不是最佳）
        listener.hashCode().toString()
    }
    val lastTime = debounceMap[key] ?: 0L
    val currentTime = System.currentTimeMillis()
    if (currentTime - lastTime >= millis) {
        listener.invoke()
        debounceMap[key] = currentTime
    }
}

fun debounce(millis: Long = 200, listener: () -> Unit) {
    val key = try {
        val stackTrace = Throwable().stackTrace
        val stack = stackTrace[2]
        "${stack.fileName}:${stack.lineNumber}"//类名+行号如：AbcActivity.kt:84
    } catch (_: Exception) {
        // 兜底用 hashCode（但这不是最佳）
        listener.hashCode().toString()
    }
    val lastTime = debounceMap[key] ?: 0L
    val currentTime = System.currentTimeMillis()
    if (currentTime - lastTime >= millis) {
        listener.invoke()
        debounceMap[key] = currentTime
    }
}