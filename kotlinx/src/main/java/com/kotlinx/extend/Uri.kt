package com.kotlinx.extend

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.storage.StorageManager
import androidx.core.net.toUri
import androidx.documentfile.provider.DocumentFile
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.OutputStreamWriter


/**
 * 写入文件
 * 举例：
 * val uri = activity.contentResolver.persistedUriPermissions.firstOrNull()?.uri
 * uri?.let { it ->
 *     val success = it.writeFileToFolder(activity, "哈哈哈", "test.txt")
 *     (if (success) "写入成功" else "写入失败").toast().logI()
 * } ?: "没有授权文件夹".toast().logI()
 */
fun Uri.writeFileToFolder(context: Context, value: String, fileName: String, mimeType: String = "text/plain"): Boolean {
    try {
        val documentFile = DocumentFile.fromTreeUri(context, this)
        documentFile?.let { folder ->
            // 创建或找到文件
            var file = folder.findFile(fileName)
            file?.delete()
            file = folder.createFile(mimeType, fileName)
            file?.uri?.let { fileUri ->
                context.contentResolver.openOutputStream(fileUri)?.use { outputStream ->
                    OutputStreamWriter(outputStream).use { writer ->
                        writer.write(value)
                        writer.flush()
                    }
                }
                return true
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return false
}

/**
 * 读取文件
 * 举例：
 * val uri = activity.contentResolver.persistedUriPermissions.firstOrNull()?.uri
 * if (uri == null) return "没有授权文件夹".toast().logI().let{}
 * val value = uri?.readFileFromFolder(activity, "test.txt")
 * "读取结果：${value}".toast().logI()
 */
fun Uri.readFileFromFolder(context: Context, fileName: String): String? {
    var value: String? = null
    try {
        val documentFile = DocumentFile.fromTreeUri(context, this)
        documentFile?.let { folder ->
            val file = folder.findFile(fileName)
            file?.uri?.let { fileUri ->
                context.contentResolver.openInputStream(fileUri)?.use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        value = reader.readText()
                    }
                } ?: run {
                    //"文件不存在".logI()
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return value
}

/**
 * 辅助函数：获取SD卡根目录URI（兼容API 24+）
 * 举例：
 * if (activity.contentResolver.persistedUriPermissions.isNotEmpty()) return "已有授权文件夹".logI().toast().let {}
 * val requestStorageAccess = activity.activityResultRegistry.register("key_storage_access", ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
 *     uri?.let {
 *         // 持久化权限
 *         activity.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
 *         println("用户选择的目录: $it ")
 *     } ?: run {
 *         println("用户取消了目录选择")
 *     }
 * }
 * // 获取SD卡根目录URI作为初始路径
 * val sdCardUri = getSDCardRootUri(activity)
 * requestStorageAccess.launch(sdCardUri)
 */
fun getSDCardRootUri(context: Context): Uri? {
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // API 30及以上使用官方方法
            val storageVolumes = context.getSystemService(StorageManager::class.java).storageVolumes.filter { it.isRemovable } // 筛选可移除存储（通常是SD卡）
            storageVolumes.firstOrNull()?.let { volume ->
                volume.directory?.toUri()
            }
        } else {
            // API 24-29使用兼容方案
            val storageManager = context.getSystemService(Context.STORAGE_SERVICE) as StorageManager
            // 通过反射获取存储卷信息
            val storageVolumes = storageManager.javaClass.getMethod("getStorageVolumes").invoke(storageManager) as List<*>

            for (volume in storageVolumes) {
                // 检查是否为可移除存储
                val isRemovable = volume?.javaClass?.getMethod("isRemovable")?.invoke(volume) as Boolean?
                if (isRemovable == true) {
                    // 获取存储卷的路径
                    val file = volume.javaClass.getMethod("getPathFile").invoke(volume) as File?
                    file?.let {
                        return it.toUri()
                    }
                }
            }
            null
        }
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

/**
 * 移除特定URI的持久化权限
 * 举例：
 * val uri = activity.contentResolver.persistedUriPermissions.firstOrNull()?.uri
 * uri?.removePermission(activity)
 */
fun Uri.removePermission(context: Context): Boolean {
    try {
        val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
        context.contentResolver.releasePersistableUriPermission(this, flags)
        return true
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

/**
 * 移除所有持久化权限
 * 举例：
 * removeAllPermissions(activity)
 */
fun removeAllPermissions(context: Context): Boolean {
    try {
        context.contentResolver.persistedUriPermissions.forEach {
            val flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            context.contentResolver.releasePersistableUriPermission(it.uri, flags)
        }
        return true
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}
