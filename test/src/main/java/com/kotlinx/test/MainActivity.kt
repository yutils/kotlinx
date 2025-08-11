package com.kotlinx.test

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import com.kotlinx.extend.jsonToObject
import com.kotlinx.extend.logI
import com.kotlinx.extend.readPath
import com.kotlinx.extend.showStackTrace
import com.kotlinx.extend.speak
import com.kotlinx.extend.toBase64String
import com.kotlinx.extend.toast
import com.kotlinx.extend.writePath
import com.kotlinx.test.ui.theme.KotlinxTheme
import java.io.File
import java.util.Date

class MainActivity : ComponentActivity() {
    companion object {
        var context: Context? = null
    }

    var ip: String
        get() = "服务器IP.txt".readPath(Kotlinx.app.filesDir.path) ?: "127.0.0.1"
        set(value) = value.writePath("服务器IP.txt", Kotlinx.app.filesDir?.path ?: "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
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
        ip.logI("服务器IP")
        ip = "192.168.1.109"
        ip.logI("服务器IP")
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
                    style = TextStyle(
                        color = Color.White, // 设置字体颜色为红色
                        fontSize = 12.sp // 设置字体大小为 16sp
                    )
                )
            }

            Button(modifier = Modifier.padding(0.dp), onClick = {
                println("你好".toBase64String()).showStackTrace()
                MainActivity.context?.startActivity(Intent(MainActivity.context, RecyclerViewTestActivity::class.java))
            }) {
                Text(
                    text = "RecyclerView",
                    style = TextStyle(
                        color = Color.White, // 设置字体颜色为红色
                        fontSize = 12.sp // 设置字体大小为 16sp
                    )
                )
            }

            Button(modifier = Modifier.padding(0.dp), onClick = {
                showStackTrace() //打印堆栈信息
                val json2 = """[{"id":11,"name":"哈哈哈"},{"id":22,"name":"嘿嘿嘿"}]"""

                class TestBean(var id: Int, var name: String?)

                val list = json2.jsonToObject<List<TestBean>>()
                for (i in list!!) {
                    Toast.makeText(MainActivity.context, "解析结果:${i.id},${i.name}", Toast.LENGTH_SHORT).show()
                }
            }) {
                Text(
                    text = "json转对象",
                    style = TextStyle(
                        color = Color.White, // 设置字体颜色为红色
                        fontSize = 12.sp // 设置字体大小为 16sp
                    )
                )
            }
        }
        Button(modifier = Modifier.padding(0.dp), onClick = {

        }) {
            Text(
                text = "空",
                style = TextStyle(
                    color = Color.White, // 设置字体颜色为红色
                    fontSize = 12.sp // 设置字体大小为 16sp
                )
            )
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