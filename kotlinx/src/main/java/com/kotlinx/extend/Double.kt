package com.kotlinx.extend

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat


/**
 * 不显示科学计数，不够的填充0
 * 如：0 显示成 0.00
 */
fun Double.fill(scale: Int = 2): String {
    val mat = StringBuilder("0")
    if (scale > 0) {
        mat.append(".")
        for (i in 0 until scale) mat.append("0")
    }
    val df = DecimalFormat(mat.toString())
    return df.format(this)
}

/**不显示科学计数*/
fun Double.notScience(scale: Int = 2): String {
    val mat = StringBuilder("#")
    if (scale > 0) {
        mat.append(".")
        for (i in 0 until scale) mat.append("#")
    }
    val df = DecimalFormat(mat.toString())
    return df.format(this)
}

/**
 * 保留小数，默认四舍五入
 */
fun Double.keepDecimalPlaces(scale: Int = 2, roundingMode: RoundingMode = RoundingMode.HALF_UP): Double {
    var bd = BigDecimal(this)
    bd = bd.setScale(scale, roundingMode)
    return bd.toDouble()
}