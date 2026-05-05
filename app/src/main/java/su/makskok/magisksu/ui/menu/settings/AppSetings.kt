package su.makskok.magisksu.ui.menu.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.launch
import su.makskok.magisksu.data.settings.AppSettings
import su.makskok.magisksu.data.utils.SuCache
import su.makskok.magisksu.ui.components.settings.*
import su.makskok.magisksu.ui.theme.Green
import su.makskok.magisksu.ui.theme.Divider


private val version_libsu: String = if (Shell.rootAccess() == true) "Да" else "Нет"

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
                title = ("Установлен ли Root\n" +
                        "с помощью этого\n" +
                        "приложения").trim(),
                value = version_libsu
            )
        }
    }
}
