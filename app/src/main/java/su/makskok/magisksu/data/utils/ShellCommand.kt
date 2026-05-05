package su.makskok.magisksu.data.utils

import androidx.compose.ui.graphics.vector.ImageVector

data class ShellCommand(
    val title:       String,
    val cmd:         String,
    val description: String,
    val icon:        ImageVector
)