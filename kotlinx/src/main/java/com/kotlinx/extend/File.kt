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
fun File.string(charset: Charset = Charset.defaultCharset()): String? {
    return this.toByteArray()?.let { String(it, charset) }
}

/** 删除文件或者目录*/
/*举例： File(Kotlinx.app?.getExternalFilesDir("")?.absolutePath + "/log.log").delFileOrDir()*/
fun File.delFileOrDir(): Boolean {
    return if (this.isFile) {
        this.delete()
    } else if (this.isDirectory) {
        if (this.listFiles() == null) return false
        if (this.listFiles()?.isNotEmpty() == true) {
            val files = this.listFiles() ?: return false
            for (i in files.indices) {
                if (files[i].isDirectory) {
                    File(files[i].absolutePath).delFileOrDir()
                }
                files[i].delete()
            }
        }
        this.delete()
    } else {
        false
    }
}

