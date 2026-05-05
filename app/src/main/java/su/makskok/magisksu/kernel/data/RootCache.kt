package su.makskok.magisksu.kernel.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import su.makskok.magisksu.data.log.AppLogger

object RootCache {
    /**
     * true  → su отсутствует (или полностью неработоспособен)
     * false → su присутствует и хотя бы запускается
     * null  → проверка ещё не проводилась
     */
    var noRooted: Boolean? by mutableStateOf(null)
        private set

    suspend fun check() = withContext(Dispatchers.IO) {
        try {
            val process = Runtime.getRuntime().exec(arrayOf("su", "--version"))
            val exitCode = process.waitFor()
            noRooted = exitCode != 0
            AppLogger.info("u", "I_3u6", "Проверка su завершена", "Root с помощью su доступен.")
        } catch (e: Exception) {
            noRooted = true
            AppLogger.warning("u", "W_4u9", "su не найден или недоступен", e.message ?: "")
        }
    }
}