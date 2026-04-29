package su.makskok.magisksu.ui.tabs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.*
import androidx.compose.material3.AlertDialogDefaults.containerColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import su.makskok.magisksu.data.SuCache
import su.makskok.magisksu.data.getSELinuxPermissive
import su.makskok.magisksu.kernel.data.RootCache
import su.makskok.magisksu.ui.components.BooleanInfoCard
import su.makskok.magisksu.ui.components.HeroCard
import su.makskok.magisksu.ui.components.InfoCard
import su.makskok.magisksu.ui.theme.Divider
import su.makskok.magisksu.ui.theme.Green
import su.makskok.magisksu.ui.theme.Surface
import su.makskok.magisksu.ui.theme.White
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun StatusTab(onInstallRoot: () -> Unit) {
    LazyColumn(
        modifier            = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding      = PaddingValues(vertical = 16.dp)
    ) {
        item { HeroCard() }

        // Кнопка «Установить Root» видна, если su отсутствует
        if (RootCache.noRooted == true) {
            item {
                Button(
                    onClick = onInstallRoot,            // ← только открыть экран рутирования
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    border   = BorderStroke(1.dp, Divider),
                    shape    = RoundedCornerShape(14.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = Surface)
                ) {
                    Icon(
                        Icons.Default.Build,
                        null,
                        tint = Green,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Установить Root",
                        color = White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }

        item { InfoCard("Версия",           SuCache.version_app,                   Icons.Default.Tag) }
        item { InfoCard("SELinux контекст", SuCache.selinuxContext,        Icons.Default.Security) }
        item { BooleanInfoCard(label = "SELinux режим", value = getSELinuxPermissive(), Icons.Default.Shield) }
        item { InfoCard("Модули",           "${SuCache.moduleCount} установлено", Icons.Default.ViewModule) }
    }
}