package com.kotlinx.test.harness

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kotlinx.extend.LogListener
import com.kotlinx.extend.addAndReplace
import com.kotlinx.extend.addFile
import com.kotlinx.extend.arrayOrNull
import com.kotlinx.extend.asBooleanOrNull
import com.kotlinx.extend.asDoubleOrNull
import com.kotlinx.extend.asIntOrNull
import com.kotlinx.extend.asLongOrNull
import com.kotlinx.extend.asStringOrNull
import com.kotlinx.extend.base64Decode
import com.kotlinx.extend.booleanOrNull
import com.kotlinx.extend.clipboardManager
import com.kotlinx.extend.compressToBytes
import com.kotlinx.extend.copyToClipboard
import com.kotlinx.extend.delFileOrDir
import com.kotlinx.extend.doubleOrNull
import com.kotlinx.extend.dp
import com.kotlinx.extend.fill
import com.kotlinx.extend.format
import com.kotlinx.extend.getJsonOrNull
import com.kotlinx.extend.getLine
import com.kotlinx.extend.getSDCardRootUri
import com.kotlinx.extend.group
import com.kotlinx.extend.groupActual
import com.kotlinx.extend.groupDouble
import com.kotlinx.extend.insert
import com.kotlinx.extend.intOrNull
import com.kotlinx.extend.isBankCard
import com.kotlinx.extend.isChinese
import com.kotlinx.extend.isDouble
import com.kotlinx.extend.isDoubleOrIntToDouble
import com.kotlinx.extend.isDoubleToDouble
import com.kotlinx.extend.isEmail
import com.kotlinx.extend.isEmptyOrInt
import com.kotlinx.extend.isEmptyOrIntOrDouble
import com.kotlinx.extend.isEnglish
import com.kotlinx.extend.isEnglishAndNumber
import com.kotlinx.extend.isHttp
import com.kotlinx.extend.isIPv4
import com.kotlinx.extend.isIPv6
import com.kotlinx.extend.isInt
import com.kotlinx.extend.isIntOrDouble
import com.kotlinx.extend.isIntToInt
import com.kotlinx.extend.isNumber
import com.kotlinx.extend.isPort
import com.kotlinx.extend.isUrl
import com.kotlinx.extend.isisEnglishAndNumberAndUnderline
import com.kotlinx.extend.jsonToObject
import com.kotlinx.extend.jsonToObjectOrNull
import com.kotlinx.extend.keepDecimalPlaces
import com.kotlinx.extend.kotlinxPrefs
import com.kotlinx.extend.logD
import com.kotlinx.extend.logE
import com.kotlinx.extend.logI
import com.kotlinx.extend.logV
import com.kotlinx.extend.logW
import com.kotlinx.extend.longOrNull
import com.kotlinx.extend.navigationBarsBottomPx
import com.kotlinx.extend.notScience
import com.kotlinx.extend.objOrNull
import com.kotlinx.extend.oneLineMessage
import com.kotlinx.extend.parseDate
import com.kotlinx.extend.parseJsonArrayOrNull
import com.kotlinx.extend.parseJsonElementOrNull
import com.kotlinx.extend.parseJsonObjectOrNull
import com.kotlinx.extend.primaryClipPlainText
import com.kotlinx.extend.putJson
import com.kotlinx.extend.pxToDp
import com.kotlinx.extend.pxToSp
import com.kotlinx.extend.readOnce
import com.kotlinx.extend.readPath
import com.kotlinx.extend.readString
import com.kotlinx.extend.removeAllPermissions
import com.kotlinx.extend.removeKey
import com.kotlinx.extend.rootCause
import com.kotlinx.extend.round
import com.kotlinx.extend.showStackTrace
import com.kotlinx.extend.sp
import com.kotlinx.extend.speak
import com.kotlinx.extend.speakQueue
import com.kotlinx.extend.statusBarsTopPx
import com.kotlinx.extend.string
import com.kotlinx.extend.stringOrNull
import com.kotlinx.extend.systemGesturesInsets
import com.kotlinx.extend.toBase64
import com.kotlinx.extend.toBase64Decode
import com.kotlinx.extend.toBase64Encode
import com.kotlinx.extend.toBase64EncodeToString
import com.kotlinx.extend.toBase64String
import com.kotlinx.extend.toBitmap
import com.kotlinx.extend.toByteArray
import com.kotlinx.extend.toBytes
import com.kotlinx.extend.toFile
import com.kotlinx.extend.toJson
import com.kotlinx.extend.toStringFromBase64
import com.kotlinx.extend.toUnit
import com.kotlinx.extend.toast
import com.kotlinx.extend.toastFilter
import com.kotlinx.extend.toastLong
import com.kotlinx.extend.toastOnFailure
import com.kotlinx.extend.toastShort
import com.kotlinx.extend.vibrateShort
import com.kotlinx.extend.view.BaseViewAdapter
import com.kotlinx.extend.view.BaseViewHolder
import com.kotlinx.extend.view.BottomAdapter
import com.kotlinx.extend.view.init
import com.kotlinx.extend.view.marquee
import com.kotlinx.extend.view.notToTopListener
import com.kotlinx.extend.view.show
import com.kotlinx.extend.view.showEmpty
import com.kotlinx.extend.view.toBottomListener
import com.kotlinx.extend.windowInsetsCompat
import com.kotlinx.extend.writePath
import com.kotlinx.extend.zoom
import com.kotlinx.test.R
import com.kotlinx.test.databinding.TestItemBinding
import com.kotlinx.utils.ExternalFile
import com.kotlinx.utils.TTS
import com.kotlinx.utils.debounce
import com.kotlinx.utils.io
import com.kotlinx.utils.isUI
import com.kotlinx.utils.mainHandler
import com.kotlinx.utils.ui
import java.io.ByteArrayInputStream
import java.io.File
import java.math.BigDecimal
import java.util.Date
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import com.kotlinx.extend.toString as extToString

internal fun assertTrue(cond: Boolean, msg: String) {
    if (!cond) error(msg)
}

internal fun assertEq(actual: Any?, expected: Any?, msg: String = "期望=$expected 实际=$actual") {
    if (actual != expected) error(msg)
}

/** 排空主线程队列，避免批量跑时前面 toast 的 ui{} 抢跑当前 toastFilter */
internal fun awaitMainIdle(timeoutMs: Long = 3000L) {
    val latch = CountDownLatch(1)
    mainHandler.post { latch.countDown() }
    assertTrue(latch.await(timeoutMs, TimeUnit.MILLISECONDS), "主线程空闲等待超时")
}

/** 在主线程执行（DataBinding / 部分 View API 要求） */
internal fun <T> runOnMain(timeoutSec: Long = 10, block: () -> T): T {
    val result = AtomicReference<T?>()
    val error = AtomicReference<Throwable?>()
    val latch = CountDownLatch(1)
    mainHandler.post {
        try {
            result.set(block())
        } catch (t: Throwable) {
            error.set(t)
        } finally {
            latch.countDown()
        }
    }
    assertTrue(latch.await(timeoutSec, TimeUnit.SECONDS), "主线程任务超时")
    error.get()?.let { throw it }
    @Suppress("UNCHECKED_CAST")
    return result.get() as T
}

data class PrefBean(val id: Int, val name: String)

/** 全部用例目录：AUTO 尽量断言；MANUAL 仅保留确需人工确认的项 */
fun buildAllTestCases(): List<TestCaseDef> = buildList {
    // ---------- 字符串校验 ----------
    add(auto(TestCategory.STRING_VALIDATE, "str.isInt", "isInt 正负与非法") {
        assertTrue("123".isInt() && "-7".isInt() && !"12.3".isInt() && !"01".isInt(), "isInt 判定错误")
    })
    add(auto(TestCategory.STRING_VALIDATE, "str.isDouble", "isDouble") {
        assertTrue("1.5".isDouble() && !"1".isDouble() && !".".isDouble(), "isDouble 判定错误")
    })
    add(auto(TestCategory.STRING_VALIDATE, "str.isEmail", "isEmail") {
        assertTrue("a@b.com".isEmail() && !"a@b".isEmail(), "isEmail 判定错误")
    })
    add(auto(TestCategory.STRING_VALIDATE, "str.isEnglish", "isEnglish / isChinese") {
        assertTrue("Abc".isEnglish() && !"A1".isEnglish(), "isEnglish")
        assertTrue("你好".isChinese() && !"你好a".isChinese(), "isChinese")
    })
    add(auto(TestCategory.STRING_VALIDATE, "str.isIPv4", "isIPv4") {
        assertTrue("192.168.1.1".isIPv4() && !"256.1.1.1".isIPv4(), "isIPv4")
    })
    add(auto(TestCategory.STRING_VALIDATE, "str.isPort", "isPort") {
        assertTrue("8080".isPort() && "65535".isPort() && !"65536".isPort(), "isPort")
    })
    add(auto(TestCategory.STRING_VALIDATE, "str.isBankCard", "银行卡长度边界") {
        assertTrue(!"123".isBankCard(), "过短应 false")
        assertTrue("4111111111111111".isBankCard(), "样例卡号 Luhn 应通过")
    })
    add(auto(TestCategory.STRING_VALIDATE, "str.isNumber.family", "isIntOrDouble / isNumber / isEmptyOr*") {
        assertTrue("12".isIntOrDouble() && "1.2".isIntOrDouble() && !"a".isIntOrDouble(), "isIntOrDouble")
        assertTrue("3".isNumber() && "3.14".isNumber(), "isNumber")
        assertTrue("".isEmptyOrInt() && "9".isEmptyOrInt() && !"x".isEmptyOrInt(), "isEmptyOrInt")
        assertTrue("".isEmptyOrIntOrDouble() && "1.5".isEmptyOrIntOrDouble(), "isEmptyOrIntOrDouble")
    })
    add(auto(TestCategory.STRING_VALIDATE, "str.isEnglishAndNumber", "英文数字 / 下划线") {
        assertTrue("Ab12".isEnglishAndNumber() && !"Ab-12".isEnglishAndNumber(), "isEnglishAndNumber")
        assertTrue("Ab_12".isisEnglishAndNumberAndUnderline() && !"Ab-12".isisEnglishAndNumberAndUnderline(), "underline")
    })
    add(auto(TestCategory.STRING_VALIDATE, "str.isUrl.http.ipv6", "isUrl / isHttp / isIPv6") {
        assertTrue("https://a.com".isUrl() || "www.a.com".isUrl(), "isUrl")
        assertTrue("https://www.example.com".isHttp(), "isHttp")
        assertTrue(!"notaurl".isHttp(), "isHttp 负例")
        assertTrue("::1".isIPv6() || "2001:db8::1".isIPv6(), "isIPv6")
    })

    // ---------- 字符串变换 ----------
    add(auto(TestCategory.STRING_TRANSFORM, "str.base64.roundtrip", "toBase64String ↔ toStringFromBase64") {
        val raw = "你好Kotlinx-测试"
        assertEq(raw.toBase64String().toStringFromBase64(), raw)
    })
    add(auto(TestCategory.STRING_TRANSFORM, "bytes.base64.roundtrip", "ByteArray Base64 编解码") {
        val raw = "abcXYZ123".toByteArray()
        val enc = raw.toBase64EncodeToString()
        assertEq(String(enc.toByteArray(Charsets.US_ASCII).toBase64Decode()), "abcXYZ123")
        val encBytes = raw.toBase64Encode()
        assertEq(String(encBytes.toBase64Decode()), "abcXYZ123")
    })
    add(auto(TestCategory.STRING_TRANSFORM, "str.base64Decode.toBase64", "String.base64Decode / toBase64") {
        val s = "hello"
        val b64 = s.toBase64String()
        assertEq(String(b64.base64Decode()), s)
        assertTrue(s.toBase64().isNotEmpty(), "toBase64 空")
    })
    add(auto(TestCategory.STRING_TRANSFORM, "str.group", "group / insert") {
        val g = "你好啊1234567890".group(3)
        assertEq(g.size, 5)
        assertEq(g[0].toString(), "你好啊")
        assertTrue("你好啊123".insert(3, "-").contains("-"), "insert 未插入")
    })
    add(auto(TestCategory.STRING_TRANSFORM, "str.groupDouble.actual", "groupDouble / groupActual") {
        val gd = "你好啊123".groupDouble(3)
        assertTrue(gd.isNotEmpty(), "groupDouble 空")
        val ga = "你好啊123456".groupActual(6)
        assertTrue(ga.isNotEmpty() && ga.joinToString("") { it.toString() }.startsWith("你"), "groupActual")
    })
    add(auto(TestCategory.STRING_TRANSFORM, "str.isIntToInt", "isIntToInt / isDoubleToDouble") {
        assertEq("42".isIntToInt(), 42)
        assertEq("x".isIntToInt(-1), -1)
        assertEq("1.5".isDoubleToDouble(), 1.5)
        assertEq("x".isDoubleToDouble(-2.0), -2.0)
        assertEq("3".isDoubleOrIntToDouble(), 3.0)
        assertEq("2.5".isDoubleOrIntToDouble(), 2.5)
    })

    // ---------- JSON ----------
    add(auto(TestCategory.JSON, "json.toJson.jsonToObject", "实体 ↔ JSON") {
        data class Bean(val id: Int, val name: String?)

        val list = listOf(Bean(1, "a"), Bean(2, "b"))
        val json = list.toJson()
        val back = json.jsonToObject<List<Bean>>()
        assertEq(back?.size, 2)
        assertEq(back?.get(0)?.id, 1)
    })
    add(auto(TestCategory.JSON, "json.jsonToObjectOrNull", "非法 JSON / null List") {
        assertEq(null.jsonToObjectOrNull<List<Any>>()?.size, 0)
        assertEq("{bad".jsonToObjectOrNull<Map<String, Any>>(), null)
        assertEq(null.jsonToObjectOrNull<String>(), null)
    })
    add(auto(TestCategory.JSON, "json.dateFormat", "jsonToObject 带 dateFormat") {
        data class Timed(val id: Int, val time: Date?)

        val json = """{"id":1,"time":"2020-01-02 03:04:05"}"""
        val back = json.jsonToObject<Timed>(dateFormat = "yyyy-MM-dd HH:mm:ss")
        assertEq(back?.id, 1)
        assertTrue(back?.time != null, "dateFormat 解析 time 失败")
        assertEq(back?.time?.format("yyyy-MM-dd HH:mm:ss"), "2020-01-02 03:04:05")
    })
    add(auto(TestCategory.JSON, "json.parseJsonObjectOrNull", "GsonJson 安全取值") {
        val obj = """{"id":7,"name":"demo","nested":{"flag":true},"arr":[1,2],"score":1.5,"big":99}""".parseJsonObjectOrNull()
        assertEq(obj?.stringOrNull("name"), "demo")
        assertEq(obj?.intOrNull("id"), 7)
        assertEq(obj?.longOrNull("big"), 99L)
        assertEq(obj?.doubleOrNull("score"), 1.5)
        assertEq(obj?.objOrNull("nested")?.booleanOrNull("flag"), true)
        assertEq(obj?.arrayOrNull("arr")?.get(0)?.asIntOrNull(), 1)
    })
    add(auto(TestCategory.JSON, "json.parseElement.array", "parseJsonElement / parseJsonArray / as*") {
        assertTrue("""{"a":1}""".parseJsonElementOrNull()?.isJsonObject == true, "element object")
        val arr = """[10,20]""".parseJsonArrayOrNull()
        assertEq(arr?.size(), 2)
        assertEq(arr?.get(0)?.asLongOrNull(), 10L)
        assertEq(""" "hi" """.trim().parseJsonElementOrNull()?.asStringOrNull(), "hi")
        assertEq("true".parseJsonElementOrNull()?.asBooleanOrNull(), true)
        assertEq("3.14".parseJsonElementOrNull()?.asDoubleOrNull(), 3.14)
        assertEq("{not-json".parseJsonElementOrNull(), null)
    })

    // ---------- 数值日期 ----------
    add(auto(TestCategory.NUMBER_DATE, "num.fill", "Double/Float/BigDecimal fill / notScience") {
        assertEq(1.2.fill(2), "1.20")
        assertEq(1.2f.fill(2), "1.20")
        assertEq(BigDecimal("0").fill(2), "0.00")
        assertTrue(BigDecimal("1E10").notScience().isNotEmpty(), "BigDecimal.notScience")
        assertTrue(1.0E10.notScience().isNotEmpty(), "notScience 空")
        assertEq(1.235.keepDecimalPlaces(2), 1.24)
    })
    add(auto(TestCategory.NUMBER_DATE, "date.format.parse", "Date.format / parseDate / 自定义格式") {
        val s = Date().format()
        val d = s.parseDate()
        assertTrue(d != null && s.length >= 19, "日期往返失败: $s")
        val custom = Date().format("yyyyMMdd")
        assertEq(custom.length, 8)
        assertTrue(custom.parseDate("yyyyMMdd") != null, "自定义 parse 失败")
        assertEq("bad-date".parseDate(), null)
    })

    // ---------- 文件流 ----------
    add(auto(TestCategory.FILE_IO, "file.write.read.delete", "toFile / string / delFileOrDir") { ctx ->
        val f = File(ctx.cacheDir, "kotlinx_test_file.txt")
        "hello-file".toFile(f)
        assertEq(f.string(), "hello-file")
        assertTrue(f.delFileOrDir(), "删除失败")
        assertTrue(!f.exists(), "删除后仍存在")
    })
    add(auto(TestCategory.FILE_IO, "file.delDir", "delFileOrDir 删除目录") { ctx ->
        val dir = File(ctx.cacheDir, "kotlinx_del_dir")
        dir.mkdirs()
        File(dir, "a.txt").writeText("x")
        assertTrue(dir.delFileOrDir() && !dir.exists(), "目录删除失败")
    })
    add(auto(TestCategory.FILE_IO, "file.readPath.writePath", "readPath / writePath") { ctx ->
        val dir = ctx.filesDir.absolutePath
        "path-content".writePath("kotlinx_path_test.txt", dir)
        assertEq("kotlinx_path_test.txt".readPath(dir), "path-content")
    })
    add(auto(TestCategory.FILE_IO, "file.ExternalFile", "ExternalFile 读写缓存") {
        ExternalFile.writeFile("ext-v", "kotlinx_ext_test.txt")
        assertEq(ExternalFile.readFile("kotlinx_ext_test.txt"), "ext-v")
        ExternalFile.clearCache()
        assertEq(ExternalFile.readFile("kotlinx_ext_test.txt"), "ext-v")
    })
    add(auto(TestCategory.FILE_IO, "stream.toBytes.readOnce", "InputStream.toBytes / readOnce") {
        val data = "stream-data-123".toByteArray()
        assertEq(String(ByteArrayInputStream(data).toBytes()), "stream-data-123")
        assertEq(String(ByteArrayInputStream(data).readOnce()), "stream-data-123")
        assertEq(ByteArrayInputStream(data).readString(), "stream-data-123")
    })
    add(auto(TestCategory.FILE_IO, "bytes.toFile.addFile", "ByteArray.toFile / addFile 追加") { ctx ->
        val f = File(ctx.cacheDir, "kotlinx_bytes.bin")
        f.delete()
        assertTrue("ab".toByteArray().toFile(f), "toFile 失败")
        assertTrue("cd".toByteArray().addFile(f), "addFile 失败")
        assertEq(f.toByteArray()?.let { String(it) }, "abcd")
        "ef".addFile(f)
        assertEq(f.string(), "abcdef")
        f.delete()
    })
    add(auto(TestCategory.FILE_IO, "file.path.overloads", "String.toFile(path) / addFile(path)") { ctx ->
        val path = File(ctx.cacheDir, "kotlinx_path_overload.txt").absolutePath
        File(path).delete()
        "hello".toFile(path)
        assertEq(File(path).string(), "hello")
        " world".addFile(path)
        assertEq(File(path).string(), "hello world")
        File(path).delete()
    })
    add(auto(TestCategory.FILE_IO, "file.ExternalFile.getDir", "ExternalFile.getDir 非空") {
        val dir = ExternalFile.getDir()
        assertTrue(dir.isNotBlank() && File(dir).exists(), "getDir 无效: $dir")
    })

    // ---------- Android 工具 ----------
    add(auto(TestCategory.ANDROID_UTILS, "clip.copy.read", "剪贴板写入并读回") { ctx ->
        val text = "clip-${System.currentTimeMillis()}"
        assertTrue(text.copyToClipboard(ctx), "copy 失败")
        assertEq(ctx.primaryClipPlainText(), text)
        assertTrue(ctx.clipboardManager() != null, "clipboardManager null")
    })
    add(auto(TestCategory.ANDROID_UTILS, "prefs.json", "SharedPreferences putJson/getJsonOrNull") { ctx ->
        val prefs = ctx.kotlinxPrefs("kotlinx_test_prefs")
        prefs.putJson("bean", PrefBean(9, "x"))
        val back: PrefBean? = prefs.getJsonOrNull("bean")
        assertEq(back?.id, 9)
        assertEq(back?.name, "x")
        assertEq(prefs.getJsonOrNull<PrefBean>("missing"), null)
        prefs.putJson("bad", "not-json-object")
        assertEq(prefs.getJsonOrNull<PrefBean>("bad"), null)
        prefs.removeKey("bean")
    })
    add(auto(TestCategory.ANDROID_UTILS, "dimen.dp", "dp / sp / pxToDp / pxToSp") { ctx ->
        val px = 16.dp(ctx)
        assertTrue(px > 0, "dp<=0")
        assertTrue(px.pxToDp(ctx) in 15.0f..17.0f, "pxToDp 偏差过大: ${px.pxToDp(ctx)}")
        assertTrue(16f.dp(ctx) > 0, "Float.dp")
        val spPx = 14.sp(ctx)
        assertTrue(spPx > 0 && spPx.pxToSp(ctx) > 0, "sp/pxToSp")
    })
    add(auto(TestCategory.ANDROID_UTILS, "insets.statusNav", "Insets / systemGestures") { ctx ->
        val act = ctx as? ComponentActivity ?: error("需要 Activity Context")
        assertTrue(act.statusBarsTopPx() >= 0, "status < 0")
        assertTrue(act.navigationBarsBottomPx() >= 0, "nav < 0")
        act.windowInsetsCompat() // 可为 null
        val g = act.systemGesturesInsets()
        assertTrue(g.left >= 0 && g.right >= 0, "gestures 负值")
    })
    add(auto(TestCategory.ANDROID_UTILS, "throwable.rootCause", "rootCause / oneLineMessage") {
        val ex = RuntimeException("外", IllegalStateException("内"))
        assertEq(ex.rootCause.message, "内")
        assertTrue(ex.oneLineMessage().contains("Runtime") || ex.oneLineMessage().contains("外"), "oneLine")
    })
    add(auto(TestCategory.ANDROID_UTILS, "stack.getLine", "getLine / showStackTrace") {
        val line = getLine(0)
        assertTrue(line != null && line.contains("(") && line.contains(":"), "getLine: $line")
        assertEq("payload".showStackTrace(), "payload")
    })
    add(auto(TestCategory.ANDROID_UTILS, "coro.ui.io", "ui / io / isUI / mainHandler") {
        assertTrue(mainHandler.looper != null, "mainHandler")
        val latch = CountDownLatch(1)
        val ioOk = AtomicBoolean(false)
        val uiOk = AtomicBoolean(false)
        io {
            ioOk.set(!isUI())
            ui {
                uiOk.set(isUI())
                latch.countDown()
            }
        }
        assertTrue(latch.await(3, TimeUnit.SECONDS), "协程超时")
        assertTrue(ioOk.get() && uiOk.get(), "线程调度不符合预期 io=${ioOk.get()} ui=${uiOk.get()}")
    })
    add(auto(TestCategory.ANDROID_UTILS, "debounce.once", "debounce 窗口内只执行一次") {
        val n = AtomicInteger(0)
        val key = "test-debounce-${System.nanoTime()}"
        repeat(5) { debounce(identifier = key, millis = 500) { n.incrementAndGet() } }
        assertEq(n.get(), 1, "防抖应只触发 1 次，实际 ${n.get()}")
    })
    add(auto(TestCategory.ANDROID_UTILS, "debounce.noKey", "debounce(millis) 无 identifier 重载") {
        val n = AtomicInteger(0)
        fun bump() {
            debounce(millis = 500) { n.incrementAndGet() }
        }
        repeat(5) { bump() }
        assertEq(n.get(), 1, "同调用点防抖应只触发 1 次，实际 ${n.get()}")
    })
    add(auto(TestCategory.ANDROID_UTILS, "result.toastOnFailure", "Result.toastOnFailure 不抛") {
        Result.failure<Int>(IllegalStateException("demo-fail")).toastOnFailure()
        Result.success(1).toastOnFailure()
        // toast 走 ui{} 异步，等主线程跑完再继续，避免干扰后续 toastFilter 用例
        awaitMainIdle()
    })

    // ---------- Bitmap ----------
    add(auto(TestCategory.BITMAP, "bitmap.ops", "toByteArray / zoom / round / compress / toBitmap") {
        val src = Bitmap.createBitmap(40, 40, Bitmap.Config.ARGB_8888)
        src.eraseColor(Color.RED)
        val bytes = src.toByteArray()
        assertTrue(bytes.isNotEmpty(), "toByteArray 空")
        val decoded = bytes.toBitmap()
        assertTrue(decoded != null && decoded!!.width > 0, "toBitmap 失败")
        val zoomed = src.zoom(20, 20)
        assertEq(zoomed.width, 20)
        val rounded = src.round(8f)
        assertEq(rounded.width, 40)
        assertTrue(Color.alpha(rounded.getPixel(20, 20)) > 0, "圆角图中心透明，蒙版可能错误")
        val compressed = src.compressToBytes(50)
        assertTrue(compressed != null && compressed.isNotEmpty(), "compress 失败")
        src.recycle(); zoomed.recycle(); rounded.recycle(); decoded?.recycle()
    })

    // ---------- 集合 ----------
    add(auto(TestCategory.COLLECTION, "list.addAndReplace", "addAndReplace 同 id 覆盖") {
        data class Item(val id: Int, val v: String)

        val list = mutableListOf(Item(1, "a"), Item(2, "b"))
        list.addAndReplace(listOf(Item(2, "B"), Item(3, "c"))) { o, n -> o.id == n.id }
        assertEq(list.size, 3)
        assertEq(list.find { it.id == 2 }?.v, "B")
    })
    add(auto(TestCategory.COLLECTION, "list.toString.ext", "List?.toString 扩展（JSON）") {
        val nullList: List<Any>? = null
        assertEq(nullList.extToString(), null)
        val list: List<Any> = listOf(mapOf("k" to 1))
        val s = list.extToString()
        assertTrue(s != null && s.contains("k") && s.contains("1"), "List.toString 扩展: $s")
    })
    add(auto(TestCategory.COLLECTION, "mutableList.toString.ext", "MutableList?.toString 扩展") {
        val nullMl: MutableList<Any>? = null
        assertEq(nullMl.extToString(), "null")
        val ml: MutableList<Any> = mutableListOf("a", "b")
        val s = ml.extToString()
        assertTrue(s.startsWith("[") && s.contains("a") && s.contains("b"), "MutableList.toString: $s")
    })
    add(auto(TestCategory.COLLECTION, "any.toJson.toUnit", "Any.toJson / toUnit") {
        val json = mapOf("a" to 1).toJson()
        assertTrue(json.contains("\"a\"") && json.contains("1"), "toJson: $json")
        assertEq("x".toUnit(), Unit)
    })

    // ---------- Recycler 逻辑 ----------
    add(auto(TestCategory.RECYCLER, "adapter.BaseViewAdapter.crud", "BaseViewAdapter add/update/remove/isSelect") { ctx ->
        val adapter = object : BaseViewAdapter<String>(mutableListOf()) {
            override fun getView(): View = View(ctx)
            override fun item(holder: BaseViewHolder, position: Int) {}
        }
        adapter.add("a")
        adapter.add("b")
        assertEq(adapter.itemCount, 2)
        adapter.update("B", 1)
        assertEq(adapter.list[1], "B")
        adapter.isSelect = 0
        assertEq(adapter.isSelect, 0)
        adapter.removeAt(0)
        assertEq(adapter.itemCount, 1)
        adapter.clear()
        assertEq(adapter.itemCount, 0)
    })
    add(auto(TestCategory.RECYCLER, "rv.init.layoutManager", "RecyclerView.init 线性/网格") { ctx ->
        val rv = RecyclerView(ctx)
        rv.init()
        assertTrue(rv.layoutManager is LinearLayoutManager, "应为 LinearLayoutManager")
        rv.init(items = 2)
        assertTrue(rv.layoutManager is GridLayoutManager, "应为 GridLayoutManager")
        assertEq((rv.layoutManager as GridLayoutManager).spanCount, 2)
    })
    add(auto(TestCategory.RECYCLER, "rv.BottomAdapter", "BottomAdapter 文案切换") { ctx ->
        val bottom = BottomAdapter(ctx, "初始")
        bottom.showWrapContent("没有更多")
        assertEq(bottom.tips.toString(), "没有更多")
        bottom.showMatchParent("暂无数据")
        assertEq(bottom.tips.toString(), "暂无数据")
        assertEq(bottom.itemCount, 1)
    })
    add(auto(TestCategory.RECYCLER, "rv.showEmpty", "showEmpty 设置 adapter") { ctx ->
        val rv = RecyclerView(ctx)
        val adapter = rv.showEmpty("空空如也")
        assertTrue(rv.adapter === adapter, "adapter 未挂上")
        assertEq(adapter.itemCount, 1)
    })
    add(auto(TestCategory.RECYCLER, "rv.show.baseAdapter", "RecyclerView.show + BaseAdapter") { ctx ->
        runOnMain {
            val rv = RecyclerView(ctx)
            val data = mutableListOf("a", "b", "c")
            val adapter = rv.show(R.layout.test_item, data) { holder, pos ->
                val binding = holder.binding as TestItemBinding
                binding.tvTest.text = data[pos]
            }
            assertTrue(rv.adapter === adapter, "show 未挂 adapter")
            assertEq(adapter.itemCount, 3)
            adapter.add("d")
            assertEq(adapter.itemCount, 4)
        }
    })
    add(auto(TestCategory.RECYCLER, "rv.scroll.listeners", "toBottom / notToTop 滚动监听") { ctx ->
        runOnMain {
            val rv = RecyclerView(ctx)
            val data = (1..50).map { "row-$it" }.toMutableList()
            rv.show(R.layout.test_item, data) { holder, pos ->
                (holder.binding as TestItemBinding).tvTest.text = data[pos]
            }
            val notTop = AtomicBoolean(false)
            val toBottom = AtomicBoolean(false)
            rv.notToTopListener { notTop.set(true) }
            rv.toBottomListener { toBottom.set(true) }
            val w = View.MeasureSpec.makeMeasureSpec(720, View.MeasureSpec.EXACTLY)
            val h = View.MeasureSpec.makeMeasureSpec(480, View.MeasureSpec.EXACTLY)
            rv.measure(w, h)
            rv.layout(0, 0, 720, 480)
            rv.scrollBy(0, 20_000)
            assertTrue(notTop.get() || toBottom.get(), "滚动监听未触发 notTop=${notTop.get()} toBottom=${toBottom.get()}")
        }
    })

    // ---------- 交互 ----------
    add(auto(TestCategory.INTERACTIVE, "log.allLevels", "logV/D/I/W/E 不抛") {
        "v".logV("TestHarness")
        "d".logD("TestHarness")
        "i".logI("TestHarness")
        "w".logW("TestHarness")
        "e".logE("TestHarness")
    })
    add(auto(TestCategory.INTERACTIVE, "log.LogListener", "LogListener 回调") {
        val hit = AtomicReference<String?>(null)
        val old = LogListener
        try {
            LogListener = { _, tag, msg, _ -> if (tag == "ListenerTag") hit.set(msg) }
            "ping".logI("ListenerTag")
            assertEq(hit.get(), "ping")
        } finally {
            LogListener = old
        }
    })
    add(auto(TestCategory.INTERACTIVE, "toast.filter", "toastFilter 拦截") {
        awaitMainIdle()
        val expect = "raw-toast-${System.nanoTime()}"
        val got = AtomicReference<String?>(null)
        val latch = CountDownLatch(1)
        val old = toastFilter
        try {
            toastFilter = { msg ->
                // 只认本用例发出的文案，忽略队列里残留的其它 toast
                if (msg == expect) {
                    got.set(msg)
                    latch.countDown()
                }
                "filtered:$msg"
            }
            expect.toast()
            assertTrue(latch.await(5, TimeUnit.SECONDS), "toastFilter 未回调 expect=$expect got=${got.get()}")
            assertEq(got.get(), expect)
        } finally {
            toastFilter = old
            awaitMainIdle()
        }
    })
    add(auto(TestCategory.INTERACTIVE, "toast.short.long", "toastShort / toastLong 经 Filter") {
        awaitMainIdle()
        val shortMsg = "short-${System.nanoTime()}"
        val longMsg = "long-${System.nanoTime()}"
        val seen = java.util.concurrent.ConcurrentHashMap.newKeySet<String>()
        val latch = CountDownLatch(2)
        val old = toastFilter
        try {
            toastFilter = { msg ->
                if ((msg == shortMsg || msg == longMsg) && seen.add(msg)) {
                    latch.countDown()
                }
                msg
            }
            shortMsg.toastShort()
            longMsg.toastLong()
            assertTrue(latch.await(5, TimeUnit.SECONDS), "toastShort/Long 未齐 seen=$seen")
            assertTrue(seen.contains(shortMsg) && seen.contains(longMsg), "seen=$seen")
        } finally {
            toastFilter = old
            awaitMainIdle()
        }
    })
    add(auto(TestCategory.INTERACTIVE, "vibrate.short", "vibrateShort 不抛异常") { ctx ->
        ctx.vibrateShort(20L)
    })
    add(manual(TestCategory.INTERACTIVE, "toast.show", "弹出 Toast：看到后点「通过」") {
        "Toast 测试 OK".toast()
    })
    add(manual(TestCategory.INTERACTIVE, "tts.speak", "TTS 播报：听得到点「通过」") {
        "扩展库 TTS 测试".speak()
    })
    add(manual(TestCategory.INTERACTIVE, "tts.speakQueue", "TTS 队列播报") {
        "队列第一条".speakQueue()
        "队列第二条".speakQueue()
    })
    add(manual(TestCategory.INTERACTIVE, "tts.speakToast", "speakToast：播报并 Toast") {
        TTS.speakToast("speakToast 测试")
    })
    add(manual(TestCategory.INTERACTIVE, "tts.speakQueueToast", "speakQueueToast：队列播报并 Toast") {
        TTS.speakQueueToast("speakQueueToast 测试")
    })
    add(manual(TestCategory.INTERACTIVE, "tts.loop", "TTS 循环：点运行开始，再点失败旁说明后需手动 loopClose") {
        TTS.loopSpeak("循环测试", 1500)
    })
    add(manual(TestCategory.INTERACTIVE, "tts.loopSpeakQueue", "loopSpeakQueue 循环队列播报") {
        TTS.loopSpeakQueue("队列循环", 2000)
    })
    add(auto(TestCategory.INTERACTIVE, "tts.loopClose", "TTS.loopClose 不抛") {
        TTS.loopClose()
    })
    add(auto(TestCategory.INTERACTIVE, "tts.onStop", "TTS.onStop 不抛") {
        TTS.onStop()
    })
    add(manual(TestCategory.INTERACTIVE, "haptic.tap", "触感演示页操作后点「通过」"))

    // ---------- View ----------
    add(auto(TestCategory.VIEW, "view.marquee.props", "TextView.marquee 属性") { ctx ->
        val tv = TextView(ctx)
        tv.text = "跑马灯很长很长很长很长的文字"
        tv.marquee()
        assertTrue(tv.isSingleLine, "isSingleLine")
        assertEq(tv.ellipsize, TextUtils.TruncateAt.MARQUEE)
        assertTrue(tv.isSelected, "isSelected")
    })
    add(manual(TestCategory.VIEW, "view.demo", "进入手势演示页（防抖/双击/长按/滑动/触感）"))
    add(manual(TestCategory.VIEW, "view.softKeyboard", "手势页底部输入框测软键盘开关后点「通过」"))

    // ---------- Recycler UI / SAF ----------
    add(manual(TestCategory.RECYCLER, "recycler.ui", "进入 RecyclerView 演示：BaseViewAdapter / show(layout) / 滚动监听"))
    add(auto(TestCategory.SAF, "saf.getSDCardRootUri", "getSDCardRootUri 不抛") { ctx ->
        getSDCardRootUri(ctx) // 可为 null
    })
    add(auto(TestCategory.SAF, "saf.removeAllPermissions", "removeAllPermissions 不抛") { ctx ->
        assertTrue(removeAllPermissions(ctx), "removeAllPermissions 应返回 true（无权限时亦 true）")
    })
    add(manual(TestCategory.SAF, "saf.demo", "进入 SAF 演示：授权读写 / 单条 removePermission / 全部移除"))
    add(manual(TestCategory.ANDROID_UTILS, "perm.notification", "打开权限演示页，请求通知权限后点「通过」"))
}

private fun auto(
    category: TestCategory,
    id: String,
    title: String,
    run: (Context) -> Unit,
): TestCaseDef = TestCaseDef(id, title, category, TestKind.AUTO, run = run)

private fun manual(
    category: TestCategory,
    id: String,
    title: String,
    run: ((Context) -> Unit)? = null,
): TestCaseDef = TestCaseDef(id, title, category, TestKind.MANUAL, description = "需人工确认", run = run)
