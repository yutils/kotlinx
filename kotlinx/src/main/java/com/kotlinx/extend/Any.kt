package com.kotlinx.extend

import com.google.gson.Gson

val gson: Gson by lazy { Gson() }

/**
 * 任意类转换成Unit
 */
fun Any.toUnit(): Unit {}
/**
 * 实体转JSON
 */
fun Any.toJson(): String {
    return Gson().toJson(this)
}
