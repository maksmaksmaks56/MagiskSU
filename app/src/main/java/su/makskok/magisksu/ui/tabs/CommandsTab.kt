package su.makskok.magisksu.ui.tabs

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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Extension
import androidx.compose.material.icons.filled.GppGood
import androidx.compose.material.icons.filled.GppMaybe
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import su.makskok.magisksu.data.ShellCommand
import su.makskok.magisksu.data.runSuCommand
import su.makskok.magisksu.ui.theme.Divider
import su.makskok.magisksu.ui.theme.Green
import su.makskok.magisksu.ui.theme.Surface
import su.makskok.magisksu.ui.theme.Yellow

val availableCommands = listOf(
    ShellCommand("Версия ядра",          "uname -r",                      "Текущая версия ядра Linux",                    Icons.Default.Memory),
    ShellCommand("CPU информация",       "cat /proc/cpuinfo | head -20",  "Первые 20 строк /proc/cpuinfo",                Icons.Default.DeveloperBoard),
    ShellCommand("Память",               "free -m",                       "Использование RAM в мегабайтах",               Icons.Default.Storage),
    ShellCommand("Место на диске",       "df -h",                         "Использование дискового пространства",         Icons.Default.SdCard),
    ShellCommand("Список процессов",     "ps -ef | head -30",             "Активные процессы (первые 30)",                Icons.Default.ListAlt),
    ShellCommand("Сетевые интерфейсы",   "ip addr",                       "Информация о сетевых интерфейсах",             Icons.Default.Wifi),
    ShellCommand("Дата и время",         "date",                          "Текущая дата и время системы",                 Icons.Default.AccessTime),
    ShellCommand("Аптайм",               "uptime",                        "Время работы с последней перезагрузки",        Icons.Default.Timer),
    ShellCommand("Загрузка CPU",         "cat /proc/loadavg",             "Средняя нагрузка (1/5/15 мин)",                Icons.Default.Speed),
    ShellCommand("SELinux статус",       "getenforce",                    "Текущий режим SELinux",                        Icons.Default.Security),
    ShellCommand("Смена SELinux статуса.","setenforce 1",                 "Меняет SELinux статус на 'Enforcing'(Принудительный)", Icons.Default.GppGood),
    ShellCommand("Смена SELinux статуса.","setenforce 0",                 "Меняет SELinux статус на 'Permissive'(Позволительный)", Icons.Default.GppMaybe),
    ShellCommand("Модули ядра",          "lsmod",                         "Загруженные модули ядра",                      Icons.Default.Extension),
    ShellCommand("Свойства сборки",      "getprop | grep ro.build",       "Build-свойства устройства",                    Icons.Default.PhoneAndroid),
    ShellCommand("Версия ядра (полная)", "cat /proc/version",             "Полная строка версии ядра",                    Icons.Default.Shield),
    ShellCommand("Переменные среды",     "printenv",                      "Все переменные окружения",                     Icons.Default.Code),
    ShellCommand("Перезапуск устройства", "reboot",                       "Перезапускает Устройство",                     Icons.Default.RestartAlt),
    ShellCommand("Перезапуск устройства в Recovery", "reboot recovery",   "Перезапускает Устройство в Recovery",          Icons.Default.RestartAlt),
    ShellCommand("Перезапуск устройства в Fastboot", "reboot bootloader", "Перезапускает Устройство в BootLoader",        Icons.Default.RestartAlt),
    ShellCommand("Перезапуск устройства в Fastbootd", "reboot fastboot",  "Перезапускает Устройство в Fastbootd",         Icons.Default.RestartAlt),
)

@Composable
fun CommandsTab() {
    var output         by remember { mutableStateOf<String?>(null) }
    var activeCmd      by remember { mutableStateOf<String?>(null) }
    var isRunning      by remember { mutableStateOf(false) }
    var terminalWeight by remember { mutableFloatStateOf(0f) }
    var copied         by remember { mutableStateOf(false) }
    val scope          = rememberCoroutineScope()
    val clipboard      = LocalClipboardManager.current

    Column(Modifier.fillMaxSize()) {
        if (terminalWeight > 0f) {
            Column(
                modifier = Modifier.fillMaxWidth().weight(terminalWeight)
                    .background(Color(0xFF060606))
            ) {
                Row(
                    modifier          = Modifier.fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(8.dp).background(
                            if (isRunning) Yellow else Green,
                            RoundedCornerShape(4.dp)
                        )
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text       = if (activeCmd != null) "\$ $activeCmd" else "terminal",
                        color      = if (activeCmd != null) Green else Color(0xFF444444),
                        fontSize   = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        modifier   = Modifier.weight(1f),
                        maxLines   = 1,
                        overflow   = TextOverflow.Ellipsis
                    )
                    IconButton(
                        onClick  = {
                            output?.let { clipboard.setText(AnnotatedString(it)); copied = true }
                        },
                        modifier = Modifier.size(36.dp),
                        enabled  = output != null && !isRunning
                    ) {
                        Icon(
                            if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                            null,
                            tint     = if (copied) Green else Color(0xFF666666),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick  = { terminalWeight = if (terminalWeight < 1.2f) 1.8f else 0.8f },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            if (terminalWeight > 1f) Icons.Default.KeyboardArrowDown
                            else Icons.Default.KeyboardArrowUp,
                            null, tint = Color(0xFF666666), modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick  = { output = null; activeCmd = null; terminalWeight = 0f; copied = false },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Close, null, tint = Color(0xFF555555), modifier = Modifier.size(18.dp))
                    }
                }

                HorizontalDivider(color = Divider)

                val scrollState = rememberScrollState()
                LaunchedEffect(output) {
                    scrollState.animateScrollTo(scrollState.maxValue)
                    copied = false
                }

                Box(Modifier.fillMaxSize().padding(horizontal = 14.dp, vertical = 10.dp)) {
                    if (isRunning) {
                        Row(Modifier.align(Alignment.Center), verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(color = Green, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(Modifier.width(10.dp))
                            Text("Выполняется…", color = Color(0xFF666666), fontSize = 12.sp, fontFamily = FontFamily.Monospace)
                        }
                    } else {
                        Text(
                            text       = output ?: "",
                            color      = Color.White,
                            fontSize   = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 18.sp,
                            modifier   = Modifier.verticalScroll(scrollState)
                        )
                    }
                }
            }
            HorizontalDivider(color = Color(0xFF1A1A1A))
        }

        LazyColumn(
            modifier            = Modifier.fillMaxWidth()
                .weight((if (terminalWeight > 0f) (2.8f - terminalWeight) else 1f).coerceAtLeast(0.5f))
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding      = PaddingValues(vertical = 12.dp)
        ) {
            items(availableCommands, key = { it.cmd }) { command ->
                CommandCard(command) {
                    activeCmd      = command.cmd
                    terminalWeight = if (terminalWeight == 0f) 0.8f else terminalWeight
                    copied         = false
                    scope.launch {
                        isRunning = true
                        output    = runSuCommand(command.cmd)
                        isRunning = false
                    }
                }
            }
        }
    }
}

@Composable
fun CommandCard(command: ShellCommand, onClick: () -> Unit) {
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
                modifier         = Modifier.size(40.dp)
                    .background(Green.copy(alpha = 0.10f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) { Icon(command.icon, null, tint = Green, modifier = Modifier.size(20.dp)) }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(command.title, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(2.dp))
                Text(command.description, color = Color(0xFF666666), fontSize = 12.sp)
            }
            Spacer(Modifier.width(8.dp))
            Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF3A3A3A))
        }
    }
}