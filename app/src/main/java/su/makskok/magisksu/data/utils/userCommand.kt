package su.makskok.magisksu.data.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import su.makskok.magisksu.data.log.AppLogger
import java.io.IOException

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