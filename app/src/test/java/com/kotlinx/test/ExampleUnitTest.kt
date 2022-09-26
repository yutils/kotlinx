package com.kotlinx.test

import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        var list1: MutableList<U> = ArrayList()
        list1.add(U("11",1))
        list1.add(U("22",2))
        list1.add(U("33",3))
        list1.add(U("44",4))

        var list2 = list1.toList()

        var list3 = list2.toMutableList()

        list3[3].name="333333333333"

        println("")
        list1.forEach { println(it) }
        println("---------------")
        list2.forEach { println(it) }
        println("---------------")
        list3.forEach { println(it) }
    }
}

class U {
    var name: String = ""
    var age = 0

    constructor(name: String, age: Int) {
        this.name = name
        this.age = age
    }

    override fun toString(): String {
        return "U(name='$name', age=$age)"
    }

}