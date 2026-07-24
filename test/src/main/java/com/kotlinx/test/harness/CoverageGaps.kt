package com.kotlinx.test.harness

/**
 * 覆盖缺口说明：需页面/系统授权的能力走 Manual 或演示页，下列刻意不强制 Auto。
 */
object CoverageGaps {
    val coveredKeywords = listOf(
        "String 校验(isInt/Email/URL/IP/银行卡等)",
        "String 变换(Base64/group/insert/数值转换)",
        "JSON(toJson/jsonToObject(+dateFormat)/GsonJson)",
        "数值日期(Double/Float/BigDecimal.fill/Date.format)",
        "文件流(File/ByteArray/InputStream/ExternalFile/getDir/path重载)",
        "剪贴板 / Prefs.putJson / dp·sp / Insets",
        "Throwable.rootCause / getLine / showStackTrace",
        "ui·io 协程 / debounce(+无key) / Result.toastOnFailure",
        "Bitmap(toByteArray/zoom/round/compress)",
        "集合(addAndReplace / List·MutableList.toString 扩展)",
        "Toast(toast/toastShort/toastLong + toastFilter)",
        "LogListener / vibrateShort / TTS(loopClose/onStop)",
        "TextView.marquee",
        "BaseViewAdapter CRUD / RecyclerView.init·show·BottomAdapter·showEmpty·滚动监听",
        "getSDCardRootUri / removeAllPermissions / removePermission(演示)",
        "registerRequestPermissionLauncher(通知权限演示)",
    )

    val intentionallySkipped = listOf(
        "View 手势(debounceClick/双击/长按/滑动) — 演示页 Manual",
        "EditText 软键盘开关 / monitorSoftKeyboard — Manual",
        "触感 performLightHapticTap / performConfirmHaptic — Manual",
        "TTS speak/Queue/Toast/loop* — Manual(听感)；destroy 会拆引擎故不强制",
        "SAF Uri.write/read — 需授权演示页",
        "BaseAdapter 网络分页示例代码 — 页面业务强依赖",
        "部分 scroll* 对称监听 — 演示页已挂常用几条，其余 API 同类",
    )

    fun overviewHint(registrySize: Int, autoSize: Int): String {
        return buildString {
            appendLine("用例 $registrySize 条（自动 $autoSize）")
            appendLine("已覆盖核心能力约 ${coveredKeywords.size} 项")
            appendLine("刻意不测：")
            intentionallySkipped.forEach { appendLine("· $it") }
        }.trimEnd()
    }
}
