package com.kotlinx.extend

import java.math.RoundingMode


/**
 * 不显示科学计数，不够的填充0
 * 如：0 显示成 0.00
 */
fun Double.fill(scale: Int = 2): String {
    return this.toBigDecimal().fill(scale)
}

/**不显示科学计数*/
fun Double.notScience(scale: Int = 2): String {
    return this.toBigDecimal().notScience(scale)
}

/**
 * 保留小数，默认四舍五入
 */
fun Double.keepDecimalPlaces(scale: Int = 2, roundingMode: RoundingMode = RoundingMode.HALF_UP): Double {
    var bd = this.toBigDecimal()
    bd = bd.setScale(scale, roundingMode)
    return bd.toDouble()
}