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
import com.kotlinx.extend.view.BaseViewAdapter
import com.kotlinx.extend.view.BaseViewHolder
import com.kotlinx.extend.view.BottomAdapter
import com.kotlinx.extend.view.init

class RecyclerViewTestActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(rootView())
    }

    private fun rootView(): View {
        val myAdapter = object : BaseViewAdapter<String>() {
            override fun getView(): View {
                return rView()
            }

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

        val rootView = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            removeAllViews()
            orientation = LinearLayout.VERTICAL
            setPadding(15, 15, 15, 15)

            val title = TextView(context).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                    setMargins(0, 0, 0, 0)
                    gravity = Gravity.CENTER //设置中心
                }
                textSize = 18f
                setTextColor(Color.parseColor("#EE22EE"))
                text = "RecyclerView测试"
            }

            val rv = RecyclerView(Kotlinx.app).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
                init()
                adapter = concatAdapter
            }

            val ll = LinearLayout(context).apply {
                layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                removeAllViews()
                orientation = LinearLayout.HORIZONTAL

                addView(Button(context).apply {
                    text = "添加"
                    setOnClickListener {

//                    myAdapter.add("003")
//                    myAdapter.add("002")
//                    myAdapter.add("001")
                        val list = arrayListOf("添加1", "添加2", "添加3", "添加4", "添加5")
                        //添加且去重复
                        myAdapter.add(list) { oldItem, newItem ->
                            oldItem == "添加1"
                        }
                    }
                })

                addView(Button(context).apply {
                    text = "更新"
                    setOnClickListener {
                        val list = arrayListOf("111", "222", "333", "444", "555", "666", "777", "888")
                        myAdapter.update(list)
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
                    setOnClickListener {
                        myAdapter.removeAll()
                    }
                })
            }

            addView(title)
            addView(ll)
            addView(rv)
        }
        return rootView
    }

    fun rView(): View {
        val tv = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 8, 0, 8)
                gravity = Gravity.CENTER //设置中心
            }
            textSize = 18f
            setTextColor(Color.parseColor("#EE22EE"))
            text = "测试"
            tag = "tv"
        }
        val root = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            removeAllViews()
            orientation = LinearLayout.VERTICAL
            setPadding(15, 15, 15, 15)
            tag = "root"
        }
        root.addView(tv)
        return root
    }
}