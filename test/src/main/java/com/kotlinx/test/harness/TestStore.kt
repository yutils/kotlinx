package com.kotlinx.test.harness

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.kotlinx.utils.mainHandler
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.Executors

class TestStore(private val appContext: Context) {
    val cases: SnapshotStateList<TestCaseState> = mutableStateListOf()
    var logText by mutableStateOf("就绪\n")
        private set

    private val timeFmt = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    private val executor = Executors.newSingleThreadExecutor { r ->
        Thread(r, "kotlinx-test-runner").apply { isDaemon = true }
    }

    @Volatile
    private var runningBatch = false

    init {
        rebuildFromCatalog(loadPersisted = true)
    }

    /** 重建用例定义；可选是否从磁盘恢复状态 */
    fun rebuildFromCatalog(loadPersisted: Boolean) {
        cases.clear()
        cases.addAll(buildAllTestCases().map { TestCaseState(it) })
        if (loadPersisted) {
            TestCoverageStore.loadInto(appContext, cases)
        }
    }

    /** 仅清空状态与日志（对齐 yutils「清空」），不改用例目录 */
    fun clearStatuses() {
        TestCoverageStore.clear(appContext, cases)
        logText = "就绪\n"
        appendLog("已清空全部用例状态与运行日志")
    }

    fun appendLog(line: String) {
        val ts = timeFmt.format(Date())
        // Compose 状态尽量在主线程写
        if (android.os.Looper.myLooper() == android.os.Looper.getMainLooper()) {
            logText += "[$ts] $line\n"
        } else {
            mainHandler.post { logText += "[$ts] $line\n" }
        }
    }

    fun summary(): List<CategorySummary> {
        return TestCategory.entries.map { cat ->
            val list = cases.filter { it.def.category == cat }
            CategorySummary(
                category = cat,
                total = list.size,
                pass = list.count { it.status == TestStatus.PASS },
                fail = list.count { it.status == TestStatus.FAIL },
                idle = list.count { it.status == TestStatus.IDLE || it.status == TestStatus.SKIP },
                manual = list.count { it.def.kind == TestKind.MANUAL },
            )
        }
    }

    fun overall(): Triple<Int, Int, Int> {
        val pass = cases.count { it.status == TestStatus.PASS }
        val fail = cases.count { it.status == TestStatus.FAIL }
        val total = cases.size
        return Triple(pass, fail, total)
    }

    /** 仅 AUTO：pass / fail / total */
    fun overallAuto(): Triple<Int, Int, Int> {
        val autos = cases.filter { it.def.kind == TestKind.AUTO }
        val pass = autos.count { it.status == TestStatus.PASS }
        val fail = autos.count { it.status == TestStatus.FAIL }
        return Triple(pass, fail, autos.size)
    }

    fun autoCount(): Int = cases.count { it.def.kind == TestKind.AUTO }

    fun update(id: String, status: TestStatus, detail: String = "", durationMs: Long? = null) {
        val apply = {
            val idx = cases.indexOfFirst { it.def.id == id }
            if (idx >= 0) {
                val old = cases[idx]
                val next = old.copy(
                    status = status,
                    detail = detail,
                    durationMs = durationMs ?: old.durationMs,
                )
                cases[idx] = next
                if (status != TestStatus.RUNNING) {
                    TestCoverageStore.persist(appContext, next)
                }
            }
        }
        if (android.os.Looper.myLooper() == android.os.Looper.getMainLooper()) {
            apply()
        } else {
            mainHandler.post(apply)
        }
    }

    /** 单条：AUTO 丢到后台线程，避免阻塞主线程（ui{} / CountDownLatch 会死锁） */
    fun runOne(context: Context, id: String) {
        val state = cases.firstOrNull { it.def.id == id } ?: return
        if (state.def.kind == TestKind.AUTO) {
            executor.execute { runOneBlocking(context, id) }
        } else {
            runOneBlocking(context, id)
        }
    }

    private fun runOneBlocking(context: Context, id: String) {
        val state = cases.firstOrNull { it.def.id == id } ?: return
        val def = state.def
        update(id, TestStatus.RUNNING)
        val runner = def.run
        if (runner == null) {
            update(id, TestStatus.IDLE, "请打开演示页后手动点通过/失败", 0L)
            appendLog("$id · 待人工（已打开/无副作用）")
            return
        }
        val start = System.currentTimeMillis()
        try {
            runner(context)
            val cost = System.currentTimeMillis() - start
            if (def.kind == TestKind.AUTO) {
                update(id, TestStatus.PASS, "OK", cost)
                appendLog("$id · PASS · ${cost}ms")
            } else {
                update(id, TestStatus.IDLE, "已执行，请人工点「通过」或「失败」", cost)
                appendLog("$id · 已执行，待人工确认 · ${cost}ms")
            }
        } catch (t: Throwable) {
            val cost = System.currentTimeMillis() - start
            val msg = t.message ?: t.toString()
            update(id, TestStatus.FAIL, msg, cost)
            appendLog("$id · FAIL · ${cost}ms · $msg")
        }
    }

    fun runAllAuto(context: Context) {
        if (runningBatch) {
            appendLog("已有批量任务在跑，请稍候")
            return
        }
        runningBatch = true
        val ids = cases.filter { it.def.kind == TestKind.AUTO }.map { it.def.id }
        executor.execute {
            try {
                appendLog("—— 开始跑全部自动项 ——")
                ids.forEach { runOneBlocking(context, it) }
                val latch = java.util.concurrent.CountDownLatch(1)
                mainHandler.post { latch.countDown() }
                latch.await(2, java.util.concurrent.TimeUnit.SECONDS)
                val (pass, fail, total) = overallAuto()
                appendLog("—— 自动项结束：通过 $pass / $total，失败 $fail ——")
            } finally {
                runningBatch = false
            }
        }
    }

    fun runCategoryAuto(context: Context, category: TestCategory) {
        if (runningBatch) {
            appendLog("已有批量任务在跑，请稍候")
            return
        }
        runningBatch = true
        val ids = cases.filter { it.def.category == category && it.def.kind == TestKind.AUTO }.map { it.def.id }
        executor.execute {
            try {
                appendLog("—— 开始跑 [${category.title}] 自动项 ——")
                ids.forEach { runOneBlocking(context, it) }
                val latch = java.util.concurrent.CountDownLatch(1)
                mainHandler.post { latch.countDown() }
                latch.await(2, java.util.concurrent.TimeUnit.SECONDS)
                val list = cases.filter { it.def.category == category && it.def.kind == TestKind.AUTO }
                val pass = list.count { it.status == TestStatus.PASS }
                appendLog("—— [${category.title}] 结束：通过 $pass / ${list.size} ——")
            } finally {
                runningBatch = false
            }
        }
    }

    fun mark(id: String, pass: Boolean, detail: String = "") {
        val msg = detail.ifBlank { if (pass) "人工通过" else "人工失败" }
        val status = if (pass) TestStatus.PASS else TestStatus.FAIL
        update(id, status, msg)
        appendLog("$id · ${status.name} · $msg")
    }
}
