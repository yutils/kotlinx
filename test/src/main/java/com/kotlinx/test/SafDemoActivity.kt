package com.kotlinx.test

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.setPadding
import com.kotlinx.extend.getSDCardRootUri
import com.kotlinx.extend.logI
import com.kotlinx.extend.readFileFromFolder
import com.kotlinx.extend.removeAllPermissions
import com.kotlinx.extend.removePermission
import com.kotlinx.extend.toast
import com.kotlinx.extend.writeFileToFolder

/** SAF 文档树演示 */
class SafDemoActivity : ComponentActivity() {
    private lateinit var logView: TextView

    private val openTree = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
        if (uri == null) {
            append("用户取消选择")
            return@registerForActivityResult
        }
        contentResolver.takePersistableUriPermission(
            uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
        )
        append("已授权: $uri")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48)
        }
        logView = TextView(this).apply {
            textSize = 13f
            text = "日志：\n"
        }

        fun btn(label: String, action: () -> Unit) {
            root.addView(Button(this).apply {
                text = label
                setOnClickListener { action() }
            })
        }

        btn("申请 SAF 目录权限") {
            if (contentResolver.persistedUriPermissions.isNotEmpty()) {
                "已有授权".toast().logI()
                append("已有授权 ${contentResolver.persistedUriPermissions.size} 个")
                return@btn
            }
            openTree.launch(getSDCardRootUri(this))
        }
        btn("写入 test.txt") {
            val uri = contentResolver.persistedUriPermissions.firstOrNull()?.uri
            if (uri == null) {
                "没有授权文件夹".toast()
                return@btn
            }
            val ok = uri.writeFileToFolder(this, "kotlinx-saf-${System.currentTimeMillis()}", "test.txt")
            (if (ok) "写入成功" else "写入失败").toast()
            append(if (ok) "写入成功" else "写入失败")
        }
        btn("读取 test.txt") {
            val uri = contentResolver.persistedUriPermissions.firstOrNull()?.uri
            if (uri == null) {
                "没有授权文件夹".toast()
                return@btn
            }
            val value = uri.readFileFromFolder(this, "test.txt")
            "读取: $value".toast()
            append("读取: $value")
        }
        btn("移除当前这条 URI 权限") {
            val uri = contentResolver.persistedUriPermissions.firstOrNull()?.uri
            if (uri == null) {
                "没有可移除的权限".toast()
                return@btn
            }
            val ok = uri.removePermission(this)
            append(if (ok) "已移除单条: $uri" else "移除单条失败")
            (if (ok) "已移除单条" else "移除失败").toast()
        }
        btn("移除全部 SAF 权限") {
            removeAllPermissions(this)
            append("已移除权限")
            "已移除".toast()
        }

        root.addView(logView)
        setContentView(ScrollView(this).apply { addView(root) })
    }

    private fun append(msg: String) {
        logView.append("$msg\n")
    }
}
