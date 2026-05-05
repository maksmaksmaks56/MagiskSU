package su.makskok.magisksu.ui

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import su.makskok.magisksu.kernel.data.RootCache
import su.makskok.magisksu.kernel.ui.menu.RootSetupScreen
import su.makskok.magisksu.supersu.ui.tabs.SuperUserTab
import su.makskok.magisksu.ui.menu.settings.AppSettingsScreen
import su.makskok.magisksu.ui.menu.commands.CommandsMenu
import su.makskok.magisksu.ui.tabs.CommandsTab
import su.makskok.magisksu.ui.tabs.HelpTab
import su.makskok.magisksu.ui.tabs.InfoTab
import su.makskok.magisksu.ui.tabs.LogsTab
import su.makskok.magisksu.ui.tabs.ModulesTab
import su.makskok.magisksu.ui.tabs.StatusTab
import su.makskok.magisksu.ui.theme.Green
import su.makskok.magisksu.ui.theme.Surface

@Composable
@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
fun MainUI() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showSettings by remember { mutableStateOf(false) }
    var showRootSetup by remember { mutableStateOf(false) }
    var showCommandMenu by remember { mutableStateOf(false) }
    if (showSettings) {
        BackHandler(enabled = true) { showSettings = false }
        AppSettingsScreen(onBack = { showSettings = false })
        return
    }

    if (showRootSetup) {
        BackHandler(enabled = true) { showRootSetup = false }
        RootSetupScreen(onBack = { showRootSetup = false })
        return
    }

    if (showCommandMenu) {
        BackHandler(enabled = true) { showCommandMenu = false }
        CommandsMenu(onBack = { showCommandMenu = false })
        return
    }

    // Отслеживаем наличие su
    val rooted by derivedStateOf { RootCache.noRooted == false }

    // Динамический список вкладок
    val tabs = remember(rooted) {
        if (rooted) {
            listOf(
                "Статус"    to Icons.Default.Shield,
                "Модули"    to Icons.Default.ViewModule,
                "Superuser" to Icons.Default.People,   // вместо Команд
                "Об Уст..." to Icons.Default.Android,
                "Инфо"      to Icons.Default.Info,
                "Логи"      to Icons.Default.Folder
            )
        } else {
            listOf(
                "Статус"    to Icons.Default.Shield,
                "Модули"    to Icons.Default.ViewModule,
                "Команды"   to Icons.Default.List,
                "Об Уст..." to Icons.Default.Android,
                "Инфо"      to Icons.Default.Info,
                "Логи"      to Icons.Default.Folder
            )
        }
    }

    if (showSettings) {
        AppSettingsScreen(onBack = { showSettings = false })
        return
    }

    if (showRootSetup) {
        // Заглушка экрана рутирования (позже заменим на настоящий)
        RootSetupScreen(onBack = { showRootSetup = false })
        return
    }

    if (showCommandMenu) {
        CommandsMenu(onBack = {showCommandMenu = false})
        return
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "MagiskSU",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 18.sp
                    )
                },
                actions = {
                    // Иконка Code видна только после рутирования
                    if (rooted) {
                        IconButton(onClick = { showRootSetup = true }) {
                            Icon(
                                Icons.Default.Code,
                                contentDescription = "Настройки Root",
                                tint = Color.White
                            )
                        }

                        IconButton(onClick = {showCommandMenu = true}) {
                            Icon(
                                Icons.Default.List,
                                contentDescription = "Быстрые Команды",
                                tint = Color.White
                            )
                        }
                    }
                    IconButton(onClick = { showSettings = true }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Настройки",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Black)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Surface, tonalElevation = 0.dp) {
                tabs.forEachIndexed { index, (label, icon) ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        label = { Text(label, fontSize = 10.sp) },
                        icon = { Icon(icon, contentDescription = label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Green,
                            selectedTextColor = Green,
                            indicatorColor = Green.copy(alpha = 0.12f),
                            unselectedIconColor = Color(0xFF555555),
                            unselectedTextColor = Color(0xFF555555)
                        )
                    )
                }
            }
        }
    ) { pad ->
        Box(Modifier.padding(pad).fillMaxSize().background(Color.Black)) {
            when (selectedTab) {
                0 -> StatusTab(onInstallRoot = {showRootSetup = true})
                1 -> ModulesTab()
                2 -> if (rooted) SuperUserTab() else CommandsTab()
                3 -> InfoTab()
                4 -> HelpTab()
                5 -> LogsTab()
            }
        }
    }
}