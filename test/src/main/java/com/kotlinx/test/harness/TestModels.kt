package com.kotlinx.test.harness

import android.content.Context

enum class TestKind {
    /** 可自动断言，无需人工判断 */
    AUTO,

    /** 需人工确认观感/听感，或需系统授权 */
    MANUAL,
}

enum class TestStatus {
    IDLE,
    RUNNING,
    PASS,
    FAIL,
    SKIP,
}

enum class TestCategory(
    val title: String,
    val subtitle: String,
) {
    STRING_VALIDATE("字符串校验", "isInt / Email / URL / IP / 银行卡等"),
    STRING_TRANSFORM("字符串变换", "Base64 / group / insert / 数值转换"),
    JSON("JSON / Gson", "toJson / jsonToObject / JsonElement 安全取值"),
    NUMBER_DATE("数值与日期", "Double/Float/BigDecimal / Date.format"),
    FILE_IO("文件与流", "File / ByteArray / InputStream / ExternalFile / addFile"),
    ANDROID_UTILS("Android 工具", "剪贴板 / Prefs / dp/sp / Insets / 协程 / 防抖 / 堆栈"),
    BITMAP("Bitmap", "压缩 / 缩放 / 圆角 / toBitmap"),
    COLLECTION("集合", "addAndReplace / toUnit"),
    INTERACTIVE("交互反馈", "Toast / LogListener / 振动 / 触感 / TTS"),
    VIEW("View 手势", "防抖 / 多击 / 长按 / 滑动 / 跑马灯 / 软键盘"),
    RECYCLER("RecyclerView", "BaseViewAdapter / init / BottomAdapter / 空态"),
    SAF("SAF 文档树", "授权目录读写 / getSDCardRootUri"),
}

data class TestCaseDef(
    val id: String,
    val title: String,
    val category: TestCategory,
    val kind: TestKind,
    val description: String = "",
    /** AUTO：抛异常即 FAIL；MANUAL：可为空，点击后执行副作用 */
    val run: ((Context) -> Unit)? = null,
)

data class TestCaseState(
    val def: TestCaseDef,
    val status: TestStatus = TestStatus.IDLE,
    val detail: String = "",
    val durationMs: Long = 0L,
)

data class CategorySummary(
    val category: TestCategory,
    val total: Int,
    val pass: Int,
    val fail: Int,
    val idle: Int,
    val manual: Int,
) {
    val coverageLabel: String
        get() = if (total == 0) "0/0" else "${pass + fail}/$total"
}
