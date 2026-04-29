package su.makskok.magisksu

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import su.makskok.magisksu.data.*
import su.makskok.magisksu.kernel.data.RootCache
import su.makskok.magisksu.ui.theme.*
import su.makskok.magisksu.ui.tabs.*
import su.makskok.magisksu.ui.menu.*
import su.makskok.magisksu.kernel.ui.menu.RootSetupScreen
import su.makskok.magisksu.supersu.ui.tabs.SuperUserTab
import androidx.activity.compose.BackHandler

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppSettings.init(this)
        setContent {
            MaterialTheme(colorScheme = AmoledTheme) {
                MainUI()
            }
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainUI() {
    var selectedTab by remember { mutableIntStateOf(0) }
    var showSettings by remember { mutableStateOf(false) }
    var showRootSetup by remember { mutableStateOf(false) }   // новое: экран рутирования
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

    LaunchedEffect(Unit) {
        SuCache.load()
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
                                contentDescription = "Root настройки",
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