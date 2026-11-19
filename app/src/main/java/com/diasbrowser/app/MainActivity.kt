package com.diasbrowser.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.graphics.Color

class MainActivity : ComponentActivity() {
    private lateinit var browserController: BrowserController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, true)
        window.statusBarColor = android.graphics.Color.rgb(8, 13, 26)
        window.navigationBarColor = android.graphics.Color.rgb(8, 13, 26)

        val store = BrowserStore(applicationContext)
        browserController = BrowserController(applicationContext, store)

        setContent {
            val colors = darkColorScheme(
                primary = Color(0xFF62D9FF),
                secondary = Color(0xFFB69CFF),
                tertiary = Color(0xFFFF72C6),
                background = Color(0xFF080D1A),
                surface = Color(0xFF10182A),
                surfaceVariant = Color(0xFF1A2740),
                onPrimary = Color(0xFF06111D),
                onBackground = Color(0xFFEAF3FF),
                onSurface = Color(0xFFEAF3FF),
                onSurfaceVariant = Color(0xFFB3C2D8)
            )
            MaterialTheme(colorScheme = colors) {
                BrowserApp(controller = browserController, store = store)
            }
        }
    }

    @Deprecated("Deprecated in Android, but retained for compatibility")
    override fun onBackPressed() {
        if (!browserController.goBack()) super.onBackPressed()
    }

    override fun onDestroy() {
        if (isFinishing && ::browserController.isInitialized) browserController.destroy()
        super.onDestroy()
    }
}
