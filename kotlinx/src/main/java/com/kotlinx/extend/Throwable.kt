package com.kotlinx.extend

/** 最内层 cause（循环引用时返回自身）。 */
val Throwable.rootCause: Throwable
    get() {
        var current: Throwable = this
        val seen = HashSet<Throwable>()
        while (current.cause != null && current.cause != current) {
            if (!seen.add(current)) break
            current = current.cause!!
        }
        return current
    }

/** 单行摘要，便于 Toast / 日志。 */
fun Throwable.oneLineMessage(): String {
    val msg = localizedMessage ?: message
    val simple = javaClass.simpleName.ifEmpty { javaClass.name }
    return if (msg.isNullOrBlank()) simple else "$simple: $msg"
}
