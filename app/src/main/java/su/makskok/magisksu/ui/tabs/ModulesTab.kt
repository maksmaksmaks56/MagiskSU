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
import su.makskok.magisksu.data.ModuleScreen
import su.makskok.magisksu.data.SuCache
import su.makskok.magisksu.data.runSuCommand
import su.makskok.magisksu.ui.components.Chip
import su.makskok.magisksu.ui.components.ModuleDetailScreen
import su.makskok.magisksu.ui.components.ModuleListScreen
import su.makskok.magisksu.ui.theme.Divider
import su.makskok.magisksu.ui.theme.Green
import su.makskok.magisksu.ui.theme.Red
import su.makskok.magisksu.ui.theme.Surface
import su.makskok.magisksu.ui.theme.Yellow

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

