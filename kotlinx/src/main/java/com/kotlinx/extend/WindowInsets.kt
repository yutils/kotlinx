package com.kotlinx.extend

import android.app.Activity
import androidx.core.graphics.Insets
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun Activity.windowInsetsCompat(): WindowInsetsCompat? =
    ViewCompat.getRootWindowInsets(window.decorView)

/** 状态栏上 inset（px）。 */
fun Activity.statusBarsTopPx(): Int =
    windowInsetsCompat()?.getInsets(WindowInsetsCompat.Type.statusBars())?.top ?: 0

/** 导航栏下 inset（px）。 */
fun Activity.navigationBarsBottomPx(): Int =
    windowInsetsCompat()?.getInsets(WindowInsetsCompat.Type.navigationBars())?.bottom ?: 0

/** 系统手势栏 inset（侧滑返回等区域，依机型而定）。 */
fun Activity.systemGesturesInsets(): Insets =
    windowInsetsCompat()?.getInsets(WindowInsetsCompat.Type.systemGestures())
        ?: Insets.NONE
