package su.makskok.magisksu.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import su.makskok.magisksu.data.AppSettings
import su.makskok.magisksu.data.SuCache
import su.makskok.magisksu.data.onOffCheckRoot
import su.makskok.magisksu.ui.theme.*


@Composable
fun InfoHeroCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(18.dp),
        colors   = CardDefaults.cardColors(containerColor = Yellow40)
    ) {
        Column(
            modifier            = Modifier.fillMaxWidth().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.Info, null, tint = Color.Black, modifier = Modifier.size(44.dp))
            Spacer(Modifier.height(12.dp))
            Text("Информация", color = Color.Black, fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center,
                fontFamily = FontFamily.Monospace)
            Spacer(Modifier.height(6.dp))
            Text("Об данном приложении и устройстве.", color = Color.Black.copy(alpha = 0.70f),
                fontSize = 15.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun HelpCard(label: String, value: String?, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        border   = BorderStroke(1.dp, Divider),
        colors   = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier         = Modifier.size(38.dp)
                    .background(Yellow.copy(alpha = 0.10f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = Yellow40, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.width(14.dp))
            Text(label, color = Color(0xFF777777), fontSize = 14.sp, modifier = Modifier.weight(1f))
            value?.let {
                Text(it, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End)
            }
        }
    }
}

@Composable
fun InfoCard(label: String, value: String?, icon: ImageVector) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        border   = BorderStroke(1.dp, Divider),
        colors   = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier         = Modifier.size(38.dp)
                    .background(Green.copy(alpha = 0.10f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) { Icon(icon, null, tint = Green, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.width(14.dp))
            Text(label, color = Color(0xFF777777), fontSize = 13.sp, modifier = Modifier.weight(1f))
            value?.let {
                Text(it, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End)
            }
        }
    }
}

@Composable
fun Chip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(text, color = color, fontSize = 10.sp, fontWeight = FontWeight.Medium)
    }
}

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

@Composable
fun BooleanHelpCard(
    label: String,
    value: Boolean?,
    icon: ImageVector
) {
    val text: String? = when (value) {
        true  -> "Позволительный"
        false -> "Принудительный"
        null  -> "--"
    }
    HelpCard(label = label, value = text, icon = icon)
}

@Composable
fun BooleanInfoCard(
    label: String,
    value: Boolean?,
    icon: ImageVector
) {
    val text: String? = when (value) {
        true  -> "Позволительный"
        false -> "Принудительный"
        null  -> "--"
    }
    InfoCard(label = label, value = text, icon = icon)
}