package com.kotlinx.extend

import java.text.SimpleDateFormat
import java.util.*

/** 日期格式化成文本**/
/*举例：Date().format()*/
fun Date.format(dateStyle: String = "yyyy-MM-dd HH:mm:ss"): String {
    val sdf = SimpleDateFormat(dateStyle, Locale.getDefault())
    return sdf.format(this)
}

/** 将日期格式的文本转换成Date**/
/*举例："5L2g5aW9".toStringFromBase64()*/
fun String.parseDate(dateStyle: String = "yyyy-MM-dd HH:mm:ss"): Date? {
    val sdf = SimpleDateFormat(dateStyle, Locale.getDefault())
    return sdf.parse(this)
}
