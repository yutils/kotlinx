package com.kotlinx.extend.view

import android.app.Activity
import android.graphics.Rect
import android.view.View

/**
 * 键盘软键盘是否打开，打开返回true，view可以是任意view
 */
fun View.monitorSoftKeyboard(activity: Activity, openListener: (Boolean) -> Unit) {
    this.viewTreeObserver.addOnGlobalLayoutListener {
        val rect = Rect()
        activity.window.decorView.getWindowVisibleDisplayFrame(rect) //获取当前界面可视部分
        val screenHeight: Int = activity.window.decorView.rootView.height //获取屏幕高度
        val heiDifference: Int = screenHeight - rect.bottom //获取键盘高度，键盘没有弹出时，高度为0，键盘弹出时，高度为正数
        if (heiDifference == 0) {
            openListener(true)
        } else {
            openListener(false)
        }
    }
}