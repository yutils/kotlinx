package com.kotlinx.extend

import android.content.Context
import android.util.TypedValue
import kotlin.math.roundToInt

/** dp 转 px（四舍五入）。 */
fun Int.dp(context: Context): Int =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this.toFloat(),
        context.resources.displayMetrics,
    ).roundToInt()

fun Float.dp(context: Context): Int =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        this,
        context.resources.displayMetrics,
    ).roundToInt()

/** sp 转 px（四舍五入）。 */
fun Int.sp(context: Context): Int =
    TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        this.toFloat(),
        context.resources.displayMetrics,
    ).roundToInt()

/** px 转 dp（浮点）。 */
fun Int.pxToDp(context: Context): Float =
    this / context.resources.displayMetrics.density

/** px 转 sp（浮点）。基于 DisplayMetrics.scaledDensity，可能与 TextView 真实换算略有偏差。 */
@Suppress("DEPRECATION")
fun Int.pxToSp(context: Context): Float =
    this / context.resources.displayMetrics.scaledDensity
