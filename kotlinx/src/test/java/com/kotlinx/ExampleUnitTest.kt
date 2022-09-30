package com.kotlinx

import com.kotlinx.extend.*
import com.kotlinx.utils.ui
import org.junit.Test

import org.junit.Assert.*
import java.io.File

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun test() {
        println("---------------------------------")
        println("---------------${"".isInt()}")
        println("---------------${"55".isInt()}")
        println("---------------${"55.55".isInt()}")


        println("---------------------------------")
        println("---------------${"".isDouble()}")
        println("---------------${"55".isDouble()}")
        println("---------------${"55.55".isDouble()}")

        println("---------------------------------")
        println("---------------${"".isIntOrDouble()}")
        println("---------------${"55".isIntOrDouble()}")
        println("---------------${"55.55".isIntOrDouble()}")

        println("---------------------------------")
        println("---------------${"".isEmptyOrInt()}")
        println("---------------${"55".isEmptyOrInt()}")
        println("---------------${"55.55".isEmptyOrInt()}")

        println("---------------------------------")
        println("---------------${"".isEmptyOrIntOrDouble()}")
        println("---------------${"55".isEmptyOrIntOrDouble()}")
        println("---------------${"55.55".isEmptyOrIntOrDouble()}")
    }
}