package su.makskok.magisksu.data.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

fun getPackageName(context: Context): String {
    return context.packageName
}

fun appVersion(context: Context): String {
    val pkg = context.packageName
    val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.packageManager.getPackageInfo(pkg, PackageManager.PackageInfoFlags.of(0L))
    } else {
        @Suppress("DEPRECATION")
        context.packageManager.getPackageInfo(pkg, 0)
    }
    val suffix = if (pkg.endsWith(".debug")) "-debug" else ""
    return "v${info.versionName}$suffix"
}