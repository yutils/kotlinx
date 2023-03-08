package com.kotlinx.extend

import java.io.InputStream
import java.util.concurrent.TimeoutException

/**
 * 读取InputStream成String
 */
@Throws(Exception::class)
fun InputStream.readString(): String {
    return String(this.toBytes())
}

/**
 * 读取InputStream成ByteArray
 */
@Throws(Exception::class)
fun InputStream.toBytes(): ByteArray {
    return this.readOnce(Long.MAX_VALUE)
}

/**
 * 读取一次InputStream超时后抛出异常
 */
@Throws(Exception::class)
fun InputStream.readOnce(timeOut: Long = 1000 * 10): ByteArray {
    val startTime = System.currentTimeMillis()
    var count = 0
    while (count == 0 && System.currentTimeMillis() - startTime < timeOut) count = this.available() //获取真正长度
    if (System.currentTimeMillis() - startTime >= timeOut) {
        throw TimeoutException("读取超时,最大超时时间：${timeOut}")
    }
    val bytes = ByteArray(count)
    // 一定要读取count个数据，如果inputStream.read(bytes);可能读不完
    var readCount = 0 // 已经成功读取的字节的个数
    while (readCount < count) readCount += this.read(bytes, readCount, count - readCount)
    return bytes
}
