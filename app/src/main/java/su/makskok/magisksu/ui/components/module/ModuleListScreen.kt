package su.makskok.magisksu.ui.components.module

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FolderOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import su.makskok.magisksu.data.utils.Module
import su.makskok.magisksu.ui.theme.Green
import su.makskok.magisksu.ui.theme.Red

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