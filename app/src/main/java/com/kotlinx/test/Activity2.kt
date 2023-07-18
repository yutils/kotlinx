package com.kotlinx.test

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kotlinx.Kotlinx
import com.kotlinx.extend.view.BaseViewAdapter
import com.kotlinx.extend.view.BaseViewHolder
import com.kotlinx.extend.view.init
import com.kotlinx.extend.view.showEmpty

class Activity2 : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(rootView())
    }

    private fun rootView(): View {
        val root = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
            removeAllViews()
            orientation = LinearLayout.VERTICAL
            setPadding(15, 15, 15, 15)
        }
        val tv = TextView(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).apply {
                setMargins(0, 0, 0, 0)
                gravity = Gravity.CENTER //设置中心
            }
            textSize = 18f
            setTextColor(Color.parseColor("#EE22EE"))
            text = "测试"
        }


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

        val ll = LinearLayout(this).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            removeAllViews()
            orientation = LinearLayout.HORIZONTAL

            addView(Button(this@Activity2).apply {
                text = "添加"
                setOnClickListener {
//                    myAdapter.add("17")
//                    myAdapter.add("18")
//                    myAdapter.add("19")
//                    myAdapter.add("20")

                    val list = arrayListOf("添加1", "添加2", "添加3")
                    myAdapter.add(list)

//                    myAdapter.add("888", 17)
                }
            })
            addView(Button(this@Activity2).apply {
                text = "更新"
                setOnClickListener {
                    val list = arrayListOf("111", "222", "333", "444", "555", "666", "777", "888")
                    myAdapter.update(list)

//                    myAdapter.update("00000", 3)
                }
            })
            addView(Button(this@Activity2).apply {
                text = "删除"
                setOnClickListener {
                    myAdapter.removeAt(3)
                    myAdapter.removeAt(5)
                }
            })
            addView(Button(this@Activity2).apply {
                text = "删完"
                setOnClickListener {
                    myAdapter.removeAll()
                }
            })
        }

        root.addView(tv)
        root.addView(ll)
        root.addView(recyclerView2)
        return root
    }

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
}