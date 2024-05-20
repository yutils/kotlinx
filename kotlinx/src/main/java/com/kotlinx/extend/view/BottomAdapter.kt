package com.kotlinx.extend.view

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.text.scale
import androidx.core.view.isVisible


/**
 * 底部显示"没有更多数据"的Adapter
 */
/*
举例:
var bottomAdapter = BottomAdapter(this, "没有更多数据")
val config = ConcatAdapter.Config.Builder().setIsolateViewTypes(true).build()
val concatAdapter = ConcatAdapter(config, myAdapter, bottomAdapter)

myAdapter.dataChangeListener = {
    if (it.size == 0) { //列表为空
        bottomAdapter.matchParent("暂无数据")
    } else {
        if (it.size > 20) {//模拟，已经是最后一页
            bottomAdapter.wrapContent("没有更多数据")
        } else {
            bottomAdapter.hide()
        }
    }
}
 */
class BottomAdapter(val context: Context, var tips: CharSequence = "没有更多数据", var listener: ((textView: TextView) -> Unit)? = null) : BaseViewAdapter<CharSequence>(mutableListOf(tips)) {
    private var height = LinearLayout.LayoutParams.WRAP_CONTENT
    private var isShow = true

    var holder: BaseViewHolder? = null
    override fun getView(): View {
        return getBottomView(context)
    }

    //每次从屏幕底部滚动出来都会产生一个新的holder，view也是新的，该方法会重新被调用
    override fun item(holder: BaseViewHolder, position: Int) {
        this.holder = holder
        holder.root.isVisible = isShow
        val textView = holder.root.findViewWithTag<TextView>("TextView")
        textView.text = tips
        listener?.invoke(textView)
    }

    fun wrapContent(content: CharSequence? = null) {
        show()
        height = LinearLayout.LayoutParams.WRAP_CONTENT
        holder?.root?.findViewWithTag<LinearLayout>("root")?.let {
            it.layoutParams.height = height
            it.gravity = Gravity.CENTER
        }
        tips = buildSpannedString {
            scale(1F) { append(content ?: tips) }
        }
    }

    fun matchParent(content: CharSequence? = null) {
        show()
        height = LinearLayout.LayoutParams.MATCH_PARENT
        holder?.root?.findViewWithTag<LinearLayout>("root")?.let {
            it.layoutParams.height = height
            it.gravity = Gravity.CENTER
        }
        tips = buildSpannedString {
            scale(1.6F) { append(content ?: tips) }
        }
    }

    fun hide() {
        isShow = false
        holder?.root?.isVisible = isShow
    }

    fun show() {
        isShow = true
        holder?.root?.isVisible = isShow
    }

    /**
     * 一个LinearLayout布局，高度为WRAP_CONTENT，中心显示提示文字
     */
    fun getBottomView(context: Context): LinearLayout {
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height)
        layoutParams.gravity = Gravity.CENTER //设置中心对其
        val linearLayout = LinearLayout(context)
        linearLayout.layoutParams = layoutParams
        linearLayout.gravity = Gravity.CENTER
        linearLayout.removeAllViews()
        linearLayout.orientation = LinearLayout.VERTICAL //设置纵向布局
        linearLayout.tag = "root"
        //文字
        val tvParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        tvParams.gravity = Gravity.CENTER //TextView在父布局里面居中
        //实例化一个TextView
        val tv = TextView(context)
        tv.layoutParams = tvParams
        tv.gravity = Gravity.CENTER //文字在TextView里面居中
        tv.textSize = 14f
        tv.setTextColor(Color.parseColor("#999999"))
        tv.tag = "TextView"
        //mEmptyText?.let { tv.text = mEmptyText }
        linearLayout.addView(tv)
        return linearLayout
    }
}