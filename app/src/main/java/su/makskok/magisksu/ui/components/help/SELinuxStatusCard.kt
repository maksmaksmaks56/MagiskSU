package su.makskok.magisksu.ui.components.help

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BooleanHelpCard(
    label: String,
    value: Boolean?,
    icon: ImageVector
) {
    val text: String? = when (value) {
        true  -> "Позволительный"
        false -> "Принудительный"
        null  -> "--"
    }
    HelpCard(label = label, value = text, icon = icon)
}