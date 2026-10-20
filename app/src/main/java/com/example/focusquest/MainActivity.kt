package com.example.focusquest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.core.view.WindowInsetsControllerCompat
import com.example.focusquest.ui.FocusQuestApp
import com.example.focusquest.ui.theme.FocusQuestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = true
            isAppearanceLightNavigationBars = true
        }
        setContent {
            FocusQuestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    FocusQuestApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
