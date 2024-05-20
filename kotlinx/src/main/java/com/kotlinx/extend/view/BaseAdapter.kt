package com.kotlinx.extend.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView


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

//简单使用
val list = mutableListOf("A","B","C")
val adapter = object : BaseAdapter<String>(R.layout.user_item, list) {
    override fun item(holder: BaseHolder, position: Int) {
        val binding = holder.binding as UserItemBinding
        val item = list[position]
        binding.tvName.text = item
        binding.iv.setOnClickListener { ("点击图片：" + item).toast() }
    }
}

//继承使用
recyclerView.init()
class UserAdapter(var data: MutableList<User>) : BaseAdapter<User>(R.layout.user_item, data) {
    override fun item(holder: BaseHolder, position: Int) {
        val binding = holder.binding as UserItemBinding
        val item = list[position] as User
        binding.user = item
        binding.iv.setOnClickListener { ("点击：" + item.name).toast() }
    }
}

recyclerView.adapter = UserAdapter(list)


RecyclerView原生用法：
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
abstract class BaseAdapter<T>(val layout: Int, val list: MutableList<T> = mutableListOf()) : RecyclerView.Adapter<BaseHolder>() {
    //recyclerView
    var recyclerView: RecyclerView? = null

    //是否是第一次加载
    private var mIsFirstLoad = true

    //单击
    var onItemClickListener: ((position: Int) -> Unit)? = null

    //长按
    var onItemLongClickListener: ((position: Int) -> Unit)? = null

    //创建ViewHolder完成
    var onCreateViewHolderListener: ((baseHolder: BaseHolder) -> Unit)? = null

    //数据改变监听
    var dataChangeListener: ((list: MutableList<T>) -> Unit)? = null

    var isSelect: Int = -1
        set(value) {
            notifyItemRangeChanged(field, list.size)
            field = value
            notifyItemRangeChanged(value, list.size)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        val baseHolder = return BaseHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), layout, parent, false))
        onCreateViewHolderListener?.invoke(baseHolder)
        return baseHolder
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
     * 添加list
     */
    fun add(newList: Iterable<T>) {
        list.addAll(newList)
        notifyDataSetChanged()
        dataChangeListener?.invoke(list)
    }

    /**
     * 添加一个
     */
    fun add(obj: T) {
        list.add(obj)
        notifyItemInserted(list.size - 1)
        dataChangeListener?.invoke(list)

    }

    /**
     * 添加一个
     */
    fun add(obj: T, position: Int) {
        if (position > list.size) return
        list.add(position, obj)
        notifyDataSetChanged()
        dataChangeListener?.invoke(list)

    }

    /**
     * 删除全部对象
     */
    fun removeAll() {
        list.clear()
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
}