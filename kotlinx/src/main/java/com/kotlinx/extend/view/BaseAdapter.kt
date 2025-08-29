package com.kotlinx.extend.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.kotlinx.extend.addAndReplace
import com.kotlinx.utils.debounce


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
val dataList = mutableListOf("A","B","C")
val adapter = object : BaseAdapter<String>(R.layout.user_item, dataList) {
    override fun item(holder: BaseHolder, position: Int) {
        val binding = holder.binding as UserItemBinding
        val item = list[position]
        binding.tvName.text = item
        binding.iv.setOnClickListener { ("点击图片：" + item).toast() }
    }
}
adapter.onItemClickListener = { position ->
    val item = adapter.list[position]
}
recyclerView.adapter = adapter


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
class UserAdapter<T>(var list: List<T>?) : RecyclerView.Adapter<UserAdapter.VH>() {
    //ViewHolder
    class VH(
        parent: ViewGroup,
        val binding: ItemUserBinding =
            ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position] as User
        holder.binding.data = item
        holder.binding.iv.setOnClickListener { ("点击：" + item.name).toast() }
        //必须要有这行，防止闪烁
        holder.binding.executePendingBindings()
    }
    override fun getItemCount(): Int {
        return list?.size ?: 0
    }
    fun add(obj: T, position: Int) {
        if (position > list.size) return
        list.add(position, obj)
        notifyDataSetChanged()
        dataChangeListener?.invoke(list)
    }
    fun update(newList: Iterable<T>) {
        list.clear()
        for (item in newList) {
            list.add(item)
        }
        notifyDataSetChanged()
        dataChangeListener?.invoke(list)
    }
}

使用
val adapter = UserAdapter(..)
binding.rv.layoutManager = LinearLayoutManager(this@UserListActivity)
binding.rv.adapter=adapter

 */

/* ListAdapter 原生用法
//ListAdapter,高效更新、内置动画、差异计算默认在后台线程进行，不会阻塞主线程、简化Adapter实现
class UserListAdapter(private val onDeleteClick: (User) -> Unit, private val onChangeClick: (User) -> Unit) : ListAdapter<User, UserListAdapter.VH>(UserDiffCallback()) {
    //ViewHolder
    class VH(
        parent: ViewGroup,
        val binding: ItemUserBinding =
            ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(parent)
    }
    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            tvId.text = "ID: ${item.id}"
            tvName.text = "姓名: ${item.name}"
            btnDelete.setOnClickListener { onDeleteClick(item) }
            btnChangeName.setOnClickListener { onChangeClick(item) }
        }
    }
}
class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id  // 判断两个对象是否代表同一个项目（通常比较唯一标识符）
    }
    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem   // 判断两个项目的内容是否相同（比较所有需要显示的字段）
    }
}

//使用
val adapter = UserListAdapter(..)
binding.rv.layoutManager = LinearLayoutManager(this@UserListActivity)
binding.rv.adapter=adapter

//更新、删除、修改数据
adapter.submitList(users)

 */

//region 下拉刷新，上拉加载完整演示
/*
var page: Int = 1
var pageSize: Int = 20
var adapter: BaseAdapter<User>? = null
var bottomAdapter = BottomAdapter(this, "正在加载...")
var oldData: ResponsePage<User>? = null //最后一次请求数据

override fun onCreate() {
    ...
    //初始化Adapter
    initAdapter()
    binding.run {
        //下拉刷新
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_green_dark, android.R.color.holo_green_light, android.R.color.holo_orange_light, android.R.color.holo_red_light)
        swipeRefreshLayout.setOnRefreshListener {
            binding.swipeRefreshLayout.isRefreshing = false
            page = 1
            adapter?.removeAll()
            网络请求(page, pageSize){value - > net(value)}
        }
        //上拉加载
        recyclerView.toBottomListener {
            oldData?.let {
                if (page * pageSize >= it.totalSize) return@let
                page++
                网络请求(page, pageSize){value - > net(value)}
            }
        }
    }
    网络请求(page, pageSize){value - > net(value)}
}

//初始化Adapter
fun initAdapter() {
    binding.rv.init()
    adapter = object : BaseAdapter<User>(R.layout.activity_user_item) {
        override fun item(holder: BaseHolder, position: Int) {
            val binding = holder.binding as ActivityUserItemBinding
            val item = list[position]
            binding.run {
                tvName.text = item.realName
                tvPhone.text = item.phone
            }
        }
    }
    //点击
    adapter?.onItemClickListener = { position ->
        adapter?.list?.get(position).let { item ->
        }
    }
    val config = ConcatAdapter.Config.Builder().setIsolateViewTypes(true).build()
    val concatAdapter = ConcatAdapter(config, adapter, bottomAdapter)
    binding.recyclerView.adapter = concatAdapter
}

//网络请求结果回调
fun net(value: ResponsePage<User>) {
    this.oldData = value
    when {
        value.totalSize == 0 -> bottomAdapter.showMatchParent("暂无数据")
        page * pageSize >= value.totalSize -> bottomAdapter.showWrapContent("没有更多数据")
        page * pageSize < value.totalSize -> bottomAdapter.showWrapContent("正在加载...")
    }
    if (page == 1) adapter?.list?.clear()
    //如果id相同，覆盖。如果不存在，添加。
    adapter?.add(value.objs) { oldItem, newItem ->
        oldItem.id == newItem.id
    }
}

 */
//endregion

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

    //防抖延迟，毫秒，默认200毫秒
    var debounceMillis: Long = 200

    var isSelect: Int = -1
        set(value) {
            notifyItemRangeChanged(field, list.size)
            field = value
            notifyItemRangeChanged(value, list.size)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder {
        val baseHolder = BaseHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), layout, parent, false))
        onCreateViewHolderListener?.invoke(baseHolder)
        return baseHolder
    }

    abstract fun item(holder: BaseHolder, position: Int)

    override fun onBindViewHolder(holder: BaseHolder, position: Int) {
        item(holder, position)
        //单击
        holder.binding.root.setOnClickListener { debounce(millis = debounceMillis) { onItemClickListener?.invoke(position) } }
        //长按
        holder.binding.root.setOnLongClickListener { onItemLongClickListener?.invoke(position);false }
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