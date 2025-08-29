package com.kotlinx.test

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlinx.Kotlinx
import com.kotlinx.extend.LogListener
import com.kotlinx.extend.addFile
import com.kotlinx.extend.format
import com.kotlinx.extend.getSDCardRootUri
import com.kotlinx.extend.jsonToObject
import com.kotlinx.extend.logI
import com.kotlinx.extend.readFileFromFolder
import com.kotlinx.extend.readPath
import com.kotlinx.extend.removeAllPermissions
import com.kotlinx.extend.showStackTrace
import com.kotlinx.extend.speak
import com.kotlinx.extend.toBase64String
import com.kotlinx.extend.toJson
import com.kotlinx.extend.toast
import com.kotlinx.extend.writeFileToFolder
import com.kotlinx.extend.writePath
import com.kotlinx.test.MainActivity.Companion.activity
import com.kotlinx.test.ui.theme.KotlinxTheme
import com.kotlinx.utils.ExternalFile
import com.kotlinx.utils.io
import com.kotlinx.utils.ui
import kotlinx.coroutines.delay
import java.io.File
import java.util.Date

class MainActivity : ComponentActivity() {
    companion object {
        var context: Context? = null
        lateinit var activity: ComponentActivity
    }

    var ip: String
        get() = "服务器IP.txt".readPath(Kotlinx.app.filesDir.path) ?: "127.0.0.1"
        set(value) = value.writePath("服务器IP.txt", Kotlinx.app.filesDir?.path ?: "")

    var ip2: String
        get() = ExternalFile.readFile("服务器IP.txt") ?: "127.0.0.1"
        set(value) = ExternalFile.writeFile(value, "服务器IP.txt")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        activity = this
        Kotlinx.init(application)

        setContent {
            KotlinxTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    Greeting("Android")
                }
            }
        }
        LogListener = listener@{ type, tag, msg, e ->
            if (tag == "StackTrace") return@listener
            if (type == Log.INFO) "${Date().format()}  $tag  $msg\r\n".addFile(File(Kotlinx.app?.getExternalFilesDir("")?.absolutePath + "/log.log"))
        }
        ip2.logI("服务器IP")
        ip2 = "192.168.1.109"
        ip2.logI("服务器IP")
    }
}


@Composable
fun Greeting(name: String) {
    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(10.dp)
    ) {
        Row {
            Text("细雨若静❤！", fontSize = 30.sp)
            Text(
                "Android开发测试", color = Color.Blue, modifier = Modifier
                    .clickable {}
                    .padding(10.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Cyan), horizontalArrangement = Arrangement.SpaceAround
        ) {
            var i = 0
            Button(
                modifier = Modifier.padding(0.dp),
                onClick = {
                    Thread { "点击一下${i++}".toast().speak().logI() }.start()
                },
            ) {
                Text(
                    text = "线程toast",
                    style = TextStyle(color = Color.White, fontSize = 12.sp)
                )
            }

            Button(modifier = Modifier.padding(0.dp), onClick = {
                println("你好".toBase64String()).showStackTrace()
                MainActivity.context?.startActivity(Intent(MainActivity.context, RecyclerViewTestActivity::class.java))
            }) {
                Text(
                    text = "RecyclerView",
                    style = TextStyle(color = Color.White, fontSize = 12.sp)
                )
            }

            Button(modifier = Modifier.padding(0.dp), onClick = {
                showStackTrace() //打印堆栈信息
                val json2: String? = """[{"id":11,"name":"哈哈哈"},{"id":22,"name":"嘿嘿嘿"}]"""

                class TestBean(var id: Int, var name: String?)

                val list = json2.jsonToObject<List<TestBean>>()
                list?.forEach { i ->
                    i.toJson().logI()
                    Toast.makeText(MainActivity.context, "解析结果:${i.id},${i.name}", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text(text = "json转对象", style = TextStyle(color = Color.White, fontSize = 12.sp))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Cyan), horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(modifier = Modifier.padding(0.dp), onClick = {
                "1111111111111111111 ${Thread.currentThread().name}".logI()
                io {
                    delay(2000)
                    "22222222222222222222 ${Thread.currentThread().name}".logI()
                    ui {
                        "33333333333333333333 ${Thread.currentThread().name}".logI()
                    }
                }
                "444444444444444444 ${Thread.currentThread().name}".logI()
            }) {
                Text(text = "io", style = TextStyle(color = Color.White, fontSize = 12.sp))
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Cyan), horizontalArrangement = Arrangement.SpaceAround
        ) {
            activity.contentResolver.persistedUriPermissions.forEach {
                println("已持久化的 Uri: ${it.uri}, 读权限: ${it.isReadPermission}, 写权限: ${it.isWritePermission}")
            }

            Button(modifier = Modifier.padding(0.dp), onClick = {
                if (activity.contentResolver.persistedUriPermissions.isNotEmpty()) return@Button "已有授权文件夹".logI().toast().let {}
                val requestStorageAccess = activity.activityResultRegistry.register("key_storage_access", ActivityResultContracts.OpenDocumentTree()) { uri: Uri? ->
                    uri?.let {
                        // 持久化权限
                        activity.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                        println("用户选择的目录: $it ")
                    } ?: run {
                        println("用户取消了目录选择")
                    }
                }
                // 获取SD卡根目录URI作为初始路径
                val sdCardUri = getSDCardRootUri(activity)
                requestStorageAccess.launch(sdCardUri)
            }) {
                Text(text = "SAF权限", style = TextStyle(color = Color.White, fontSize = 12.sp))
            }

            Button(modifier = Modifier.padding(0.dp), onClick = {
                removeAllPermissions(activity)
            }) {
                Text(text = "SAF移除权限", style = TextStyle(color = Color.White, fontSize = 12.sp))
            }

            Button(modifier = Modifier.padding(0.dp), onClick = {
                val uri = activity.contentResolver.persistedUriPermissions.firstOrNull()?.uri
                uri?.let { it ->
                    val success = it.writeFileToFolder(activity, "哈哈哈", "test.txt")
                    (if (success) "写入成功" else "写入失败").toast().logI()
                } ?: "没有授权文件夹".toast().logI()
            }) {
                Text(text = "写文件", style = TextStyle(color = Color.White, fontSize = 12.sp))
            }

            Button(modifier = Modifier.padding(0.dp), onClick = {
                val uri = activity.contentResolver.persistedUriPermissions.firstOrNull()?.uri
                if (uri == null) return@Button "没有授权文件夹".toast().logI().let {}
                val value = uri?.readFileFromFolder(activity, "test.txt")
                "读取结果：${value}".toast().logI()
            }) {
                Text(text = "读文件", style = TextStyle(color = Color.White, fontSize = 12.sp))
            }

        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KotlinxTheme {
        Greeting("Android")
    }
}

fun show(string: String) {
    Toast.makeText(MainActivity.context, string, Toast.LENGTH_LONG).show()
}