package com.kotlinx.test

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kotlinx.Kotlinx
import com.kotlinx.test.harness.TestStore
import com.kotlinx.test.ui.Screen
import com.kotlinx.test.ui.TestApp
import com.kotlinx.test.ui.theme.KotlinxTheme
import com.kotlinx.test.ui.theme.Paper

class MainActivity : ComponentActivity() {
    private lateinit var store: TestStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Kotlinx.init(application)
        store = TestStore(applicationContext)
        setContent {
            KotlinxTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = Paper) {
                    var screen by remember { mutableStateOf<Screen>(Screen.Home) }
                    TestApp(store = store, screen = screen, onNavigate = { screen = it })
                }
            }
        }
    }
}
