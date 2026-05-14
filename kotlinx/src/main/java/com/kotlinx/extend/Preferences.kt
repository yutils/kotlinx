package com.kotlinx.extend

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

fun Context.kotlinxPrefs(name: String = "kotlinx_prefs"): SharedPreferences =
    getSharedPreferences(name, Context.MODE_PRIVATE)

inline fun <reified T> SharedPreferences.getJsonOrNull(key: String): T? =
    getString(key, null)?.jsonToObject()

fun SharedPreferences.putJson(key: String, value: Any?) {
    edit { putString(key, value?.toJson()) }
}

fun SharedPreferences.removeKey(key: String) {
    edit { remove(key) }
}
