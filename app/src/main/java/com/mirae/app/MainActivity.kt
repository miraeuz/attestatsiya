package com.mirae.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mirae.app.ui.MiraeTheme
import com.mirae.app.ui.screens.MiraeApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var darkTheme by remember { mutableStateOf(false) }
            MiraeTheme(darkTheme = darkTheme) {
                val vm: AppViewModel = viewModel()
                MiraeApp(
                    vm = vm,
                    darkTheme = darkTheme,
                    onDarkThemeChange = { darkTheme = it }
                )
            }
        }
    }
}
