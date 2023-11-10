package com.kotlinx.extend.view

import android.app.Activity
import android.content.Context
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

/**
 * 打开软键盘
 */
fun EditText.openSoftKeyboard(activity: Activity): Boolean {
    val manager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    return manager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
}

/**
 * 关闭软键盘
 */
fun EditText.closeSoftKeyboard(activity: Activity): Boolean {
    val manager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    return manager.hideSoftInputFromWindow(this.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
}