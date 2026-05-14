package com.kotlinx.extend

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonParser

fun String.parseJsonElementOrNull(): JsonElement? =
    runCatching { JsonParser.parseString(this) }.getOrNull()

fun String.parseJsonObjectOrNull(): JsonObject? {
    val el = parseJsonElementOrNull() ?: return null
    return if (el.isJsonObject) el.asJsonObject else null
}

fun String.parseJsonArrayOrNull(): JsonArray? {
    val el = parseJsonElementOrNull() ?: return null
    return if (el.isJsonArray) el.asJsonArray else null
}

fun JsonElement?.asStringOrNull(): String? {
    if (this == null || isJsonNull) return null
    if (!isJsonPrimitive) return null
    val p = asJsonPrimitive
    return try {
        when {
            p.isString -> p.asString
            else -> p.toString().trim('"')
        }
    } catch (_: Exception) {
        null
    }
}

fun JsonElement?.asIntOrNull(): Int? =
    if (this != null && isJsonPrimitive) runCatching { asJsonPrimitive.asInt }.getOrNull() else null

fun JsonElement?.asLongOrNull(): Long? =
    if (this != null && isJsonPrimitive) runCatching { asJsonPrimitive.asLong }.getOrNull() else null

fun JsonElement?.asDoubleOrNull(): Double? =
    if (this != null && isJsonPrimitive) runCatching { asJsonPrimitive.asDouble }.getOrNull() else null

fun JsonElement?.asBooleanOrNull(): Boolean? =
    if (this != null && isJsonPrimitive) runCatching { asJsonPrimitive.asBoolean }.getOrNull() else null

fun JsonObject.stringOrNull(key: String): String? = get(key)?.asStringOrNull()
fun JsonObject.intOrNull(key: String): Int? = get(key)?.asIntOrNull()
fun JsonObject.longOrNull(key: String): Long? = get(key)?.asLongOrNull()
fun JsonObject.doubleOrNull(key: String): Double? = get(key)?.asDoubleOrNull()
fun JsonObject.booleanOrNull(key: String): Boolean? = get(key)?.asBooleanOrNull()
fun JsonObject.objOrNull(key: String): JsonObject? {
    val el = get(key) ?: return null
    return if (el.isJsonObject) el.asJsonObject else null
}

fun JsonObject.arrayOrNull(key: String): JsonArray? {
    val el = get(key) ?: return null
    return if (el.isJsonArray) el.asJsonArray else null
}
