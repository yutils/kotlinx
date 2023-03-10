package com.kotlinx

import com.kotlinx.extend.showStackTrace
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun test() {

        println("---------------------------------".showStackTrace())

    }
}