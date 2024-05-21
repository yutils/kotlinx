package com.kotlinx.extend.view

import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView 注入直接使用的方法
 */
/*
用法
val list = mutableListOf("1", "2", "3", "4", "5", "6")
adapter = recyclerView.show(R.layout.user_item, list) { holder, position ->
    val binding = holder.binding as UserItemBinding
    val item = list[position]
}
adapter.onItemClickListener = { position ->
    "第${position}行被点击了".toast()
}

或直接使用BaseAdapter
var adapter: BaseAdapter<String>? = null
private fun initRecyclerView(){
    recyclerView.init()
    adapter = object : BaseAdapter<String>(R.layout.user_item) {
        override fun item(holder: BaseHolder, position: Int) {
            val binding = holder.binding as UserItemBinding
            val item = list[position]
            binding.tvName.text = item
            binding.iv.setOnClickListener { ("点击图片：" + item).toast() }
        }
    }
    adapter?.onItemClickListener = { position ->
        val item = adapter.list[position]
    }
    recyclerView.adapter = adapter
}
adapter?.add(mutableListOf("1", "2", "3"))
 */
fun <T> RecyclerView.show(layout: Int, list: MutableList<T> = mutableListOf(), listener: ((holder: BaseHolder, position: Int) -> Unit)? = null): BaseAdapter<T> {
    if (this.layoutManager == null) this.init()
    val adapter = object : BaseAdapter<T>(layout, list) {
        override fun item(holder: BaseHolder, position: Int) {
            listener?.invoke(holder, position)
        }
    }
    this.adapter = adapter
    return adapter
}

/**
 * RecyclerView 设置布局方向，和一行（列）个数
 */
/*
用法：
binding.rv.init()
 */
fun RecyclerView.init(orientation: Int = RecyclerView.VERTICAL, items: Int? = null): RecyclerView {
    //单行/单列
    if (items == null) {
        val layoutManager = LinearLayoutManager(context)
        layoutManager.isSmoothScrollbarEnabled = true
        layoutManager.orientation = orientation
        this.layoutManager = layoutManager
        return this
    }
    //多行多列
    val layoutManager = GridLayoutManager(context, items)
    layoutManager.isSmoothScrollbarEnabled = true
    layoutManager.orientation = orientation
    this.layoutManager = layoutManager
    return this
}

/**
 * 显示空的RecyclerView，可以设置提示文字或者 textView
 */
/*
用法：
recyclerView.showEmpty("暂无数据") {
    it.textSize = 20F
    it.setTextColor(Color.RED)
}
 */
fun RecyclerView.showEmpty(mEmptyText: CharSequence = "暂无数据", listener: ((textView: TextView) -> Unit)? = null): BaseViewAdapter<CharSequence> {
    if (this.layoutManager == null) this.init()
    val adapter = BottomAdapter(context, mEmptyText, listener)
    adapter.showMatchParent()
    this.adapter = adapter
    return adapter
}

/**
 * 滑动时到顶部监听（到顶部后继续滑动不会触发）
 */
/*
binding.rv.toTopListener {
    "toTopListener".toast()
}
 */
inline fun RecyclerView.toTopListener(crossinline listener: () -> Unit) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        //滚动完毕，没有滑动成功，不会触发
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            //不能继续往顶部滑动
            if (!recyclerView.canScrollVertically(-1)) listener.invoke()
        }
    })
}

/**
 * 滑动时不在顶部监听，一旦满足条件会立即触发多次
 */
/*
binding.rv.toTopListener {
   "toTopListener".toast()
}
 */
inline fun RecyclerView.notToTopListener(crossinline listener: () -> Unit) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        //滚动完毕，没有滑动成功，不会触发
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            //不能继续往顶部滑动
            if (recyclerView.canScrollVertically(-1)) listener.invoke()
        }
    })
}

/**滑动时到底部监听（到底部后继续滑动不会触发）（上拉加载）*/
inline fun RecyclerView.toBottomListener(crossinline listener: () -> Unit) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        //滚动完毕，没有滑动成功，不会触发
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            //不能继续往底部滑动
            if (!recyclerView.canScrollVertically(1)) listener.invoke()
        }
    })
}

/**滑动时不在低部监听，一旦满足条件会立即触发多次*/
inline fun RecyclerView.notToBottomListener(crossinline listener: () -> Unit) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        //滚动完毕，没有滑动成功，不会触发
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            //不能继续往底部滑动
            if (recyclerView.canScrollVertically(1)) listener.invoke()
        }
    })
}

/**滑动时在顶部和底部之间会触发，一旦满足条件会立即触发多次*/
inline fun RecyclerView.toBetweenTopAndBottomListener(crossinline listener: () -> Unit) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        //滚动完毕，没有滑动成功，不会触发
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            //能继续往顶部滑动，也能继续往底部滑动
            if (recyclerView.canScrollVertically(1) && recyclerView.canScrollVertically(-1)) listener.invoke()
        }
    })
}


/**滑动完成后能看到顶部，会触发 （下拉刷新）*/
inline fun RecyclerView.scrollToTopListener(crossinline listener: () -> Unit) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        //滚动状态改变，没有滑动成功，也会触发
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            //newState分 0,1,2三个状态,2是滚动状态,0是停止
            if (newState == 0) {
                //不能继续往顶部滑动
                if (!recyclerView.canScrollVertically(-1)) listener.invoke()
            }
        }
    })
}

/**滑动完成后不能看到顶部，会触发*/
inline fun RecyclerView.scrollNotToTopListener(crossinline listener: () -> Unit) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        //滚动状态改变，没有滑动成功，也会触发
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            //newState分 0,1,2三个状态,2是滚动状态,0是停止
            if (newState == 0) {
                //不能继续往顶部滑动
                if (recyclerView.canScrollVertically(-1)) listener.invoke()
            }
        }
    })
}

/**滑动完成后能看到底部，会触发 */
inline fun RecyclerView.scrollToBottomListener(crossinline listener: () -> Unit) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        //滚动状态改变，没有滑动成功，也会触发
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            //newState分 0,1,2三个状态,2是滚动状态,0是停止
            if (newState == 0) {
                //不能继续往底部滑动
                if (!recyclerView.canScrollVertically(1)) listener.invoke()
            }
        }
    })
}

/**滑动完成后不能看到底部，会触发 */
inline fun RecyclerView.scrollNotToBottomListener(crossinline listener: () -> Unit) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        //滚动状态改变，没有滑动成功，也会触发
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            //newState分 0,1,2三个状态,2是滚动状态,0是停止
            if (newState == 0) {
                //不能继续往底部滑动
                if (recyclerView.canScrollVertically(1)) listener.invoke()
            }
        }
    })
}

/**滑动完成后在顶部和底部之间会触发*/
inline fun RecyclerView.scrollToBetweenTopAndBottomListener(crossinline listener: () -> Unit) {
    this.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        //滚动状态改变，没有滑动成功，也会触发
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            //newState分 0,1,2三个状态,2是滚动状态,0是停止
            if (newState == 0) {
                //能继续往顶部滑动，也能继续往底部滑动
                if (recyclerView.canScrollVertically(1) && recyclerView.canScrollVertically(-1)) listener.invoke()
            }
        }
    })
}
