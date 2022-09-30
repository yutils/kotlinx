package com.kotlinx.extend

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.nio.charset.Charset

/**
 * 读取文件并返回ByteArray
 */
fun File.toByteArray(): ByteArray? {
    try {
        FileInputStream(this).use { stream ->
            ByteArrayOutputStream(this.length().toInt()).use { out ->
                val b = ByteArray(1024 * 4)
                var n: Int
                while (stream.read(b).also { n = it } != -1) out.write(b, 0, n)
                return out.toByteArray()
            }
        }
    } catch (ignored: IOException) {
    }
    return null
}

/** 读取文件并返回String*/
/*举例 var s= File("D:/abc.txt").toString() */
fun File.toString(charset: Charset = Charset.defaultCharset()): String? {
    return this.toByteArray()?.let { String(it, charset) }
}