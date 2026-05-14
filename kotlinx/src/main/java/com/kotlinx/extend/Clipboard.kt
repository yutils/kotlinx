package com.kotlinx.extend

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context

fun Context.clipboardManager(): ClipboardManager =
    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

/**
 * 复制到剪贴板（主线程调用更安全）。
 */
fun CharSequence.copyToClipboard(context: Context, label: CharSequence = "label"): Boolean =
    runCatching {
        context.clipboardManager().setPrimaryClip(ClipData.newPlainText(label, this))
        true
    }.getOrElse {
        it.printStackTrace()
        false
    }

/**
 * 读取剪贴板纯文本（部分机型或权限策略下可能为空）。
 */
fun Context.primaryClipPlainText(): String? {
    val clip = clipboardManager().primaryClip ?: return null
    if (clip.itemCount <= 0) return null
    return clip.getItemAt(0)?.text?.toString()
}
