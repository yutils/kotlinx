package com.kotlinx.extend

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64
import java.io.*

/**
 * base64的ByteArray转码返回String
 */
fun ByteArray.toBase64EncodeToString(): String {
    try {
        return Base64.encodeToString(this, Base64.NO_WRAP) //不自动换行
    } catch (e: Exception) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) return java.util.Base64.getEncoder().encodeToString(this)
    }
    return Base64.encodeToString(this, Base64.NO_WRAP) //不自动换行
}

/**
 * base64的ByteArray转码返回ByteArray
 */
fun ByteArray.toBase64Encode(): ByteArray {
    try {
        return Base64.encode(this, Base64.NO_WRAP) //不自动换行
    } catch (e: Exception) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) return java.util.Base64.getEncoder().encode(this)
    }
    return Base64.encode(this, Base64.NO_WRAP) //不自动换行
}

/**
 * base64解码 返回ByteArray
 */
fun ByteArray.toBase64Decode(): ByteArray {
    try {
        return Base64.decode(this, Base64.NO_WRAP) //不自动换行
    } catch (e: Exception) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) return java.util.Base64.getDecoder().decode(this)
    }
    return Base64.decode(this, Base64.NO_WRAP) //不自动换行
}

/**
 * ByteArray转换成bitmap
 */
fun ByteArray.toBitmap(): Bitmap? {
    return if (this.isNotEmpty()) {
        BitmapFactory.decodeByteArray(this, 0, this.size)
    } else {
        null
    }
}

/**
 * ByteArray转换成file并写入储存
 */
fun ByteArray.toFile(file: File): Boolean {
    file.parentFile?.let {
        if (!it.exists()) it.mkdirs() // 如果位置不存在
    }
    if (file.exists()) file.delete()
    val out: FileOutputStream
    try {
        out = FileOutputStream(file)
        out.write(this)
        out.flush()
        out.close()
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        return false
    } catch (e: IOException) {
        e.printStackTrace()
        return false
    }
    return true
}

/**
 * ByteArray添加到file
 */
fun ByteArray.addFile(file: File): Boolean {
    try {
        file.parentFile?.let {
            if (!it.exists()) it.mkdirs() // 如果位置不存在
        }
        // 打开一个随机访问文件流，按读写方式
        val randomFile = RandomAccessFile(file, "rw")
        // 文件长度，字节数
        val fileLength = randomFile.length()
        // 将写文件指针移到文件尾。
        randomFile.seek(fileLength)
        randomFile.write(this)
        randomFile.close()
    } catch (e: IOException) {
        e.printStackTrace()
        return false
    }
    return true
}