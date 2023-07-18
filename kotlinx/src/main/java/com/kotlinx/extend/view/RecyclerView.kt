package com.kotlinx.extend.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView 注入直接使用的方法
 */
/*
用法
val list = arrayListOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12")
recyclerView.show(R.layout.user_item, list) { holder, position ->
    val binding = holder.binding as UserItemBinding
    val item = list[position]
}.onItemClickListener = { position ->
    "第${position}行被点击了".toast()
}
 */
fun <T> RecyclerView.show(layout: Int, list: MutableList<T>, listener: ((holder: BaseHolder, position: Int) -> Unit)? = null): BaseAdapter<T> {
    if (this.layoutManager == null) this.init()
    val adapter = object : BaseAdapter<T>(layout, list) {
        override fun item(holder: BaseHolder, position: Int) {
            listener?.invoke(holder, position)
        }
    }
    this.adapter = adapter
    return adapter
}

//获取数据列表
fun <T> RecyclerView.getMutableList(): MutableList<T>? {
    this.adapter?.let {
        return (it as BaseAdapter<T>).list
    }
    return null
}

//添加一条数据
fun <T> RecyclerView.addItem(item: T): RecyclerView {
    getMutableList<T>()?.let {
        it.add(item)
        adapter?.notifyItemInserted(it.size - 1)
        scrollToPosition(it.size - 1)
    }
    return this
}

//删除一条数据
fun <T> RecyclerView.removeAt(position: Int): RecyclerView {
    getMutableList<T>()?.let {
        it.removeAt(position)
        //刷新,notifyItemRemoved要配合notifyItemRangeChanged，不然要数组越界
        adapter?.notifyItemRemoved(position)
        adapter?.notifyItemRangeChanged(position, it.size - position)
    }
    return this
}

/**
 * RecyclerView 设置布局方向，和一行（列）个数
 */
/*
用法：
binding.rv.init()
 */
fun RecyclerView.init(Orientation: Int = RecyclerView.VERTICAL, items: Int? = null) {
    //单行/单列
    if (items == null) {
        val layoutManager = LinearLayoutManager(context)
        layoutManager.isSmoothScrollbarEnabled = true
        layoutManager.orientation = Orientation
        this.layoutManager = layoutManager
        return
    }
    //多行多列
    val layoutManager = GridLayoutManager(context, items)
    layoutManager.isSmoothScrollbarEnabled = true
    layoutManager.orientation = Orientation
    this.layoutManager = layoutManager
}

/**
 * 显示空的RecyclerView，可以设置提示文字或者 textView
 */
fun RecyclerView.showEmpty(emptyText: CharSequence? = null, textView: TextView? = null): EmptyAdapter {
    if (this.layoutManager == null) this.init()
    val mAdapter = EmptyAdapter(this.context, emptyText, textView)
    this.adapter = mAdapter
    return mAdapter
}

/**
 * 清空RecyclerView
 */
fun RecyclerView.clear() {
    if (this.layoutManager == null) this.init()
    class ClearAdapter(var context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            class Holder : RecyclerView.ViewHolder(View(context))
            return Holder()
        }

        override fun getItemCount(): Int = 0

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        }
    }
    this.adapter = ClearAdapter(context)
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

/**
空的EmptyHolder
 */
class EmptyHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!)

/**
空的Adapter
 */
class EmptyAdapter(var context: Context, var mEmptyText: CharSequence? = null, var mTextView: TextView? = null) : RecyclerView.Adapter<EmptyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmptyHolder {
        return EmptyHolder(createView())
    }

    override fun onBindViewHolder(holder: EmptyHolder, position: Int) {}

    override fun getItemCount(): Int = 1

    private fun createView(): LinearLayout {
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        layoutParams.gravity = Gravity.CENTER //设置中心对其
        val linearLayout = LinearLayout(context)
        linearLayout.layoutParams = layoutParams
        linearLayout.removeAllViews()
        linearLayout.orientation = LinearLayout.VERTICAL //设置纵向布局
        //如果textView优先用传进来的textView
        mTextView?.let {
            linearLayout.addView(mTextView)
            return linearLayout
        }
        //文字
        val tvParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        tvParams.gravity = Gravity.CENTER //TextView在父布局里面居中
        //实例化一个TextView
        val tv = TextView(context)
        tv.layoutParams = tvParams
        tv.gravity = Gravity.CENTER //文字在TextView里面居中
        tv.textSize = 25f
        tv.setTextColor(Color.parseColor("#999999"))
        mEmptyText?.let {
            tv.text = mEmptyText
        }
        linearLayout.addView(tv)
        return linearLayout
    }
}

/**
 * ViewHolder 基于DataBinding视图绑定
 * @author 余静 2022年6月7日10:21:55
 * 全称 BaseDataBindingViewHolder
 */
class BaseHolder(var binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root)

/**
 * RecyclerViewAdapter 基于DataBinding视图绑定
 * @author 余静 2022年6月7日10:21:55
 * 全称 BaseDataBindingRecyclerViewAdapter
 */

/*
用法举例：
class UserAdapter(var data: List<User>) : BaseAdapter<User>(R.layout.user_item, data) {
    override fun item(holder: BaseHolder, position: Int) {
        val binding = holder.binding as UserItemBinding
        val item = list[position] as User
        binding.user = item
        binding.iv.setOnClickListener { ("点击：" + item.name).toast() }
    }
}
class UserAdapter<T>(var data: List<T>) : BaseAdapter<T>(R.layout.user_item, data) {
    override fun item(holder: BaseHolder, position: Int) {
        val binding = holder.binding as UserItemBinding
        val item = list[position] as User
    }
}

recyclerView.init()
val adapter = object : BaseAdapter<T>(layout, list) {
    override fun item(holder: BaseHolder, position: Int) {
        listener?.invoke(holder, position)
    }
}
recyclerView.adapter = adapter

//或者
val list :MutableList<String> =ArrayList()
list.add("A")
list.add("B")
list.add("C")
val adapter = object : BaseAdapter<String>(R.layout.user_item, list) {
    override fun item(holder: BaseHolder, position: Int) {
        val binding = holder.binding as UserItemBinding
        val item = list[position]
        binding.tvName.text = item
        binding.iv.setOnClickListener { ("点击图片：" + item).toast() }
    }
}

原生用法：
//ViewHolder
class MyViewHolder(var binding: ActivityListItemBinding) : RecyclerView.ViewHolder(binding.root) {}
class CarAdapter<T>(var list: List<T>?) : RecyclerView.Adapter<MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.activity_list_item, parent, false))
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position] as User
        holder.binding.data = item
        holder.binding.iv.setOnClickListener { ("点击：" + item.name).toast() }
        //必须要有这行，防止闪烁
        holder.binding.executePendingBindings()
    }
    override fun getItemCount(): Int {
        return list?.size ?: 0
    }
}
 */
abstract class BaseAdapter<T>(var layout: Int, var list: MutableList<T>?) : RecyclerView.Adapter<BaseHolder>() {
    //recyclerView
    var recyclerView: RecyclerView? = null

    //单击
    var onItemClickListener: ((position: Int) -> Unit)? = null

    //长按
    var onItemLongClickListener: ((position: Int) -> Unit)? = null

    var isSelect: Int = -1
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        return BaseHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), layout, parent, false))
    }

    abstract fun item(holder: BaseHolder, position: Int)

    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        item(holder, position)
        //单击
        onItemClickListener?.let { holder.binding.root.setOnClickListener { onItemClickListener?.invoke(position) } }
        //长按
        onItemClickListener?.let { holder.binding.root.setOnLongClickListener { onItemLongClickListener?.invoke(position);false } }
        //必须要有这行，防止闪烁
        holder.binding.executePendingBindings()
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    /**
     * 更新list
     */
    fun update(newList: Iterable<T>) {
        list?.let {
            it.clear()
            for (item in newList) {
                it.add(item)
            }
            notifyDataSetChanged()
        }
    }

    /**
     * 更新一个对象
     */
    fun update(obj: T, position: Int) {
        list?.let {
            if (position in 0 until it.size) {
                it[position] = obj
                notifyDataSetChanged()
            }
        }
    }

    /**
     * 添加list
     */
    fun add(newList: Iterable<T>) {
        list?.let {
            it.addAll(newList)
            notifyDataSetChanged()
        }
    }

    /**
     * 添加一个
     */
    fun add(obj: T) {
        list?.let {
            it.add(obj)
            notifyItemInserted(list!!.size - 1)
        }
    }

    /**
     * 添加一个
     */
    fun add(obj: T, position: Int) {
        list?.let {
            if (position > it.size) return
            it.add(position, obj)
            notifyDataSetChanged()
        }
    }

    /**
     * 删除全部对象
     */
    fun removeAll() {
        list?.let {
            it.clear()
            notifyDataSetChanged()
        }
    }

    /**
     * 删除一个对象
     */
    fun removeAt(position: Int) {
        list?.let {
            if (position in 0 until it.size) {
                it.removeAt(position)
                //刷新,notifyItemRemoved要配合notifyItemRangeChanged，不然要数组越界
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, it.size - position)
            }
        }
    }
}


/*
用法举例：
val list2 = arrayListOf("111", "222", "333", "444")
val myAdapter = object : BaseViewAdapter<String>(list2) {
    override fun getView(): View {
        return rView()
    }
    override fun item(holder: BaseViewHolder, position: Int) {
        val tv = holder.root.findViewWithTag<TextView>("tv")
        tv.text = list2[position]
    }
}
val recyclerView2 = RecyclerView(Kotlinx.app).apply {
    this.init()
    this.adapter = myAdapter
}
root.addView(recyclerView2)

//创建view
fun rView(): View {
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

或者：


val list2 = arrayListOf("111", "222", "333", "444")
class MyAdapter(var data: MutableList<String>) : BaseViewAdapter<String>(data) {
    override fun getView(): View {
        return rView()
    }
    override fun item(holder: BaseViewHolder, position: Int) {
        val tv = holder.root.findViewWithTag<TextView>("tv")
        tv.text = list2[position]
    }
}
val myAdapter=MyAdapter(list2)
val recyclerView2 = RecyclerView(Kotlinx.app).apply {
    this.init()
    this.adapter = myAdapter
}
root.addView(recyclerView2)

*/

class BaseViewHolder(var root: View) : RecyclerView.ViewHolder(root)

/**
 * 不使用布局文件的RecyclerView
 * @author yujing 2023年7月18日11:36:10
 */
abstract class BaseViewAdapter<T>(var list: MutableList<T>?) : RecyclerView.Adapter<BaseViewHolder>() {
    //recyclerView
    var recyclerView: RecyclerView? = null

    //单击
    var onItemClickListener: ((position: Int) -> Unit)? = null

    //长按
    var onItemLongClickListener: ((position: Int) -> Unit)? = null

    var isSelect: Int = -1
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    abstract fun getView(): View
    abstract fun item(holder: BaseViewHolder, position: Int)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(getView())
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        item(holder, position)
        //单击
        onItemClickListener?.let { holder.root.setOnClickListener { onItemClickListener?.invoke(position) } }
        //长按
        onItemClickListener?.let { holder.root.setOnLongClickListener { onItemLongClickListener?.invoke(position);false } }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun getItemCount(): Int {
        return list?.size ?: 0
    }

    /**
     * 更新list
     */
    fun update(newList: Iterable<T>) {
        list?.let {
            it.clear()
            for (item in newList) {
                it.add(item)
            }
            notifyDataSetChanged()
        }
    }

    /**
     * 更新一个对象
     */
    fun update(obj: T, position: Int) {
        list?.let {
            if (position in 0 until it.size) {
                it[position] = obj
                notifyDataSetChanged()
            }
        }
    }

    /**
     * 添加list
     */
    fun add(newList: Iterable<T>) {
        list?.let {
            it.addAll(newList)
            notifyDataSetChanged()
        }
    }

    /**
     * 添加一个对象
     */
    fun add(obj: T) {
        list?.let {
            it.add(obj)
            notifyItemInserted(list!!.size - 1)
        }
    }

    /**
     * 指定位置，添加一个对象
     */
    fun add(obj: T, position: Int) {
        list?.let {
            if (position > it.size) return
            it.add(position, obj)
            notifyDataSetChanged()
        }
    }

    /**
     * 删除全部对象
     */
    fun removeAll() {
        list?.let {
            it.clear()
            notifyDataSetChanged()
        }
    }

    /**
     * 删除一个对象
     */
    fun removeAt(position: Int) {
        list?.let {
            if (position in 0 until it.size) {
                it.removeAt(position)
                //刷新,notifyItemRemoved要配合notifyItemRangeChanged，不然要数组越界
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, it.size - position)
            }
        }
    }
}
