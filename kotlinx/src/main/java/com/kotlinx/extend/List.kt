package com.kotlinx.extend

/**
 * 打印 List 对象
 */
fun List<Any>?.toString(): String? {
    return this?.toJson()
}
