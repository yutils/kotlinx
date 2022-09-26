package com.kotlinx.extend

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.*
import java.util.*

fun ByteArray.toBitmap(): Bitmap? {
    return if (this.isNotEmpty()) {
        BitmapFactory.decodeByteArray(this, 0, this.size)
    } else {
        null
    }
}

fun Bitmap.toByteArray(): ByteArray {
    val bos = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.PNG, 100, bos)
    return bos.toByteArray()
}

fun ByteArray.base64EncodeToString(): String {
    return Base64.encodeToString(this, Base64.NO_WRAP) //不自动换行
}

fun ByteArray.base64Encode(): ByteArray {
    return Base64.encode(this, Base64.NO_WRAP) //不自动换行
}

fun String.base64Decode(): ByteArray {
    return Base64.decode(this.toByteArray(), Base64.NO_WRAP) //不自动换行
}

fun ByteArray.toFile(file: File): Boolean {
    if (!Objects.requireNonNull(file.parentFile).exists()) // 如果位置不存在
        file.parentFile?.mkdirs()
    if (file.exists()) file.delete()
    val out: FileOutputStream
    try {
        out = FileOutputStream(file)
        out.write(this)
        out.flush()
        out.close()
    } catch (e: FileNotFoundException) {
        println("No Find File")
        return false
    } catch (e: IOException) {
        println("IO Error")
        return false
    }
    return true
}

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

