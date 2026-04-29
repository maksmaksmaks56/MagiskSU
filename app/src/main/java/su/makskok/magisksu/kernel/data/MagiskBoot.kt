package su.makskok.magisksu.kernel.data

import android.content.Context
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import su.makskok.magisksu.R
import su.makskok.magisksu.data.AppLogger
import java.io.File

object MagiskBoot {
    private const val MAGISKBOOT_BIN = "magiskboot"   // имя файла в каталоге filesDir
    private var isExtracted = false

    /**
     * Извлекает подходящий magiskboot из raw ресурсов в рабочую директорию приложения
     * и даёт права на выполнение. Вызови один раз перед использованием.
     */
    suspend fun extract(context: Context): Boolean = withContext(Dispatchers.IO) {
        if (isExtracted) return@withContext true
        try {
            val workDir = context.filesDir
            val destFile = File(workDir, MAGISKBOOT_BIN)

            // Определяем, какой ресурс использовать для текущей архитектуры
            val resId = when (Build.SUPPORTED_ABIS.firstOrNull()) {
                "arm64-v8a"   -> R.raw.magiskboot_arm64
                "armeabi-v7a" -> R.raw.magiskboot_armv7
                "x86"         -> R.raw.magiskboot_x86
                "x86_64"      -> R.raw.magiskboot_x64
                else          -> {
                    AppLogger.fatal("r", "F_1r0", "Unsupported Architecture ${Build.SUPPORTED_ABIS.firstOrNull()}")
                    return@withContext false
                }
            }

            // Копируем из res/raw в рабочую папку
            context.resources.openRawResource(resId).use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            // Выставляем права на выполнение (rwx------)
            if (!destFile.setExecutable(true, true)) {
                AppLogger.error("r", "E_2r-p1", "Не удалось выставить права на выполнение для magiskboot")
                return@withContext false
            }

            isExtracted = true
            AppLogger.info("u", "I_3u6", "magiskboot извлечён и готов к работе (${Build.SUPPORTED_ABIS.firstOrNull()})")
            true
        } catch (e: Exception) {
            AppLogger.fatal("u", "F_1u4", "Ошибка извлечения magiskboot: ${e.message}")
            false
        }
    }

    /**
     * Запускает патч boot.img через magiskboot.
     * @param context Контекст приложения (для доступа к рабочей папке).
     * @param bootImagePath Абсолютный путь к исходному boot.img.
     * @return Путь к пропатченному образу (new-boot.img) или null при ошибке.
     */
    suspend fun patchBootImage(context: Context, bootImagePath: String): String? = withContext(Dispatchers.IO) {
        if (!isExtracted) {
            val extracted = extract(context)
            if (!extracted) return@withContext null
        }

        val workDir = context.filesDir
        val magiskBootFile = File(workDir, MAGISKBOOT_BIN)
        if (!magiskBootFile.exists() || !magiskBootFile.canExecute()) {
            AppLogger.error("u", "E_2u-p4", "magiskboot не существует или не исполняемый")
            return@withContext null
        }

        // Запускаем magiskboot напрямую, без su, так как он работает в пользовательском пространстве
        val cmd = arrayOf(
            magiskBootFile.absolutePath,
            "patch",
            bootImagePath
        )

        AppLogger.info("u", "I_3u6", "Запуск патча boot.img", cmd.joinToString(" "))
        try {
            val process = Runtime.getRuntime().exec(cmd, null, workDir)
            val out = process.inputStream.bufferedReader().readText()
            val err = process.errorStream.bufferedReader().readText()
            val exitCode = process.waitFor()

            // Логируем вывод magiskboot
            if (out.isNotBlank()) AppLogger.info("u", "I_3u6", "magiskboot stdout", out)
            if (err.isNotBlank()) AppLogger.info("u", "I_3u6", "magiskboot stderr", err)

            if (exitCode != 0) {
                AppLogger.error("u", "E_2u-p4", "Патч boot.img не удался (exit code $exitCode)", "$out\n$err")
                return@withContext null
            }

            // magiskboot при успехе создаёт new-boot.img в рабочей папке
            val patchedFile = File(workDir, "new-boot.img")
            if (!patchedFile.exists()) {
                AppLogger.error("u", "E_2u-p4", "Пропатченный файл не найден", "$out\n$err")
                return@withContext null
            }

            AppLogger.info("u", "I_3u6", "Патч boot.img завершён успешно", patchedFile.absolutePath)
            patchedFile.absolutePath
        } catch (e: Exception) {
            AppLogger.error("u", "E_2u-p4", "Ошибка выполнения magiskboot: ${e.message}")
            null
        }
    }
}