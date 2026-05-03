package su.makskok.magisksu.ui.tabs

import android.os.Build
import android.util.Config.DEBUG
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import su.makskok.magisksu.data.SuCache
import su.makskok.magisksu.data.getSELinuxPermissive
import su.makskok.magisksu.ui.components.*

@Composable
fun HelpTab() {
    LazyColumn(
        modifier            = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding      = PaddingValues(vertical = 16.dp)
    ) {
        item { HelpHeroCard() }
        item { HelpCard("Модель",            Build.MODEL,                      Icons.Default.Info) }
        item { HelpCard("Версия Android",    Build.VERSION.RELEASE,            Icons.Default.Tag)  }
        item { HelpCard("Версия SDK",        Build.VERSION.SDK_INT.toString(), Icons.Default.Info) }
        item { HelpCard("Версия Приложения", SuCache.version_app,              Icons.Default.Tag)  }
        item { HelpCard("ID приложения",     SuCache.whoami,                   Icons.Default.Info) }
        item { HelpCard("Пакет",             SuCache.package_manager,          Icons.Default.Info) }
        item { HelpCard("Версия Magisk",     SuCache.magiskVersion,            Icons.Default.Tag)  }
        item { BooleanHelpCard(label = "SELinux режим",value = getSELinuxPermissive(),icon = Icons.Default.Info) }
        item { HelpCard("Доступ Root",       if (SuCache.rootAccess == true && SuCache.onOffRoot == true) "true" else if (SuCache.onOffRoot == false) "unknown" else "false", Icons.Default.Info) }
        item { HelpCard("Просто место для эксприментов а так же пояснений", "",Icons.Default.AccessibilityNew) }
        item { HelpCard("Ошибки",            "",                               Icons.Default.Android) }
        item { HelpCard("Расшифровка '[L]_[N_L][type][N]' где первое это уровень, второе это номер уровня, третье это и тип, а четвертое номер по порядку введения ошибки(0 всегда первое)", "", Icons.Default.Info) }
        item { HelpCard("Ошибка 'F_1r0' возникает когда 'su' не найден или не может быть запущен.","",Icons.Default.Info) }
        item { HelpCard("Ошибка 'E_2r-p1' возникает при ошибке прав доступа.","",Icons.Default.Info) }
        item { HelpCard("Ошибка 'E_2r2([CODE])', возникает при завершении su с определенным кодом.","",Icons.Default.Info) }
        item { HelpCard("Ошибка 'F_1u3' – ошибка запуска команды.","",         Icons.Default.Info) }
        item { HelpCard("Ошибка 'E_2u-p4' – ошибка прав доступа.","",          Icons.Default.Info) }
        item { HelpCard("Ошибка 'E_2u5([CODE])' – команда завершилась с ненулевым кодом.","",Icons.Default.Info) }

    }
}
