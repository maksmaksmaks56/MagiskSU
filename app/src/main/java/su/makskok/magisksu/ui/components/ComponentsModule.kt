package su.makskok.magisksu.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import su.makskok.magisksu.data.Module
import su.makskok.magisksu.data.runSuCommand
import su.makskok.magisksu.ui.theme.Divider
import su.makskok.magisksu.ui.theme.Green
import su.makskok.magisksu.ui.theme.Red
import su.makskok.magisksu.ui.theme.Surface
import su.makskok.magisksu.ui.theme.Yellow

@Composable
fun ModuleListScreen(
    modules:  List<Module>,
    loaded:   Boolean,
    rootAvailable: Boolean,
    onOpen:   (Module) -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        when {
            !loaded -> CircularProgressIndicator(
                color = Green,
                modifier = Modifier.align(Alignment.Center)
            )
            !rootAvailable -> Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.FolderOff, null, tint = Red, modifier = Modifier.size(52.dp))
                Spacer(Modifier.height(12.dp))
                Text("Не доступно для чтения", color = Color(0xFF666666), fontSize = 15.sp)
            }
            modules.isEmpty() -> Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.FolderOff, null, tint = Color(0xFF444444), modifier = Modifier.size(52.dp))
                Spacer(Modifier.height(12.dp))
                Text("Модули не найдены", color = Color(0xFF666666), fontSize = 15.sp)
            }
            else -> LazyColumn(
                modifier            = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding      = PaddingValues(vertical = 16.dp)
            ) {
                items(modules, key = { it.id }) { module ->
                    ModuleListCard(module = module, onClick = { onOpen(module) })
                }
            }
        }
    }
}

@Composable
fun ModuleListCard(module: Module, onClick: () -> Unit) {
    Card(
        onClick  = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape    = RoundedCornerShape(14.dp),
        border   = BorderStroke(1.dp, Divider),
        colors   = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier         = Modifier.size(42.dp)
                    .background(Green.copy(alpha = 0.09f), RoundedCornerShape(11.dp)),
                contentAlignment = Alignment.Center
            ) { Icon(Icons.Default.Extension, null, tint = Green, modifier = Modifier.size(22.dp)) }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(module.name, color = Color.White, fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(3.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Chip(module.version, Green)
                    if (module.hasAction) Chip("action", Yellow)
                }
            }
            Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF3A3A3A))
        }
    }
}



// ── Детальный экран модуля ────────────────────────────────────────────────────

@Composable
fun ModuleDetailScreen(
    module:     Module,
    onBack:     () -> Unit,
    onDeleted:  () -> Unit
) {
    var log by remember { mutableStateOf("") }
    var isRunning by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val clipboard = LocalClipboardManager.current
    val logScroll = rememberScrollState()

    LaunchedEffect(log) { logScroll.animateScrollTo(logScroll.maxValue) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor   = Color(0xFF1A1A1A),
            title   = { Text("Удалить модуль?", color = Color.White) },
            text    = { Text("«${module.name}» будет удалён без возможности восстановления.",
                color = Color(0xFF999999)) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    scope.launch {
                        isRunning = true
                        log += "\n\$ rm -rf /data/adb/modules/${module.id}\n"
                        val result = runSuCommand("rm -rf /data/adb/modules/${module.id}")
                        log += if (result == "(нет вывода)") "✓ Модуль удалён\n" else result
                        isRunning = false
                        onDeleted()
                    }
                }) { Text("Удалить", color = Red) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Отмена", color = Color(0xFF777777))
                }
            }
        )
    }

    Column(Modifier.fillMaxSize()) {
        Surface(color = Surface) {
            Column(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                        Icon(Icons.Default.ArrowBack, "Назад", tint = Color.White)
                    }
                    Spacer(Modifier.width(8.dp))
                    Column(Modifier.weight(1f)) {
                        Text(module.name, color = Color.White, fontSize = 17.sp,
                            fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Text("${module.version} · ${module.author}", color = Color(0xFF666666), fontSize = 12.sp)
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, "Удалить", tint = Red)
                    }
                }
                if (module.description.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    Text(module.description, color = Color(0xFF888888), fontSize = 13.sp, lineHeight = 19.sp)
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (module.hasAction) {
                        Button(
                            onClick = {
                                scope.launch {
                                    isRunning = true
                                    log += "\n\$ sh /data/adb/modules/${module.id}/action.sh\n"
                                    val result = runSuCommand("sh /data/adb/modules/${module.id}/action.sh")
                                    log += result
                                    isRunning = false
                                }
                            },
                            enabled = !isRunning,
                            colors  = ButtonDefaults.buttonColors(containerColor = Green),
                            shape   = RoundedCornerShape(5.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(5.dp))
                            Text("Запустить", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                    OutlinedButton(
                        onClick = { clipboard.setText(AnnotatedString(log)) },
                        enabled = log.isNotBlank(),
                        border  = BorderStroke(1.dp, Divider),
                        shape   = RoundedCornerShape(5.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, null, tint = Color(0xFF888888), modifier = Modifier.size(15.dp))
                        Spacer(Modifier.width(5.dp))
                        Text("Копировать", color = Color(0xFF888888))
                    }
                    if (log.isNotBlank()) {
                        OutlinedButton(
                            onClick = { log = "" },
                            border  = BorderStroke(1.dp, Divider),
                            shape   = RoundedCornerShape(5.dp)
                        ) {
                            Icon(Icons.Default.Clear, null, tint = Color.White, modifier = Modifier.size(15.dp))
                            Spacer(Modifier.width(5.dp))
                            Text(" ", color = Color.Red)
                        }
                    }
                }
            }
        }

        HorizontalDivider(color = Divider)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF050505))
                .padding(14.dp)
        ) {
            if (log.isBlank() && !isRunning) {
                Text(
                    text     = if (module.hasAction) "Нажмите «Запустить» для запуска"
                    else "Этот модуль нельзя запустить",
                    color    = Color(0xFF333333),
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Center),
                    textAlign = TextAlign.Center
                )
            }
            Column(Modifier.fillMaxSize()) {
                Text(
                    text       = log,
                    color      = Color.White,
                    fontSize   = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 18.sp,
                    modifier   = Modifier
                        .weight(1f)
                        .verticalScroll(logScroll)
                )
                if (isRunning) {
                    Row(
                        Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = Green, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                        Spacer(Modifier.width(8.dp))
                        Text("Выполняется…", color = Color(0xFF555555), fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace)
                    }
                }
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