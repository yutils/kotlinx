package com.kotlinx.test

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.view.setPadding
import com.kotlinx.extend.performConfirmHaptic
import com.kotlinx.extend.performLightHapticTap
import com.kotlinx.extend.toast
import com.kotlinx.extend.view.closeSoftKeyboard
import com.kotlinx.extend.view.debounceClick
import com.kotlinx.extend.view.marquee
import com.kotlinx.extend.view.monitorSoftKeyboard
import com.kotlinx.extend.view.openSoftKeyboard
import com.kotlinx.extend.view.setCustomDoubleClickListener
import com.kotlinx.extend.view.setCustomLongClickListener
import com.kotlinx.extend.view.setCustomMultipleClickListener
import com.kotlinx.extend.view.setCustomOnClickListener
import com.kotlinx.extend.view.setCustomSwipeListener

/** View 手势 / 触感 / 软键盘 / 跑马灯演示（人工确认） */
class ViewGestureDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48)
        }
        val tip = TextView(this).apply {
            text = "操作下方控件，确认正常后返回测试台点「通过」"
            textSize = 15f
        }
        root.addView(tip)

        val marqueeTv = TextView(this).apply {
            text = "跑马灯测试：这段文字足够长才会滚动显示效果——————尾部"
            textSize = 16f
            marquee()
        }
        root.addView(marqueeTv)

        fun addBtn(label: String, block: Button.() -> Unit) {
            root.addView(Button(this).apply {
                text = label
                block()
            })
        }

        addBtn("防抖点击 debounceClick(500)") {
            debounceClick(500) { "防抖点击触发".toast() }
        }
        addBtn("防抖 setCustomOnClickListener") {
            setCustomOnClickListener(500) { "CustomOnClick 触发".toast() }
        }
        addBtn("双击触发") {
            setCustomDoubleClickListener { "双击触发".toast() }
        }
        addBtn("5 连击（3 秒内）") {
            setCustomMultipleClickListener(3000, 5) { "五连击触发".toast() }
        }
        addBtn("自定义长按（1 秒）") {
            setCustomLongClickListener(1000) { "长按触发".toast() }
        }
        addBtn("滑动（上下左右）") {
            setCustomSwipeListener(
                onSwipeLeft = { "左滑".toast() },
                onSwipeRight = { "右滑".toast() },
                onSwipeUp = { "上滑".toast() },
                onSwipeDown = { "下滑".toast() },
            )
        }
        addBtn("轻触感 KEYBOARD_TAP") {
            setOnClickListener { performLightHapticTap() }
        }
        addBtn("确认触感 CONFIRM") {
            setOnClickListener { performConfirmHaptic() }
        }

        val kbState = TextView(this).apply {
            text = "软键盘状态：—"
            textSize = 14f
        }
        root.addView(kbState)
        root.monitorSoftKeyboard(this) { open ->
            kbState.text = "软键盘状态：${if (open) "打开" else "关闭"}"
        }

        val et = EditText(this).apply {
            hint = "点此输入测软键盘"
        }
        root.addView(et)
        addBtn("打开软键盘") {
            setOnClickListener {
                et.requestFocus()
                et.openSoftKeyboard(this@ViewGestureDemoActivity)
            }
        }
        addBtn("关闭软键盘") {
            setOnClickListener { et.closeSoftKeyboard(this@ViewGestureDemoActivity) }
        }

        setContentView(ScrollView(this).apply { addView(root) })
    }
}
