package su.makskok.magisksu.data

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build


fun Context.appVersion(): String {
    val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0L))
    } else {
        @Suppress("DEPRECATION")
        packageManager.getPackageInfo(packageName, 0)
    }
    return "v${info.versionName}"
}