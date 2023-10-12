package com.kotlinx.extend

/**
 * 打印 List 对象
 */
fun List<Any>?.toString(): String {
    if (this == null) return "null"
    val iMax = this.size - 1
    if (iMax == -1) return "[]"
    val b = StringBuilder()
    b.append('[')
    var i = 0
    while (true) {
        b.append(this[i].toString())
        if (i == iMax) return b.append(']').toString()
        if (i != 0) b.append(", ")
        i++
    }
}
