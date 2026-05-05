package su.makskok.magisksu.data.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import su.makskok.magisksu.data.log.AppLogger
import java.io.IOException

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