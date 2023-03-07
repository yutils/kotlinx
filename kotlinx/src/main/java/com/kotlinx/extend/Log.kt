package com.kotlinx.extend

/**
 * Log扩展的log调用监听
 *
 */
/*
举例（日志记录本地文件）：
LogListener = listener@{ type, tag, msg, e ->
    if (tag=="StackTrace") return@listener
    if (type == Log.INFO) "${Date().format()}  $tag  $msg\r\n".addFile(File(Kotlinx.app?.getExternalFilesDir("")?.absolutePath + "/log.log"))
}
"测试文字".logI()
 */
var LogListener: ((Int, String, String, Throwable?) -> Unit)? = null