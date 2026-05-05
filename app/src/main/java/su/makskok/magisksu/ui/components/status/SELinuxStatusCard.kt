package su.makskok.magisksu.ui.components.status

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun BooleanInfoCard(
    label: String,
    value: Boolean?,
    icon: ImageVector
) {
    val text: String? = when (value) {
        true  -> "Позволительный"
        false -> "Принудительный"
        null  -> "--"
    }
    InfoCard(label = label, value = text, icon = icon)
}