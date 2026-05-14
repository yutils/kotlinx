package com.kotlinx.extend

fun <T> Result<T>.toastOnFailure(mapper: (Throwable) -> String = { it.oneLineMessage() }) {
    exceptionOrNull()?.let { mapper(it).toast() }
}
