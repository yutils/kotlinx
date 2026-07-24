package com.kotlinx.test.harness

import android.content.Context
import androidx.core.content.edit
import com.kotlinx.extend.kotlinxPrefs

/**
 * 用 SharedPreferences 持久化用例状态，重启后仍可看到覆盖进度。
 */
object TestCoverageStore {
    private const val PREFS = "kotlinx_test_coverage"
    private const val KEY_PREFIX = "case_"

    private fun prefs(context: Context) = context.applicationContext.kotlinxPrefs(PREFS)

    fun loadInto(context: Context, cases: MutableList<TestCaseState>) {
        val p = prefs(context)
        for (i in cases.indices) {
            val raw = p.getString(KEY_PREFIX + cases[i].def.id, null) ?: continue
            val parts = raw.split("|", limit = 3)
            if (parts.isEmpty()) continue
            var status = runCatching { TestStatus.valueOf(parts[0]) }.getOrDefault(TestStatus.IDLE)
            if (status == TestStatus.RUNNING) status = TestStatus.IDLE
            val detail = parts.getOrNull(1).orEmpty()
            val durationMs = parts.getOrNull(2)?.toLongOrNull() ?: 0L
            cases[i] = cases[i].copy(status = status, detail = detail, durationMs = durationMs)
        }
    }

    fun persist(context: Context, state: TestCaseState) {
        val msg = state.detail.replace("|", " ")
        prefs(context).edit {
            putString(KEY_PREFIX + state.def.id, "${state.status.name}|$msg|${state.durationMs}")
        }
    }

    fun clear(context: Context, cases: MutableList<TestCaseState>) {
        val p = prefs(context)
        p.edit {
            cases.forEach { remove(KEY_PREFIX + it.def.id) }
        }
        for (i in cases.indices) {
            cases[i] = cases[i].copy(status = TestStatus.IDLE, detail = "", durationMs = 0L)
        }
    }
}
