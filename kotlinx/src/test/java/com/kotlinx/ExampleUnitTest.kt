package com.kotlinx

import com.kotlinx.extend.groupActual
import com.kotlinx.extend.insert
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun test() {

        println("你好啊1234567890".insert(3, "⊙"))

        "你好啊1234567890".groupActual(3).forEach {
            println(it.toString())
        }
    }
}