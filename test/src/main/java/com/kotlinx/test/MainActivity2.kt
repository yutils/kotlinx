package com.kotlinx.test

import android.Manifest
import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlinx.extend.arrayOrNull
import com.kotlinx.extend.asIntOrNull
import com.kotlinx.extend.booleanOrNull
import com.kotlinx.extend.copyToClipboard
import com.kotlinx.extend.dp
import com.kotlinx.extend.format
import com.kotlinx.extend.getJsonOrNull
import com.kotlinx.extend.kotlinxPrefs
import com.kotlinx.extend.logI
import com.kotlinx.extend.navigationBarsBottomPx
import com.kotlinx.extend.objOrNull
import com.kotlinx.extend.oneLineMessage
import com.kotlinx.extend.parseDate
import com.kotlinx.extend.parseJsonObjectOrNull
import com.kotlinx.extend.performConfirmHaptic
import com.kotlinx.extend.performLightHapticTap
import com.kotlinx.extend.primaryClipPlainText
import com.kotlinx.extend.putJson
import com.kotlinx.extend.pxToDp
import com.kotlinx.extend.rootCause
import com.kotlinx.extend.statusBarsTopPx
import com.kotlinx.extend.stringOrNull
import com.kotlinx.extend.systemGesturesInsets
import com.kotlinx.extend.toJson
import com.kotlinx.extend.toast
import com.kotlinx.extend.toastOnFailure
import com.kotlinx.extend.vibrateShort
import com.kotlinx.test.ui.theme.KotlinxTheme
import java.util.Date

/** Gson 读写 SharedPreferences 演示用（勿用于隐私字段）。 */
data class KotlinxPrefsDemoBean(val code: Int = 0, val msg: String? = null)

/**
 * 扩展函数演示界面（入口：Kotlinx test 主页「扩展演示」）。
 */
class MainActivity2 : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KotlinxTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    ExtensionsDemoScreen()
                }
            }
        }
    }
}

@Composable
private fun ExtensionsDemoScreen() {
    val context = LocalContext.current
    val view = LocalView.current
    val activity = context as? Activity

    val notifyLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        "通知权限: ${if (granted) "已授予" else "拒绝"}".toast()
    }

    Column(
        modifier = Modifier
            .padding(12.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "kotlinx 扩展演示",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
        )

        DemoButton("剪贴板：写入并读回") {
            val ok = "kotlinx-demo-${System.currentTimeMillis()}".copyToClipboard(context)
            val read = context.primaryClipPlainText().orEmpty()
            "copyOk=$ok clip=$read".logI("ExtensionsDemo")
            "剪贴板: $read".toast()
        }

        DemoButton("短振动（需 Manifest VIBRATE）") {
            context.vibrateShort()
            "已触发振动".toast()
        }

        DemoButton("触感：KEYBOARD_TAP") {
            view.performLightHapticTap()
        }

        DemoButton("触感：CONFIRM") {
            view.performConfirmHaptic()
        }

        DemoButton("尺寸：16dp → px") {
            val px = 16.dp(context)
            "16.dp = ${px}px, px→dp=${px.pxToDp(context)}".toast()
        }

        DemoButton("WindowInsets：状态栏/导航栏") {
            val act = activity ?: run {
                "无法获取 Activity".toast()
                return@DemoButton
            }
            val top = act.statusBarsTopPx()
            val bottom = act.navigationBarsBottomPx()
            val g = act.systemGesturesInsets()
            "statusBars.top=$top navBars.bottom=$bottom gestures L=${g.left} R=${g.right}".toast()
        }

        DemoButton("Gson Json：parseJsonObjectOrNull") {
            val raw = """{"id":7,"name":"demo","nested":{"flag":true},"arr":[1,2]}"""
            val obj = raw.parseJsonObjectOrNull()
            val nestedFlag = obj?.objOrNull("nested")?.booleanOrNull("flag")
            val arr = obj?.arrayOrNull("arr")
            val firstArr = arr?.takeIf { it.size() > 0 }?.get(0)?.asIntOrNull()
            val name = obj?.stringOrNull("name")
            "name=$name nested.flag=$nestedFlag arr[0]=$firstArr".toast()
        }

        DemoButton("SharedPreferences + Gson put/get") {
            val prefs = context.kotlinxPrefs("kotlinx_extension_demo")
            prefs.putJson("bean", KotlinxPrefsDemoBean(42, "hello"))
            val back: KotlinxPrefsDemoBean? = prefs.getJsonOrNull("bean")
            "prefs bean=${back?.toJson()}".toast()
        }

        DemoButton("Throwable：rootCause / oneLineMessage") {
            val ex = RuntimeException("外层", IllegalStateException("内层"))
            "${ex.rootCause.oneLineMessage()} | chain=${ex.oneLineMessage()}".toast()
        }

        DemoButton("Result.toastOnFailure") {
            Result.failure<Int>(IllegalArgumentException("演示失败")).toastOnFailure()
        }

        DemoButton("Date：线程安全 format / parseDate") {
            val now = Date().format()
            val parsed = now.parseDate()?.time ?: -1L
            "now=$now parsedMs=$parsed".toast()
        }

        DemoButton("通知权限（API 33+）") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                notifyLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                "当前 API < 33，无需运行时请求 POST_NOTIFICATIONS".toast()
            }
        }
    }
}

@Composable
private fun DemoButton(label: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
    ) {
        Text(text = label, fontSize = 14.sp)
    }
}
