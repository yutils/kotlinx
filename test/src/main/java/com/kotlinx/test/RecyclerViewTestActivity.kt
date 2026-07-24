package com.kotlinx.test

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kotlinx.Kotlinx
import com.kotlinx.extend.toast
import com.kotlinx.extend.view.BaseViewAdapter
import com.kotlinx.extend.view.BaseViewHolder
import com.kotlinx.extend.view.BottomAdapter
import com.kotlinx.extend.view.init
import com.kotlinx.extend.view.notToTopListener
import com.kotlinx.extend.view.scrollToBottomListener
import com.kotlinx.extend.view.scrollToTopListener
import com.kotlinx.extend.view.show
import com.kotlinx.extend.view.toBottomListener
import com.kotlinx.extend.view.toTopListener
import com.kotlinx.test.databinding.TestItemBinding

class RecyclerViewTestActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(rootView())
    }

    private fun rootView(): View {
        val scrollTip = TextView(this).apply {
            textSize = 13f
            setTextColor(Color.parseColor("#334155"))
            text = "滚动监听：—"
        }

        val myAdapter = object : BaseViewAdapter<String>() {
            override fun getView(): View = rView()

            override fun item(holder: BaseViewHolder, position: Int) {
                val tv = holder.root.findViewWithTag<TextView>("tv")
                tv.text = list[position]
                holder.root.setBackgroundColor(Color.parseColor(if (position == isSelect) "#FFFF00" else "#00FFFFFF"))
            }
        }

        myAdapter.onItemClickListener = {
            myAdapter.isSelect = it
        }

        val bottomAdapter = BottomAdapter(this, "没有更多数据")
        bottomAdapter.showMatchParent()
        val config = ConcatAdapter.Config.Builder().setIsolateViewTypes(true).build()
        val concatAdapter = ConcatAdapter(config, myAdapter, bottomAdapter)

        myAdapter.dataChangeListener = {
            if (it.size == 0) bottomAdapter.showMatchParent("暂无数据")
            else bottomAdapter.showWrapContent("没有更多数据")
        }

        val showData = (1..30).map { "show-$it" }.toMutableList()
        val showRv = RecyclerView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                360,
            )
            val adapter = show(R.layout.test_item, showData) { holder, pos ->
                val binding = holder.binding as TestItemBinding
                binding.tvTest.text = showData[pos]
            }
            adapter.onItemClickListener = { pos ->
                "show 点击 $pos".toast()
            }
            toTopListener { scrollTip.text = "滚动监听：toTopListener" }
            toBottomListener { scrollTip.text = "滚动监听：toBottomListener" }
            notToTopListener { /* 高频，仅用于覆盖注册 */ }
            scrollToTopListener { scrollTip.text = "滚动监听：scrollToTopListener（停在顶部）" }
            scrollToBottomListener { scrollTip.text = "滚动监听：scrollToBottomListener（停在底部）" }
        }

        val rootView = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT,
            )
            orientation = LinearLayout.VERTICAL
            setPadding(15, 15, 15, 15)

            val title = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                ).apply { gravity = Gravity.CENTER }
                textSize = 18f
                setTextColor(Color.parseColor("#EE22EE"))
                text = "RecyclerView 测试"
            }

            val rv = RecyclerView(Kotlinx.app).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    0,
                    1f,
                )
                init()
                adapter = concatAdapter
            }

            val ll = LinearLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                )
                orientation = LinearLayout.HORIZONTAL

                addView(Button(context).apply {
                    text = "添加"
                    setOnClickListener {
                        val list = arrayListOf("添加1", "添加2", "添加3", "添加4", "添加5")
                        myAdapter.add(list) { oldItem, _ -> oldItem == "添加1" }
                    }
                })
                addView(Button(context).apply {
                    text = "更新"
                    setOnClickListener {
                        myAdapter.update(arrayListOf("111", "222", "333", "444", "555", "666", "777", "888"))
                    }
                })
                addView(Button(this@RecyclerViewTestActivity).apply {
                    text = "删除"
                    setOnClickListener {
                        myAdapter.removeAt(3)
                        myAdapter.removeAt(5)
                    }
                })
                addView(Button(this@RecyclerViewTestActivity).apply {
                    text = "删完"
                    setOnClickListener { myAdapter.removeAll() }
                })
            }

            val showTitle = TextView(context).apply {
                text = "下方：RecyclerView.show(R.layout.test_item) + 滚动监听（请上下滑）"
                textSize = 14f
                setPadding(0, 16, 0, 8)
            }

            addView(title)
            addView(ll)
            addView(rv)
            addView(showTitle)
            addView(scrollTip)
            addView(showRv)
        }
        return rootView
    }

    fun rView(): View {
        val tv = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            ).apply {
                setMargins(0, 8, 0, 8)
                gravity = Gravity.CENTER
            }
            textSize = 18f
            setTextColor(Color.parseColor("#EE22EE"))
            text = "测试"
            tag = "tv"
        }
        val root = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
            )
            orientation = LinearLayout.VERTICAL
            setPadding(15, 15, 15, 15)
            tag = "root"
        }
        root.addView(tv)
        return root
    }
}
