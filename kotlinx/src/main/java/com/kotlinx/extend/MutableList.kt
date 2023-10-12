package com.kotlinx.extend

/**
 * 向list中添加一个list，如果他们条件满足，覆盖（如果id相同）。如果不存在条件满足的项，添加。
 * 如：用于上拉加载，重新拉取当前页数据更新，拉取新的页面数据覆盖
 */
/*
举例：
(adapter.list as MutableList<User>).addAndReplace(list) { oldItem, newItem ->
    oldItem.id == newItem.id
}
 */
inline fun <T> MutableList<T>.addAndReplace(newList: List<T>, identical: (T, T) -> Boolean): Unit {
    //条件满足，覆盖。如果不存在，添加。
    newList.forEach { newItem ->
        var exist = false
        for (i in 0 until this.size) {
            val oldItem = this[i]
            //条件满足，覆盖
            if (identical.invoke(oldItem, newItem)) {
                this[i] = newItem
                exist = true
            }
        }
        //如果不存在条件满足，添加
        if (!exist) this.add(newItem)
    }
}


/**
 * 打印 MutableList 对象
 */
fun MutableList<Any>?.toString(): String {
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