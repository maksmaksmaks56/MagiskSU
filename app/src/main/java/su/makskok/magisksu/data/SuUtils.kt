package su.makskok.magisksu.data

import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import com.topjohnwu.superuser.Shell
import su.makskok.magisksu.data.*
import su.makskok.magisksu.kernel.data.RootCache

suspend fun userCommand(cmd: String): String = withContext(Dispatchers.IO) {
    try {
        val process = Runtime.getRuntime().exec(arrayOf(cmd))
        val out     = process.inputStream.bufferedReader().readText()
        val err     = process.errorStream.bufferedReader().readText()
        val exitCode = process.waitFor()


        if (exitCode != 0) {
            val combined = (err + out).lowercase()
            val errorCode = if (combined.contains("permission denied") ||
                combined.contains("access denied") ||
                combined.contains("not allowed")) {
                "E_2u-p4"
            } else {
                "E_2u5($exitCode)"
            }
            AppLogger.error("u", errorCode, "user command failed", cmd)
            return@withContext errorCode
        }

        AppLogger.info("u", "I_3u6", "Успешно!", cmd)

        when {
            out.isNotBlank() -> out.trim()
            err.isNotBlank() -> err.trim()
            else             -> "(нет вывода)"
        }
    } catch (e: IOException) {
        AppLogger.fatal("u", "F_1u3", "command execution failed", cmd)
        "F_1u3"
    } catch (e: Exception) {
        AppLogger.fatal("u", "U_2u5(-1)", "command execution exception", "${e.message}")
        "U_2u5(-1)"
    }
}

suspend fun runSuCommand(cmd: String): String = withContext(Dispatchers.IO) {
    try {
        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", cmd))
        val out = process.inputStream.bufferedReader().readText()
        val err = process.errorStream.bufferedReader().readText()
        val exitCode = process.waitFor()


        if (exitCode != 0) {
            val combinedError = (err + out).lowercase()
            val errorCode = if (combinedError.contains("permission denied") ||
                combinedError.contains("access denied") ||
                combinedError.contains("not allowed")) {
                "E_2r-p1"
            } else {
                "E_2r2($exitCode)"
            }
            AppLogger.error("r", errorCode, "su команда упала в небытие кода ошибки", cmd)
            return@withContext errorCode
        }

        AppLogger.info("r", "I_3r7", "Успешно!", cmd)

        if (out.isBlank()) "(нет вывода)" else out.trim()
    } catch (e: IOException) {
        AppLogger.fatal("r", "F_1r0", "su не существует или недоступен.", cmd)
        "F_1r0"
    } catch (e: Exception) {
        AppLogger.error("r", "E_2r2(-1)", "su завершился с кодом", "${e.message}")
        "E_2r2(-1)"
    }
}

suspend fun checkRootAccess(): Boolean = withContext(Dispatchers.IO) {
    try {
        val process = Runtime.getRuntime().exec(arrayOf("su", "-c", "id"))
        val output = process.inputStream.bufferedReader().readText()
        process.waitFor()

        output.contains("uid=0")
    } catch (e: Exception) {
        false
    }
}

suspend fun onOffCheckRoot(): Boolean = withContext(Dispatchers.IO) {
    try {
        if (!AppSettings.autoCheckRoot) {
            false
        }
        else {
            true
        }
    } catch (e: Exception) {
        true
    } as Boolean
}



fun isSELinuxPermissive(): String {
    val shell = Shell.Builder.create().build("sh")
    val stdoutList = ArrayList<String>()
    val result = shell.use {
        it.newJob().add("getenforce").to(stdoutList).exec()
    }
    return if (result.isSuccess) {
        when (stdoutList.joinToString("").trim()) {
            "Permissive"  -> "Позволительный"
            "Enforcing"  ->  "Принудительный"
            else         ->  "--"
        }
    } else {
        "--"
    }
}

suspend fun checkerRoot(): String = withContext(Dispatchers.IO) {
    val hmm: Boolean = checkRootAccess()
    if (checkRootAccess() == false) {
        AppLogger.warning("u", "W_4u9", "Root права не найдены.")
    }

    return@withContext hmm.toString()
}

fun getSELinuxPermissive(): Boolean {
    val shell = Shell.Builder.create().build("sh")
    val stdoutList = ArrayList<String>()
    val result = shell.use {
        it.newJob().add("getenforce").to(stdoutList).exec()
    }
    return result.isSuccess && stdoutList.joinToString("").trim() == "Permissive"
}

suspend fun readModuleCount(): Int = withContext(Dispatchers.IO) {
    val raw = runSuCommand("ls -1F /data/adb/modules").trim()
    if (raw.isBlank() || raw.startsWith("Ошибка")) 0
    else raw.lines().count { it.trim().endsWith("/") }
}

suspend fun readModules(): List<Module> = withContext(Dispatchers.IO) {
    val script = """
        for d in /data/adb/modules/*/; do
          id=${'$'}(basename "${'$'}d")
          echo "---MODULE_START---"
          echo "id=${'$'}{id}"
          cat "${'$'}{d}module.prop"
          if [ -f "${'$'}{d}action.sh" ]; then echo "hasAction=yes"; else echo "hasAction=no"; fi
          echo "---MODULE_END---"
        done
    """.trimIndent()

    val raw = runSuCommand(script).trim()
    if (raw.isBlank() || raw.startsWith("Ошибка") || raw.startsWith("E_") || raw.startsWith("F_")) {
        return@withContext emptyList()
    }

    val modules = mutableListOf<Module>()
    val entries = raw.split("---MODULE_START---").drop(1)
    for (entry in entries) {
        val lines = entry.lines()
        val endIdx = lines.indexOf("---MODULE_END---")
        if (endIdx == -1) continue
        val relevantLines = lines.subList(0, endIdx)
        var id = ""
        val props = mutableMapOf<String, String>()
        var hasAction = false

        for (line in relevantLines) {
            when {
                line.startsWith("id=") -> id = line.substringAfter("id=")
                line == "hasAction=yes" -> hasAction = true
                line == "hasAction=no" -> hasAction = false
                '=' in line -> {
                    val idx = line.indexOf('=')
                    props[line.substring(0, idx).trim()] = line.substring(idx + 1).trim()
                }
            }
        }

        if (id.isNotEmpty()) {
            modules.add(
                Module(
                    id          = id,
                    name        = props["name"] ?: id,
                    version     = props["version"] ?: "?",
                    author      = props["author"] ?: "unknown",
                    description = props["description"] ?: "",
                    hasAction   = hasAction
                )
            )
        }
    }
    modules
}

data class Module(
    val id:          String,
    val name:        String,
    val version:     String,
    val author:      String,
    val description: String,
    val hasAction:   Boolean = false
)

data class ShellCommand(
    val title:       String,
    val cmd:         String,
    val description: String,
    val icon:        ImageVector
)



object SuCache {
    var rootAccess: Boolean? by mutableStateOf(null)

    var onOffRoot: Boolean? by mutableStateOf(null)

    var commandUser: String by mutableStateOf("--")

    var selinuxContext: String by mutableStateOf("--")

    var selinuxMode: String by mutableStateOf("--")

    var hmm: String by mutableStateOf("--")

    var whoami: String by mutableStateOf("--")

    var moduleCount: Int by mutableIntStateOf(0)

    var moduleNotRoot: String by mutableStateOf("--")

    var modules: List<Module> by mutableStateOf(emptyList())

    var magiskVersion: String by mutableStateOf("--")


    var version_app: String by mutableStateOf("--")

    var magisk_type: String by mutableStateOf("--")

    suspend fun load() {
        rootAccess = checkRootAccess()
        onOffRoot = onOffCheckRoot()
        hmm = checkerRoot()
        RootCache.check()
        magisk_type = (if (Build.SUPPORTED_ABIS.firstOrNull() == "arm64-v8a") "R.raw.magiskboot_arm64_v8a" else if (Build.SUPPORTED_ABIS.firstOrNull() == "armeabi-v7a") "R.raw.magiskboot_armeabi_v7a" else if (Build.SUPPORTED_ABIS.firstOrNull() == "x86") "R.raw.magiskboot_x86" else if (Build.SUPPORTED_ABIS.firstOrNull() == "x86_64") "R.raw.magiskboot_x86_64" else "")

        if (rootAccess == true || AppSettings.autoCheckRoot) {
            selinuxContext = runSuCommand("cat /proc/self/attr/current").trim()
            if (selinuxContext == "неизвестно" && selinuxContext == "F_1r0") {
                AppLogger.warning("r", "W_4r9", "SELinux контекст не известен")
            }
            selinuxMode = isSELinuxPermissive()
            if (selinuxMode == "--" && selinuxMode == "F_1r0") {
                AppLogger.warning("u", "W_4u8", "SELinux статус неизвестен")
            }
            whoami = userCommand("whoami").trim()
            moduleCount = readModuleCount()
            modules = readModules()
            magiskVersion = runSuCommand("magisk -v").trim().ifBlank { "unknown" }
            if (magiskVersion == "unknown" && magiskVersion == "F_1r0" || magiskVersion.isBlank()) {
                AppLogger.warning("r", "W_4r9", "Magisk версия не получена")
            }
            version_app = "2.0.0"
        } else {
            if (rootAccess == false || !AppSettings.autoCheckRoot) {
                version_app = "2.0.0"
                selinuxContext = runSuCommand("cat /proc/self/attr/current").trim()
                selinuxMode    = isSELinuxPermissive()
                if (selinuxMode == "--" && selinuxMode == "F_1r0") {
                    AppLogger.warning("u", "W_4u8", "SELinux статус неизвестен")
                }
                whoami = userCommand("whoami").trim()
                moduleCount = readModuleCount()
                modules = readModules()
                magiskVersion = runSuCommand("magisk -v").trim().ifBlank { "unknown" }
                if (magiskVersion == "unknown" && magiskVersion == "F_1r0" || magiskVersion.isBlank()) {
                    AppLogger.warning("r", "W_4r9", "Magisk версия не получена")
                }
                magisk_type = (if (Build.SUPPORTED_ABIS.firstOrNull() == "arm64-v8a") "R.raw.magiskboot_arm64_v8a" else if (Build.SUPPORTED_ABIS.firstOrNull() == "armeabi-v7a") "R.raw.magiskboot_armeabi_v7a" else if (Build.SUPPORTED_ABIS.firstOrNull() == "x86") "magiskboot_x86" else if (Build.SUPPORTED_ABIS.firstOrNull() == "x86_64") "R.raw.magiskboot_x86_64" else "") as String
            }
            else {
                version_app = "2.0.0"
                selinuxContext = runSuCommand("cat /proc/self/attr/current").trim()
                selinuxMode = isSELinuxPermissive()
                if (selinuxMode == "--") {
                    AppLogger.warning("u", "W_4u8", "SELinux статус неизвестен")
                }
                whoami = userCommand("whoami").trim()
                moduleCount = readModuleCount()
                modules = readModules()
                magiskVersion = runSuCommand("magisk -v").trim().ifBlank { "unknown" }
                if (magiskVersion == "unknown" && magiskVersion == "F_1r0" || magiskVersion.isBlank()) {
                    AppLogger.warning("r", "W_4r9", "Magisk версия не получена")
                magisk_type = (if (Build.SUPPORTED_ABIS.firstOrNull() == "arm64-v8a") "R.raw.magiskboot_arm64_v8a" else if (Build.SUPPORTED_ABIS.firstOrNull() == "armeabi-v7a") "R.raw.magiskboot_armeabi_v7a" else if (Build.SUPPORTED_ABIS.firstOrNull() == "x86") "R.raw.magiskboot_x86" else if (Build.SUPPORTED_ABIS.firstOrNull() == "x86_64") "R.raw.magiskboot_x86_64" else "") as String
                }
            }
        }
    }

    suspend fun refreshModules() {
        if (rootAccess == true) {
            modules = readModules()
            moduleCount = readModuleCount()
        }
    }
}

sealed class ModuleScreen {
    object List : ModuleScreen()
    data class Detail(val module: Module) : ModuleScreen()
}