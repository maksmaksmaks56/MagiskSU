package su.makskok.magisksu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import su.makskok.magisksu.data.LogEntry
import su.makskok.magisksu.data.LogLevel
import su.makskok.magisksu.data.SuCache

@Composable
fun LogEntryCard(entry: LogEntry) {
    val levelColor = when (entry.level) {
        LogLevel.INFO    -> Color(0xFF4CAF50)
        LogLevel.WARNING -> Color(0xFFFFC107)
        LogLevel.ERROR   -> Color(0xFFF44336)
        LogLevel.FATAL   -> Color(0xFFFF0000)
    }

    val sourceLabel = if (entry.source == "r") "root" else if (entry.source == "u") SuCache.whoami else "Unknown"

    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F0F0F)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = entry.formattedTime,
                    color = Color(0xFF888888),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = sourceLabel,
                        color = Color(0xFF888888),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(levelColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = entry.level.label,
                            color = levelColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${entry.code}: ${entry.message}",
                color = Color.White,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                lineHeight = 16.sp
            )
            if (entry.detail.isNotBlank()) {
                Text(
                    text = entry.detail,
                    color = Color(0xFFAAAAAA),
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 3
                )
            }
        }
    }
}