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
import androidx.compose.material.icons.filled.Info
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
import su.makskok.magisksu.ui.theme.Divider
import su.makskok.magisksu.ui.theme.Surface
import su.makskok.magisksu.ui.theme.Yellow
import su.makskok.magisksu.ui.theme.Yellow40

@Composable
fun HelpHeroCard() {
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
                Text(it, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.End)
            }
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