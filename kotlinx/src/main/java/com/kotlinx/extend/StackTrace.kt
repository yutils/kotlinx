package com.kotlinx.extend

import android.util.Log
import java.util.*

fun <T> T.showStackTrace(type: Int = Log.INFO): T {
    getLine(1)?.let {
        when (type) {
            Log.VERBOSE -> it.logV("StackTrace")
            Log.DEBUG -> it.logD("StackTrace")
            Log.INFO -> it.logI("StackTrace")
            Log.WARN -> it.logW("StackTrace")
            Log.ERROR -> it.logE("StackTrace")
            else -> {}
        }
    }
    return this
}


/**
 * 获取堆栈中的java行
 *
 * @param targetElement 堆栈跟踪
 * @return java类和行数
 */
private fun getJavaFileName(targetElement: StackTraceElement?): String {
    val fileName = targetElement!!.fileName
    if (fileName != null) return fileName
    var className = targetElement.className
    val classNameInfo = className.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    if (classNameInfo.isNotEmpty()) className = classNameInfo[classNameInfo.size - 1]
    val index = className.indexOf('$')
    if (index != -1) className = className.substring(0, index)
    return "$className.java"
}

/**
 * 返回调用此方法堆栈跟踪的行数
 *
 * @param lineDeviation 偏移行，1就是向上级方法偏移一行，2就是向上级偏移两行
 * @return 线程名，类名，行数
 */
fun getLine(lineDeviation: Int): String? {
    val stackTrace = Throwable().stackTrace
    val targetElement = try {
        stackTrace[1 + lineDeviation]
    } catch (e: ArrayIndexOutOfBoundsException) {
        """错误！堆栈跟踪偏移行数组越界。 ${e.message}""".trimIndent().logE(t = e)
        return null
    }
    val fileName = getJavaFileName(targetElement)
    return Formatter()
        .format(
            "%s, %s.%s(%s:%d)",
            Thread.currentThread().name,
            targetElement?.className,
            targetElement?.methodName,
            fileName,
            targetElement?.lineNumber
        )
        .toString()
}