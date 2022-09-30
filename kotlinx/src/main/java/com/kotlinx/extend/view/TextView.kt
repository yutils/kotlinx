package com.kotlinx.extend.view

import android.text.TextUtils
import android.widget.TextView

/**
 * 跑马灯效果
 */
fun TextView.marquee() {
    this.isSingleLine = true
    this.ellipsize = TextUtils.TruncateAt.MARQUEE
    this.marqueeRepeatLimit = Integer.MAX_VALUE
    this.isSelected = true
}