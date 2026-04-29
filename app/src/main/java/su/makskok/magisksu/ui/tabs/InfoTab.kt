package su.makskok.magisksu.ui.tabs

import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import su.makskok.magisksu.ui.theme.Divider

@Composable
fun InfoTab() {
    val items = remember {
        listOf(
            "Производитель" to Build.MANUFACTURER,
            "Android"       to Build.VERSION.RELEASE,
            "SDK"           to Build.VERSION.SDK_INT.toString(),
            "Архитектура"   to (Build.SUPPORTED_ABIS.firstOrNull() ?: "unknown"),
            "Ядро"          to (System.getProperty("os.version") ?: "unknown")
        )
    }

    Column(
        modifier            = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))
        items.forEachIndexed { index, (label, value) ->
            Column(
                modifier            = Modifier.fillMaxWidth().padding(vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(label.uppercase(), color = Color(0xFF4A4A4A), fontSize = 11.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 2.sp, textAlign = TextAlign.Center)
                Spacer(Modifier.height(6.dp))
                Text(value, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center, fontFamily = FontFamily.Monospace)
            }
            if (index < items.lastIndex)
                HorizontalDivider(modifier = Modifier.fillMaxWidth(0.55f), thickness = 1.dp, color = Divider)
        }
        Spacer(Modifier.height(32.dp))
    }
}