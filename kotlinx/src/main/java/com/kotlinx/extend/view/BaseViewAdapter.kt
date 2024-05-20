package com.kotlinx.extend.view

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kotlinx.extend.addAndReplace
import com.kotlinx.utils.debounce

/*
简单使用：
val list2 = mutableListOf("111", "222", "333", "444")
val myAdapter = object : BaseViewAdapter<String>(list2) {
    override fun getView(): View {
        return createView()
    }
    override fun item(holder: BaseViewHolder, position: Int) {
        val tv = holder.root.findViewWithTag<TextView>("tv")
        tv.text = list2[position]
    }
}
recyclerView.adapter = myAdapter

继承使用：
val list2 = mutableListOf("111", "222", "333", "444")
class MyAdapter(var data: MutableList<String>) : BaseViewAdapter<String>(data) {
    override fun getView(): View {
        return createView()
    }
    override fun item(holder: BaseViewHolder, position: Int) {
        val tv = holder.root.findViewWithTag<TextView>("tv")
        tv.text = list2[position]
    }
}
val myAdapter=MyAdapter(list2)
recyclerView.adapter = myAdapter


//创建view
fun createView(): View {
    val tv = TextView(this).apply {
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
            setMargins(0, 0, 0, 0)
            gravity = Gravity.CENTER //设置中心
        }
        textSize = 18f
        setTextColor(Color.parseColor("#EE22EE"))
        text = "测试"
        tag = "tv"
    }
    val root2 = LinearLayout(this).apply {
        layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        removeAllViews()
        orientation = LinearLayout.VERTICAL
        setPadding(15, 15, 15, 15)
        tag = "root"
    }
    root2.addView(tv)
    return root2
}
*/

class BaseViewHolder(val root: View) : RecyclerView.ViewHolder(root)

/**
 * 不使用布局文件的RecyclerView
 * @author yujing 2023年7月18日11:36:10
 */
abstract class BaseViewAdapter<T>(val list: MutableList<T> = mutableListOf()) : RecyclerView.Adapter<BaseViewHolder>() {
    //recyclerView
    var recyclerView: RecyclerView? = null

    //是否是第一次加载
    private var mIsFirstLoad = true

    //单击
    var onItemClickListener: ((position: Int) -> Unit)? = null

    //长按
    var onItemLongClickListener: ((position: Int) -> Unit)? = null

    //数据改变监听
    var dataChangeListener: ((list: MutableList<T>) -> Unit)? = null

    //防抖延迟，毫秒，默认200毫秒
    var debounceMillis: Long = 200

    var isSelect: Int = -1
        set(value) {
            notifyItemRangeChanged(field, list.size)
            field = value
            notifyItemRangeChanged(value, list.size)
        }

    abstract fun getView(): View
    abstract fun item(holder: BaseViewHolder, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(getView())
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        item(holder, position)
        //单击
        onItemClickListener?.let { holder.root.setOnClickListener { debounce(debounceMillis) { onItemClickListener?.invoke(position) } } }
        //长按
        onItemClickListener?.let { holder.root.setOnLongClickListener { onItemLongClickListener?.invoke(position);false } }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        if (mIsFirstLoad) {
            dataChangeListener?.invoke(list)
            mIsFirstLoad = false
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    /**
     * 更新list
     */
    fun update(newList: Iterable<T>) {
        list.clear()
        for (item in newList) {
            list.add(item)
        }
        notifyDataSetChanged()
        dataChangeListener?.invoke(list)

    }

    /**
     * 更新一个对象
     */
    fun update(obj: T, position: Int) {
        if (position in 0 until list.size) {
            list[position] = obj
            notifyItemRangeChanged(position, position)
            dataChangeListener?.invoke(list)
        }
    }

    /**
     * 添加list，如果他们条件满足，覆盖（如果id相同）。如果不存在条件满足的项，添加。
     */
    /*
        adapter.add(list) { oldItem, newItem ->
            oldItem.id == newItem.id
        }
     */
    fun add(newList: Iterable<T>, identical: ((T, T) -> Boolean)? = null) {
        if (identical != null) {
            list.addAndReplace(newList, identical)
        } else {
            list.addAll(newList)
        }
        notifyDataSetChanged()
        dataChangeListener?.invoke(list)
    }

    /**
     * 添加一个对象
     */
    fun add(obj: T) {
        list.add(obj)
        notifyItemInserted(list.size - 1)
        dataChangeListener?.invoke(list)
    }

    /**
     * 指定位置，添加一个对象
     */
    fun add(obj: T, position: Int) {
        if (position > list.size) return
        list.add(position, obj)
        notifyDataSetChanged()
        dataChangeListener?.invoke(list)
    }

    /**
     * 删除一个对象
     */
    fun removeAt(position: Int) {
        if (position in 0 until list.size) {
            list.removeAt(position)
            //刷新,notifyItemRemoved要配合notifyItemRangeChanged，不然要数组越界
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, list.size - position)
            dataChangeListener?.invoke(list)
        }

    }

    /**
     * 删除全部对象
     */
    fun removeAll() {
        clear()
    }

    /**
     * 删除全部对象
     */
    fun clear() {
        list.clear()
        notifyDataSetChanged()
        dataChangeListener?.invoke(list)
    }
}