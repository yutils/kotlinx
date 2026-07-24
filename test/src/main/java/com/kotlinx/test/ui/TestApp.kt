package com.kotlinx.test.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlinx.test.PermissionDemoActivity
import com.kotlinx.test.RecyclerViewTestActivity
import com.kotlinx.test.SafDemoActivity
import com.kotlinx.test.ViewGestureDemoActivity
import com.kotlinx.test.harness.CoverageGaps
import com.kotlinx.test.harness.TestCaseState
import com.kotlinx.test.harness.TestCategory
import com.kotlinx.test.harness.TestKind
import com.kotlinx.test.harness.TestStatus
import com.kotlinx.test.harness.TestStore
import com.kotlinx.test.ui.theme.Accent
import com.kotlinx.test.ui.theme.FailRed
import com.kotlinx.test.ui.theme.IdleGray
import com.kotlinx.test.ui.theme.Ink
import com.kotlinx.test.ui.theme.InkSoft
import com.kotlinx.test.ui.theme.Line
import com.kotlinx.test.ui.theme.ManualAmber
import com.kotlinx.test.ui.theme.Paper
import com.kotlinx.test.ui.theme.PaperCard
import com.kotlinx.test.ui.theme.PassGreen

sealed class Screen {
    data object Home : Screen()
    data class Category(val category: TestCategory) : Screen()
}

@Composable
fun TestApp(store: TestStore, screen: Screen, onNavigate: (Screen) -> Unit) {
    // 分类页系统返回 → 回首页，而不是直接 finish Activity
    BackHandler(enabled = screen is Screen.Category) {
        onNavigate(Screen.Home)
    }
    when (screen) {
        Screen.Home -> HomeScreen(store, onOpen = { onNavigate(Screen.Category(it)) })
        is Screen.Category -> CategoryScreen(
            store = store,
            category = screen.category,
            onBack = { onNavigate(Screen.Home) },
        )
    }
}

@Composable
private fun HomeScreen(store: TestStore, onOpen: (TestCategory) -> Unit) {
    val context = LocalContext.current
    val (pass, fail, total) = store.overall()
    val (autoPass, autoFail, autoTotal) = store.overallAuto()
    val done = pass + fail
    val summaries = store.summary()
    val logScroll = rememberScrollState()

    LaunchedEffect(store.logText) {
        logScroll.animateScrollTo(logScroll.maxValue)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Paper),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PaperCard)
                .border(BorderStroke(1.dp, Line))
                .padding(20.dp),
        ) {
            Text("kotlinx 功能测试台", color = Ink, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(6.dp))
            Text(
                "已执行 $done / $total · 通过 $pass · 失败 $fail · 未测 ${total - done}",
                color = InkSoft,
                fontSize = 13.sp,
            )
            Text(
                "自动 $autoPass / $autoTotal" + if (autoFail > 0) " · 失败 $autoFail" else "",
                color = InkSoft,
                fontSize = 13.sp,
            )
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { store.runAllAuto(context) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Accent, contentColor = Color.White),
                ) { Text("一键跑全部自动项") }
                OutlinedButton(
                    onClick = { store.clearStatuses() },
                    border = BorderStroke(1.dp, Accent),
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = Color.Transparent,
                        contentColor = Accent,
                    ),
                ) { Text("清空", color = Accent) }
                OutlinedButton(
                    onClick = {
                        AlertDialog.Builder(context)
                            .setTitle("覆盖说明")
                            .setMessage(CoverageGaps.overviewHint(store.cases.size, store.autoCount()))
                            .setPositiveButton("知道了", null)
                            .show()
                    },
                    border = BorderStroke(1.dp, Accent),
                    colors = ButtonDefaults.outlinedButtonColors(
                        backgroundColor = Color.Transparent,
                        contentColor = Accent,
                    ),
                ) { Text("说明", color = Accent) }
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f),
        ) {
            items(summaries) { s ->
                CategoryCard(s.category, s.pass, s.fail, s.idle, s.manual, s.total) {
                    onOpen(s.category)
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 120.dp, max = 180.dp)
                .background(Color(0xFF1A1A1A))
                .padding(12.dp),
        ) {
            Text("运行日志", color = Color.White.copy(0.7f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(6.dp))
            Text(
                store.logText,
                color = Color(0xFFB8F0C8),
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(logScroll),
            )
        }
    }
}

@Composable
private fun CategoryCard(
    category: TestCategory,
    pass: Int,
    fail: Int,
    idle: Int,
    manual: Int,
    total: Int,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(PaperCard)
            .border(1.dp, Line, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(category.title, fontWeight = FontWeight.SemiBold, fontSize = 17.sp, color = Ink, modifier = Modifier.weight(1f))
            Text("${pass + fail}/$total", color = InkSoft, fontSize = 13.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(category.subtitle, color = IdleGray, fontSize = 12.sp)
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusChip("通过 $pass", PassGreen)
            StatusChip("失败 $fail", FailRed)
            StatusChip("未测 $idle", IdleGray)
            if (manual > 0) StatusChip("人工 $manual", ManualAmber)
        }
    }
}

@Composable
private fun StatusChip(text: String, color: Color) {
    Text(
        text = text,
        color = color,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}

@Composable
private fun CategoryScreen(store: TestStore, category: TestCategory, onBack: () -> Unit) {
    val context = LocalContext.current
    val activity = context as Activity
    val list = store.cases.filter { it.def.category == category }

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Paper)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PaperCard)
                .border(BorderStroke(1.dp, Line))
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(onClick = onBack) { Text("← 返回", color = Accent) }
            Column(modifier = Modifier.weight(1f)) {
                Text(category.title, color = Ink, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(category.subtitle, color = InkSoft, fontSize = 12.sp)
            }
        }

        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Button(
                onClick = { store.runCategoryAuto(context, category) },
                colors = ButtonDefaults.buttonColors(backgroundColor = Accent, contentColor = Color.White),
            ) { Text("跑本类自动项") }
        }

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.weight(1f),
        ) {
            items(list, key = { it.def.id }) { state ->
                CaseRow(
                    state = state,
                    onRun = {
                        when (state.def.id) {
                            "view.demo",
                            "view.softKeyboard",
                            "haptic.tap",
                                -> activity.startActivity(Intent(activity, ViewGestureDemoActivity::class.java))

                            "perm.notification" ->
                                activity.startActivity(Intent(activity, PermissionDemoActivity::class.java))

                            "recycler.ui" -> activity.startActivity(Intent(activity, RecyclerViewTestActivity::class.java))
                            "saf.demo" -> activity.startActivity(Intent(activity, SafDemoActivity::class.java))
                            "tts.loop" -> store.runOne(context, state.def.id)
                            else -> store.runOne(context, state.def.id)
                        }
                    },
                    onPass = { store.mark(state.def.id, true) },
                    onFail = { store.mark(state.def.id, false) },
                )
            }
        }
    }
}

@Composable
private fun CaseRow(
    state: TestCaseState,
    onRun: () -> Unit,
    onPass: () -> Unit,
    onFail: () -> Unit,
) {
    val statusColor = when (state.status) {
        TestStatus.PASS -> PassGreen
        TestStatus.FAIL -> FailRed
        TestStatus.RUNNING -> Accent
        TestStatus.SKIP -> ManualAmber
        TestStatus.IDLE -> IdleGray
    }
    val durationLabel = if (state.durationMs > 0) " · ${state.durationMs}ms" else ""
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PaperCard)
            .border(1.dp, Line, RoundedCornerShape(12.dp))
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(statusColor),
            )
            Spacer(Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(state.def.title, fontWeight = FontWeight.Medium, color = Ink, fontSize = 15.sp)
                Text(
                    "${state.def.id} · ${if (state.def.kind == TestKind.AUTO) "自动" else "人工"} · ${state.status.name}$durationLabel",
                    color = IdleGray,
                    fontSize = 11.sp,
                )
            }
        }
        if (state.detail.isNotBlank()) {
            Spacer(Modifier.height(6.dp))
            Text(state.detail, color = if (state.status == TestStatus.FAIL) FailRed else InkSoft, fontSize = 12.sp)
        }
        Spacer(Modifier.height(10.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onRun,
                colors = ButtonDefaults.buttonColors(backgroundColor = Accent, contentColor = Color.White),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
            ) { Text(if (state.def.kind == TestKind.AUTO) "运行" else "执行/打开", fontSize = 13.sp) }
            if (state.def.kind == TestKind.MANUAL) {
                OutlinedButton(onClick = onPass, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
                    Text("通过", color = PassGreen, fontSize = 13.sp)
                }
                OutlinedButton(onClick = onFail, contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)) {
                    Text("失败", color = FailRed, fontSize = 13.sp)
                }
            }
        }
    }
}
