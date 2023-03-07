package com.kotlinx.extend

import java.text.SimpleDateFormat
import java.util.*

/** 日期格式化成文本**/
/*举例：Date().format()*/
fun Date.format(dateStyle: String = "yyyy-MM-dd HH:mm:ss"): String {
    val sdf = SimpleDateFormat(dateStyle, Locale.getDefault())
    return sdf.format(this)
}