package com.kotlinx.extend

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Base64
import com.kotlinx.Kotlinx
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.RandomAccessFile

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
        // 如果位置不存在,就创建目录
        if (!it.exists() && !it.mkdirs()) {
            "无法创建父目录，请检查权限 ${it.absolutePath}".logE()
        }
    }
    if (file.exists()) {
        file.delete()//"删除旧文件: ${file.delete()}  ${file.absolutePath}".logI()
    }
    return try {
        FileOutputStream(file).use { out ->
            out.write(this)
            out.flush()
            //"写入文件成功".logI()
            true
        }
    } catch (e: Exception) {
        e.printStackTrace()
        //"写入文件失败: ${e.message}".logE()
        false
    }
}

/**
 * ByteArray添加到file
 */
fun ByteArray.addFile(file: File): Boolean {
    try {
        file.parentFile?.let {
            // 如果位置不存在,就创建目录
            if (!it.exists() && !it.mkdirs()) {
                "无法创建父目录，请检查权限 ${it.absolutePath}".logE()
            }
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

//
///**
// * ByteArray转换成文件写入储存DIRECTORY_DOWNLOADS同理文件夹
// * 如：
// * val resolver = activity.contentResolver
// * val customDir = "/YuJing"
// * val fileName = "abc.txt"
// * "123456789".toByteArray().saveToDOWNLOADS(resolver, customDir, fileName)
// */
//fun ByteArray.saveToDOWNLOADS(resolver: ContentResolver, dir: String, fileName: String): Boolean {
//    val customDir = "${Environment.DIRECTORY_DOWNLOADS}${dir}"
//    return saveToMediaStore(resolver, customDir, fileName)
//}
//
///**
// * ByteArray转换成文件写入储存DIRECTORY_DOCUMENTS同理文件夹
// * 如：
// * val resolver = activity.contentResolver
// * val customDir = "/YuJing"
// * val fileName = "abc.txt"
// * "123456789".toByteArray().saveToDOCUMENTS(resolver, customDir, fileName)
// */
//fun ByteArray.saveToDOCUMENTS(resolver: ContentResolver, dir: String, fileName: String): Boolean {
//    val customDir = "${Environment.DIRECTORY_DOCUMENTS}${dir}"
//    return saveToMediaStore(resolver, customDir, fileName)
//}
//
///**
// * ByteArray转换成文件写入储存
// *
// * val resolver = activity.contentResolver
// * val customDir = "${Environment.DIRECTORY_DOCUMENTS}/YuJing" //末尾不加斜杠 ,仅支持 DIRECTORY_DOCUMENTS DIRECTORY_DOWNLOADS
// * val fileName = "abc.txt"
// *
// */
//private fun ByteArray.saveToMediaStore(resolver: ContentResolver, customDir: String, fileName: String): Boolean {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        val projection = arrayOf(MediaStore.MediaColumns._ID)
//        val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} = ? AND ${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
//        val uri = MediaStore.Files.getContentUri(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) MediaStore.VOLUME_EXTERNAL else "external")
//
//        //查询并删除
//        resolver.query(uri, projection, selection, arrayOf("${customDir}/", fileName), null)?.use { cursor ->
//            while (cursor.moveToNext()) {
//                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
//                val id = cursor.getLong(idColumn)
//                val deleteUri = ContentUris.withAppendedId(uri, id)
//                resolver.delete(deleteUri, null, null)  // 删除旧文件
//            }
//        }
//        val values = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
//            put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
//            put(MediaStore.MediaColumns.RELATIVE_PATH, customDir)
//        }
//        resolver.insert(uri, values)?.let {
//            resolver.openOutputStream(it, "wt")?.use { outputStream ->
//                outputStream.write(this)
//                outputStream.flush()
//                "写入文件成功:${customDir}/$fileName".logI()
//                return true
//            }
//        }
//    } else {
//        val mDir = Environment.getExternalStoragePublicDirectory(customDir).absolutePath
//        val file = File(mDir, fileName)
//        return this.toFile(file)
//    }
//    return false
//}
//
///**
// * 读取 DIRECTORY_DOWNLOADS 文件夹中文件到ByteArray
// * 如：
// * val resolver = activity.contentResolver
// * val customDir = "/YuJing"
// * val fileName = "abc.txt"
// * val byteArray = readFromDOWNLOADS(resolver, customDir, fileName)
// * byteArray?.let { String(it).toast().logI() }
// */
//fun readFromDOWNLOADS(resolver: ContentResolver, dir: String, fileName: String): ByteArray? {
//    val customDir = "${Environment.DIRECTORY_DOWNLOADS}${dir}"
//    return readFromMediaStore(resolver, customDir, fileName)
//}
//
///**
// * 读取 DIRECTORY_DOCUMENTS 文件夹中文件到ByteArray
// * 如：
// * val resolver = activity.contentResolver
// * val customDir = "/YuJing"
// * val fileName = "abc.txt"
// * val byteArray = readFromDOCUMENTS(resolver, customDir, fileName)
// * byteArray?.let { String(it).toast().logI() }
// */
//fun readFromDOCUMENTS(resolver: ContentResolver, dir: String, fileName: String): ByteArray? {
//    val customDir = "${Environment.DIRECTORY_DOCUMENTS}${dir}"
//    return readFromMediaStore(resolver, customDir, fileName)
//}
//
///**
// * 读取 MediaStore 文件夹中文件到ByteArray
// * 如：
// * val resolver = activity.contentResolver
// * val customDir = "${Environment.DIRECTORY_DOCUMENTS}/YuJing" //末尾不加斜杠 ,仅支持 DIRECTORY_DOCUMENTS DIRECTORY_DOWNLOADS
// * val fileName = "abc.txt"
// */
//private fun readFromMediaStore(resolver: ContentResolver, customDir: String, fileName: String): ByteArray? {
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        // 步骤 1：查询是否存在同名文件
//        val projection = arrayOf(MediaStore.MediaColumns._ID)
//        val selection = "${MediaStore.MediaColumns.RELATIVE_PATH} = ? AND ${MediaStore.MediaColumns.DISPLAY_NAME} = ?"
//        val uri = MediaStore.Files.getContentUri(if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) MediaStore.VOLUME_EXTERNAL else "external")
//        var fileUri: android.net.Uri? = null
//        //查询
//        resolver.query(uri, projection, selection, arrayOf("${customDir}/", fileName), null)?.use { cursor ->
//            if (cursor.moveToFirst()) {
//                // 文件存在，获取 Uri
//                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID)
//                val id = cursor.getLong(idColumn)
//                fileUri = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL).buildUpon().appendPath(id.toString()).build()
//                //"找到现有文件: ${customDir}/$fileName".logI()
//            }
//        }
//        return fileUri?.let {
//            resolver.openInputStream(it)?.use { inputStream ->
//                inputStream.readBytes()
//            }
//        }
//    } else {
//        val mDir = Environment.getExternalStoragePublicDirectory(customDir).absolutePath
//        val file = File(mDir, fileName)
//        return file.toByteArray()
//    }
//}
