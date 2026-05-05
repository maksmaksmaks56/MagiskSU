package su.makskok.magisksu.data.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import su.makskok.magisksu.data.log.AppLogger
import su.makskok.magisksu.data.settings.AppSettings

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

suspend fun checkerRoot(): String = withContext(Dispatchers.IO) {
    val hmm: Boolean = checkRootAccess()
    if (checkRootAccess() == false) {
        AppLogger.warning("u", "W_4u9", "Root права не найдены.")
    }

    return@withContext hmm.toString()
}