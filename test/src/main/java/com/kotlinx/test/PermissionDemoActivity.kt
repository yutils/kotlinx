package com.kotlinx.test

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.core.view.setPadding
import com.kotlinx.extend.registerRequestPermissionLauncher
import com.kotlinx.extend.toast

/**
 * 演示 [registerRequestPermissionLauncher]：API 33+ 请求 POST_NOTIFICATIONS。
 * 测完返回测试台，对「perm.notification」点「通过」或「失败」。
 */
class PermissionDemoActivity : ComponentActivity() {

    private lateinit var statusTv: TextView

    private val notificationPermission = registerRequestPermissionLauncher { granted ->
        val msg = if (granted) "已授予通知权限" else "用户拒绝通知权限"
        msg.toast()
        refreshStatus()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48)
        }

        root.addView(TextView(this).apply {
            text = "权限扩展演示\nregisterRequestPermissionLauncher"
            textSize = 18f
        })
        root.addView(TextView(this).apply {
            text = "点击下方按钮会弹出系统权限框（Android 13+）。测完后返回测试台点「通过」或「失败」。"
            textSize = 14f
            setPadding(0, 24, 0, 24)
        })

        statusTv = TextView(this).apply { textSize = 15f }
        root.addView(statusTv)
        refreshStatus()

        root.addView(Button(this).apply {
            text = "请求通知权限 POST_NOTIFICATIONS"
            setOnClickListener { requestNotificationPermission() }
        })
        root.addView(Button(this).apply {
            text = "刷新当前状态"
            setOnClickListener { refreshStatus() }
        })

        setContentView(ScrollView(this).apply { addView(root) })
    }

    override fun onResume() {
        super.onResume()
        if (::statusTv.isInitialized) refreshStatus()
    }

    private fun refreshStatus() {
        val sdk = Build.VERSION.SDK_INT
        val text = when {
            sdk < Build.VERSION_CODES.TIRAMISU ->
                "当前 API $sdk（< 33）：系统不要求 POST_NOTIFICATIONS，可直接点通过"

            else -> {
                val granted = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS,
                ) == PackageManager.PERMISSION_GRANTED
                "当前 API $sdk\n通知权限：${if (granted) "已授权 ✓" else "未授权"}"
            }
        }
        statusTv.text = text
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            "当前系统 < API 33，无需请求通知权限".toast()
            refreshStatus()
            return
        }
        val granted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) {
            "已经有通知权限了".toast()
            refreshStatus()
            return
        }
        notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
