package com.kotlinx.extend

import java.text.DecimalFormat


/**
 * 不显示科学计数，不够的填充0
 * 如：0 显示成 0.00
 */
fun Float.fill(scale: Int = 2): String {
    val mat = StringBuilder("0")
    if (scale > 0) {
        mat.append(".")
        for (i in 0 until scale) mat.append("0")
    }
    val df = DecimalFormat(mat.toString())
    return df.format(this)
}
