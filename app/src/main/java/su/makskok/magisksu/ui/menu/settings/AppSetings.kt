package su.makskok.magisksu.ui.menu.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.launch
import su.makskok.magisksu.data.settings.AppSettings
import su.makskok.magisksu.data.utils.SuCache

private val Green = Color(0xFF00E676)
private val Divider = Color(0xFF2A2A2A)

// ═══════════════════════════════════════════
// Экран настроек
// ═══════════════════════════════════════════
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        AppSettings.init(context)
    }

    var showClearCacheDialog by remember { mutableStateOf(false) }
    var showResetSettingsDialog by remember { mutableStateOf(false) }

    if (showClearCacheDialog) {
        AlertDialog(
            onDismissRequest = { showClearCacheDialog = false },
            containerColor = Color(0xFF1A1A1A),
            title = { Text("Очистить кэш?", color = Color.White) },
            text = { Text("Данные о модулях будут удалены из памяти.", color = Color(0xFF999999)) },
            confirmButton = {
                TextButton(onClick = {
                    showClearCacheDialog = false
                    scope.launch {
                        SuCache.modules = emptyList()
                        SuCache.moduleCount = 0
                    }
                }) { Text("Очистить", color = Green) }
            },
            dismissButton = {
                TextButton(onClick = { showClearCacheDialog = false }) {
                    Text("Отмена", color = Color(0xFF777777))
                }
            }
        )
    }

    if (showResetSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showResetSettingsDialog = false },
            containerColor = Color(0xFF1A1A1A),
            title = { Text("Сбросить настройки?", color = Color.White) },
            text = { Text("Все настройки будут возвращены к значениям по умолчанию.", color = Color(0xFF999999)) },
            confirmButton = {
                TextButton(onClick = {
                    showResetSettingsDialog = false
                    AppSettings.resetAll()
                }) { Text("Сбросить", color = Green) }
            },
            dismissButton = {
                TextButton(onClick = { showResetSettingsDialog = false }) {
                    Text("Отмена", color = Color(0xFF777777))
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        TopAppBar(
            title = {
                Text(
                    "Настройки",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, "Назад", tint = Color.White)
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
        )

        HorizontalDivider(color = Divider)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            SettingsCategoryHeader("Основные")

            SettingsToggleItem(
                icon = Icons.Default.CheckCircle,
                title = "Автоматическая проверка root",
                subtitle = "Проверять статус root при каждом запуске",
                checked = AppSettings.autoCheckRoot,
                onCheckedChange = { AppSettings.setAutoCheckRoot(it) }
            )

            SettingsToggleItem(
                icon = Icons.Default.Notifications,
                title = "Уведомления",
                subtitle = "Показывать уведомления о состоянии root",
                checked = AppSettings.showNotifications,
                onCheckedChange = { AppSettings.setShowNotifications(it) }
            )

            HorizontalDivider(color = Divider, modifier = Modifier.padding(horizontal = 16.dp))

            SettingsCategoryHeader("Данные")

            SettingsClickItem(
                icon = Icons.Default.Delete,
                title = "Очистить кэш",
                subtitle = "Удалить сохранённые данные о модулях",
                onClick = { showClearCacheDialog = true }
            )

            SettingsClickItem(
                icon = Icons.Default.Refresh,
                title = "Сбросить настройки",
                subtitle = "Вернуть настройки по умолчанию",
                onClick = { showResetSettingsDialog = true }
            )

            HorizontalDivider(color = Divider, modifier = Modifier.padding(horizontal = 16.dp))

            SettingsCategoryHeader("О приложении")

            SettingsInfoItem(
                icon = Icons.Default.Info,
                title = "Версия приложения",
                value = SuCache.version_app
            )

            SettingsInfoItem(
                icon = Icons.Default.Code,
                title = "Версия libsu",
                value =
            )
        }
    }
}

// ─── Вспомогательные компоненты ─────────────────────────────────

@Composable
private fun SettingsCategoryHeader(text: String) {
    Text(
        text = text,
        color = Green,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
    )
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = checked, onClick = { onCheckedChange(!checked) })
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color(0xFF888888), modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 15.sp)
            Text(subtitle, color = Color(0xFF777777), fontSize = 12.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Green,
                checkedTrackColor = Green.copy(alpha = 0.3f)
            )
        )
    }
}

@Composable
private fun SettingsClickItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = false, onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color(0xFF888888), modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = Color.White, fontSize = 15.sp)
            Text(subtitle, color = Color(0xFF777777), fontSize = 12.sp)
        }
        Icon(Icons.Default.ChevronRight, null, tint = Color(0xFF3A3A3A))
    }
}

@Composable
private fun SettingsInfoItem(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color(0xFF888888), modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(16.dp))
        Text(title, color = Color(0xFF777777), fontSize = 15.sp, modifier = Modifier.weight(1f))
        Text(value, color = Color.White, fontSize = 14.sp)
    }
}