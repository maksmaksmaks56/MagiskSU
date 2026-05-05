package su.makskok.magisksu.data.utils

sealed class ModuleScreen {
    object List : ModuleScreen()
    data class Detail(val module: Module) : ModuleScreen()
}

data class Module(
    val id:          String,
    val name:        String,
    val version:     String,
    val author:      String,
    val description: String,
    val hasAction:   Boolean = false
)