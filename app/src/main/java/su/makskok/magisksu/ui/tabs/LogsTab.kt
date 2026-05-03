package su.makskok.magisksu.ui.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import su.makskok.magisksu.data.AppLogger
import su.makskok.magisksu.data.LogEntry
import su.makskok.magisksu.data.LogLevel
import su.makskok.magisksu.data.SuCache
import su.makskok.magisksu.ui.components.LogEntryCard

@Composable
fun LogsTab() {
    val logs = AppLogger.logs
    val clipboardManager = LocalClipboardManager.current

    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        // Заголовок
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Логи",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            if (logs.isNotEmpty()) {
                Row {
                    // Кнопка копирования
                    IconButton(onClick = {
                        val allLogs = logs.joinToString("\n") { it.fullLine }
                        clipboardManager.setText(AnnotatedString(allLogs))
                    }) {
                        Icon(
                            Icons.Default.ContentCopy,
                            contentDescription = "Копировать логи",
                            tint = Color(0xFF888888),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    // Кнопка очистки
                    IconButton(onClick = { AppLogger.clear() }) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Очистить логи",
                            tint = Color(0xFF888888),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        HorizontalDivider(color = Color(0xFF2A2A2A))

        if (logs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Нет логов", color = Color(0xFF666666), fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(logs) { entry ->
                    LogEntryCard(entry)
                }
            }
        }
    }
}

