package com.kotlinx.test

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kotlinx.Kotlinx
import com.kotlinx.extend.*
import com.kotlinx.test.ui.theme.KotlinxTheme
import java.io.File
import java.util.*

class MainActivity : ComponentActivity() {
    companion object {
        var context: Context? = null
    }

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
            Text("Android开发测试", color = Color.Blue, modifier = Modifier
                .clickable {}
                .padding(10.dp))
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color.Cyan), horizontalArrangement = Arrangement.SpaceAround
        ) {
            var i = 0
            Button(onClick = {
                Thread { "点击一下${i++}".toast().speak().logI() }.start()
            }) {
                Text("测试1")
            }

            Button(onClick = {
                println("你好".toBase64String()).showStackTrace()
            }) {
                Text("测试2")
            }


            Button(onClick = {
                Toast.makeText(MainActivity.context, "点击一下", Toast.LENGTH_LONG).show()
                showStackTrace()

                "测试文字111111111111111111111111111111".logI()

            }) {
                Text("测试3")
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