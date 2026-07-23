package com.kotlinx.extend.view

import android.app.Activity
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View

/**
 * 键盘软键盘是否打开，打开返回 true，view 可以是任意 view。
 * 注意：依赖可见区域高度差估算，全屏/刘海机型上可能不够精确。
 */
fun View.monitorSoftKeyboard(activity: Activity, openListener: (Boolean) -> Unit) {
    this.viewTreeObserver.addOnGlobalLayoutListener {
        val rect = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(rect) //获取当前界面可视部分
        val screenHeight: Int = activity.window.decorView.rootView.height //获取屏幕高度
        val heiDifference: Int = screenHeight - rect.bottom //键盘未弹出时约为 0，弹出时为正数
        // heiDifference > 0 表示键盘打开
        openListener(heiDifference > 0)
    }
}

/**
 * 防抖点击方法 自定义时长
 * （setOnClickListener 本身已支持 Accessibility，勿在回调里再调 performClick，否则会死递归）
 */
fun View.setCustomOnClickListener(debounceMs: Long = 500, action: (View) -> Unit) {
    var lastClickTime = 0L
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= debounceMs) {
            lastClickTime = currentTime
            action(this)
        }
    }
}

/**
 * 防抖点击方法 自定义时长
 */
fun View.debounceClick(debounceMs: Long = 500, action: (View) -> Unit) {
    var lastClickTime = 0L
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime >= debounceMs) {
            lastClickTime = currentTime
            action(this)
        }
    }
}

/**
 * 长按方法 自定义时长
 */
fun View.setCustomLongClickListener(durationMs: Long = 4000L, action: (View) -> Unit) {
    val handler = Handler(Looper.getMainLooper())
    val longPressRunnable = Runnable {
        action(this) // 5秒后直接触发
        isPressed = false // 恢复按钮状态，取消按下效果
    }

    setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                v.isPressed = true // 触发按下动画
                handler.postDelayed(longPressRunnable, durationMs)
                true
            }

            MotionEvent.ACTION_UP -> {
                handler.removeCallbacks(longPressRunnable) // 松手时取消长按
                if (event.eventTime - event.downTime < 500) { // 短按触发点击
                    v.performClick()
                }
                v.isPressed = false // 恢复按钮状态
                false
            }

            MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacks(longPressRunnable) // 取消时移除长按
                v.isPressed = false // 恢复按钮状态
                false
            }

            else -> false
        }
    }
}

/**
 * 双击监听 自定义时长
 */
fun View.setCustomDoubleClickListener(doubleClickIntervalMs: Long = 300, action: (View) -> Unit) {
    this.setCustomMultipleClickListener(doubleClickIntervalMs, 2, action)
}


/**
 * 多击监听 自定义时长  指定时间内点击多次
 */
fun View.setCustomMultipleClickListener(doubleClickIntervalMs: Long = 3000, clickCount: Int = 5, action: (View) -> Unit) {
    var lastClickTime = 0L
    var count = 0
    setOnClickListener {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime <= doubleClickIntervalMs) {
            count++
            if (count == clickCount) {
                action(this)
                count = 0 // 重置计数
            }
        } else {
            count = 1 // 第一次点击
        }
        lastClickTime = currentTime
    }
}

/**
 * 滑动监听方法（支持左右和上下滑动）
 */
/*
binding.btTzzSyn.setCustomSwipeListener(
    onSwipeLeft = { Toast.makeText(this, "左滑", Toast.LENGTH_SHORT).show() },
    onSwipeRight = { Toast.makeText(this, "右滑", Toast.LENGTH_SHORT).show() },
    onSwipeUp = { Toast.makeText(this, "上滑", Toast.LENGTH_SHORT).show() },
    onSwipeDown = { Toast.makeText(this, "下滑", Toast.LENGTH_SHORT).show() }
)
 */
fun View.setCustomSwipeListener(
    minSwipeDistance: Float = 100f, // 最小滑动距离
    onSwipeLeft: (View) -> Unit = {},
    onSwipeRight: (View) -> Unit = {},
    onSwipeUp: (View) -> Unit = {},
    onSwipeDown: (View) -> Unit = {},
) {
    var startX = 0f
    var startY = 0f
    setOnTouchListener { v, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y
                v.isPressed = true // 触发按下动画
                true
            }

            MotionEvent.ACTION_UP -> {
                val endX = event.x
                val endY = event.y
                val distanceX = endX - startX
                val distanceY = endY - startY
                // 优先检测水平滑动
                if (kotlin.math.abs(distanceX) > kotlin.math.abs(distanceY) && kotlin.math.abs(distanceX) > minSwipeDistance) {
                    if (distanceX > 0) {
                        onSwipeRight(v)
                    } else {
                        onSwipeLeft(v)
                    }
                }
                // 检测垂直滑动
                else if (kotlin.math.abs(distanceY) > minSwipeDistance) {
                    if (distanceY > 0) {
                        onSwipeDown(v)
                    } else {
                        onSwipeUp(v)
                    }
                }
                v.isPressed = false // 恢复按钮状态
                if (event.eventTime - event.downTime < 500) {
                    v.performClick() // 短按触发 Accessibility
                }
                false
            }

            MotionEvent.ACTION_CANCEL -> {
                v.isPressed = false // 恢复按钮状态
                false
            }

            else -> false
        }
    }
}

