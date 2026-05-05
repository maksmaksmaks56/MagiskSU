package su.makskok.magisksu.ui.components.help

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import su.makskok.magisksu.ui.theme.Yellow40

@Composable
fun HeroCard() {
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