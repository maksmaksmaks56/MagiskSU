package su.makskok.magisksu.ui.components.status

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GppBad
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import su.makskok.magisksu.data.utils.SuCache
import su.makskok.magisksu.ui.theme.Green
import su.makskok.magisksu.ui.theme.Red
import su.makskok.magisksu.ui.theme.Yellow40

@Composable
fun HeroCard() {
    val isRooted = SuCache.rootAccess
    val cardColor = if (isRooted == true && SuCache.onOffRoot == true ) Green else if (isRooted == false && SuCache.onOffRoot == true) Red else if (SuCache.onOffRoot == false) Yellow40 else Color.DarkGray
    val statusText = if (isRooted == true && SuCache.onOffRoot == true) "MagiskSU ― Менеджер Root прав.\nизначально задумывался\nкак копия KernelSU".trim()
    else if (isRooted == false && SuCache.onOffRoot == true) "Root не найден. Я ничего не могу."
    else if (SuCache.onOffRoot == false) "Невозможно проверить Root" else "Cheking..."

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(18.dp),
        colors   = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = if (isRooted == true && SuCache.onOffRoot == true) Icons.Default.GppGood else if (SuCache.onOffRoot == false) Icons.Default.Info else Icons.Default.GppBad,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(44.dp)
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "MagiskSU",
                color = Color.Black,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = FontFamily.Monospace
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = statusText,
                color = Color.Black.copy(alpha = 0.70f),
                fontSize = 13.sp, textAlign = TextAlign.Center
            )
        }
    }
}