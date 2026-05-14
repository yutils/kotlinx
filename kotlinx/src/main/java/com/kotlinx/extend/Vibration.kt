package com.kotlinx.extend

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission

private fun Context.systemVibrator(): Vibrator? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator
    } else {
        getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }
}

@RequiresPermission(Manifest.permission.VIBRATE)
@Suppress("DEPRECATION")
private fun legacyVibrateMs(vibrator: Vibrator, durationMs: Long) {
    vibrator.vibrate(durationMs)
}

/**
 * 短振动；需 Manifest 声明 [android.permission.VIBRATE]。
 */
@RequiresPermission(Manifest.permission.VIBRATE)
fun Context.vibrateShort(durationMs: Long = 40L) {
    val v = systemVibrator() ?: return
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        v.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        legacyVibrateMs(v, durationMs)
    }
}

/** 轻触感（不声明权限）。 */
fun View.performLightHapticTap() {
    performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
}

/** 确认类触感（不声明权限）。 */
@RequiresApi(Build.VERSION_CODES.R)
fun View.performConfirmHaptic() {
    performHapticFeedback(HapticFeedbackConstants.CONFIRM)
}
