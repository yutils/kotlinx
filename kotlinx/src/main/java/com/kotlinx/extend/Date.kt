package com.kotlinx.extend

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
private val threadLocalFormatters = ThreadLocal.withInitial { HashMap<String, SimpleDateFormat>() }

@RequiresApi(Build.VERSION_CODES.O)
private fun formatterFor(pattern: String): SimpleDateFormat {
    val map = threadLocalFormatters.get()!!
    return map.getOrPut(pattern) { SimpleDateFormat(pattern, Locale.getDefault()) }
}

/** 日期格式化成文本**/
/*举例：Date().format()*/
fun Date.format(dateStyle: String = "yyyy-MM-dd HH:mm:ss"): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        formatterFor(dateStyle).format(this)
    } else {
        val sdf = SimpleDateFormat(dateStyle, Locale.getDefault())
        sdf.format(this)
    }

/** 将日期格式的文本转换成Date**/
fun String.parseDate(dateStyle: String = "yyyy-MM-dd HH:mm:ss"): Date? =
    runCatching { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        formatterFor(dateStyle).parse(this)
    } else {
        val sdf = SimpleDateFormat(dateStyle, Locale.getDefault())
        sdf.parse(this)
    }
    }.getOrNull()
