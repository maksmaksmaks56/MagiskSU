package su.makskok.magisksu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import su.makskok.magisksu.data.*
import su.makskok.magisksu.ui.theme.*
import su.makskok.magisksu.ui.MainUI

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppSettings.init(this)
        lifecycleScope.launch {
            SuCache.load(this@MainActivity)
        }
        setContent {
            MaterialTheme(colorScheme = AmoledTheme) {
                MainUI()
            }
        }
    }
}