package su.makskok.magisksu.data.utils

import android.content.Context
import android.os.Build
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import su.makskok.magisksu.data.log.AppLogger
import su.makskok.magisksu.data.settings.AppSettings
import su.makskok.magisksu.kernel.data.RootCache

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

    var package_manager: String by mutableStateOf("--")


    suspend fun load(context: Context) {
        rootAccess = checkRootAccess()
        onOffRoot = onOffCheckRoot()
        hmm = checkerRoot()
        RootCache.check()
        magisk_type = (if (Build.SUPPORTED_ABIS.firstOrNull() == "arm64-v8a") "R.raw.magiskboot_arm64_v8a" else if (Build.SUPPORTED_ABIS.firstOrNull() == "armeabi-v7a") "R.raw.magiskboot_armeabi_v7a" else if (Build.SUPPORTED_ABIS.firstOrNull() == "x86") "R.raw.magiskboot_x86" else if (Build.SUPPORTED_ABIS.firstOrNull() == "x86_64") "R.raw.magiskboot_x86_64" else "")
        version_app = appVersion(context)
        package_manager = getPackageName(context)


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
        } else {
            if (rootAccess == false || !AppSettings.autoCheckRoot) {

                magisk_type = (if (Build.SUPPORTED_ABIS.firstOrNull() == "arm64-v8a") "R.raw.magiskboot_arm64_v8a" else if (Build.SUPPORTED_ABIS.firstOrNull() == "armeabi-v7a") "R.raw.magiskboot_armeabi_v7a" else if (Build.SUPPORTED_ABIS.firstOrNull() == "x86") "magiskboot_x86" else if (Build.SUPPORTED_ABIS.firstOrNull() == "x86_64") "R.raw.magiskboot_x86_64" else "") as String
            }
            else {
                magisk_type = (if (Build.SUPPORTED_ABIS.firstOrNull() == "arm64-v8a") "R.raw.magiskboot_arm64_v8a" else if (Build.SUPPORTED_ABIS.firstOrNull() == "armeabi-v7a") "R.raw.magiskboot_armeabi_v7a" else if (Build.SUPPORTED_ABIS.firstOrNull() == "x86") "R.raw.magiskboot_x86" else if (Build.SUPPORTED_ABIS.firstOrNull() == "x86_64") "R.raw.magiskboot_x86_64" else "") as String
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