package su.makskok.magisksu.data.utils

import com.topjohnwu.superuser.Shell

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

fun getSELinuxPermissive(): Boolean {
    val shell = Shell.Builder.create().build("sh")
    val stdoutList = ArrayList<String>()
    val result = shell.use {
        it.newJob().add("getenforce").to(stdoutList).exec()
    }
    return result.isSuccess && stdoutList.joinToString("").trim() == "Permissive"
}