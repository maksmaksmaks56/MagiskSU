package su.makskok.magisksu.data.log

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import java.text.SimpleDateFormat
import java.util.*

enum class LogLevel(val label: String) {
    INFO("Info"),
    WARNING("Warning"),
    ERROR("Error"),
    FATAL("Fatal")
}

data class LogEntry(
    val timestamp: Long,
    val level: LogLevel,
    val source: String,
    val code: String,
    val message: String,
    val detail: String = ""
) {
    val formattedTime: String get() {
        val sdf = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    val fullLine: String get() = "$formattedTime:[${level.label}]:$source:$code:$message${if (detail.isNotBlank()) " | $detail" else ""}"
}

object AppLogger {
    val logs: SnapshotStateList<LogEntry> = mutableStateListOf()

    fun add(level: LogLevel, source: String, code: String, message: String, detail: String = "") {
        logs.add(LogEntry(
            timestamp = System.currentTimeMillis(),
            level = level,
            source = source,
            code = code,
            message = message,
            detail = detail
        ))
    }

    fun info(source: String, code: String, message: String, detail: String = "") =
        add(LogLevel.INFO, source, code, message, detail)

    fun warning(source: String, code: String, message: String, detail: String = "") =
        add(LogLevel.WARNING, source, code, message, detail)

    fun error(source: String, code: String, message: String, detail: String = "") =
        add(LogLevel.ERROR, source, code, message, detail)

    fun fatal(source: String, code: String, message: String, detail: String = "") {
        add(LogLevel.FATAL, source, code, message, detail)
    }

    fun clear() {
        logs.clear()
    }
}