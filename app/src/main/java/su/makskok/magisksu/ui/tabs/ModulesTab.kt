package su.makskok.magisksu.ui.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import kotlinx.coroutines.launch
import su.makskok.magisksu.data.utils.ModuleScreen
import su.makskok.magisksu.data.utils.SuCache
import su.makskok.magisksu.ui.components.module.ModuleDetailScreen
import su.makskok.magisksu.ui.components.module.ModuleListScreen

@Composable
fun ModulesTab() {
    var screen by remember { mutableStateOf<ModuleScreen>(ModuleScreen.List) }
    val scope  = rememberCoroutineScope()

    // Определяем, можно ли показывать список: root проверен и доступен
    val rootAvailable = SuCache.rootAccess == true
    val dataLoaded = SuCache.rootAccess != null  // проверка завершена

    when (val s = screen) {
        is ModuleScreen.List   -> ModuleListScreen(
            modules = SuCache.modules,
            loaded = dataLoaded,
            rootAvailable = rootAvailable,
            onOpen = { screen = ModuleScreen.Detail(it) }
        )
        is ModuleScreen.Detail -> ModuleDetailScreen(
            module  = s.module,
            onBack  = { screen = ModuleScreen.List },
            onDeleted = {
                screen = ModuleScreen.List
                scope.launch { SuCache.refreshModules() }
            }
        )
    }
}

