package com.kotlinx.extend

import java.io.InputStream
import java.util.concurrent.TimeoutException

/**
 * 读取 InputStream 成 String
 */
@Throws(Exception::class)
fun InputStream.readString(): String {
    return String(this.toBytes())
}

/**
 * 读取 InputStream 全部内容成 ByteArray（直到 EOF）。
 */
@Throws(Exception::class)
fun InputStream.toBytes(): ByteArray {
    return this.readBytes()
}

/**
 * 等待有可读数据后读取「当前 available 这一批」；超时抛 [TimeoutException]。
 *
 * 说明：不把 [available] 当作流总长度；仅用于「一次读当前缓冲」。
 * 读到 EOF（[read] 返回 -1）时提前结束，避免死循环。
 */
@Throws(Exception::class)
fun InputStream.readOnce(timeOut: Long = 1000 * 10): ByteArray {
    val startTime = System.currentTimeMillis()
    var count = 0
    while (count == 0) {
        if (System.currentTimeMillis() - startTime >= timeOut) {
            throw TimeoutException("读取超时,最大超时时间：${timeOut}")
        }
        count = this.available().coerceAtLeast(0)
        if (count == 0) {
            Thread.sleep(1) // 避免 available==0 时空转占满 CPU
        }
    }
    val bytes = ByteArray(count)
    var readCount = 0
    while (readCount < count) {
        val n = this.read(bytes, readCount, count - readCount)
        if (n < 0) break // EOF
        if (n == 0) break
        readCount += n
    }
    return if (readCount == count) bytes else bytes.copyOf(readCount)
}
