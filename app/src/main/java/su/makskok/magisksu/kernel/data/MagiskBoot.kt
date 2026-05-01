package su.makskok.magisksu.kernel.data

import android.content.Context
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import su.makskok.magisksu.R
import su.makskok.magisksu.data.AppLogger
import java.io.File

object MagiskBoot {
    private var ready = false
    private lateinit var boot: String
    private lateinit var arch: String

    private fun archName() = when (Build.SUPPORTED_ABIS.firstOrNull()) {
        "arm64-v8a" -> "arm64"
        "armeabi-v7a" -> "armv7"
        "x86" -> "x86"
        "x86_64" -> "x64"
        else -> "unknown"
    }

    suspend fun extract(ctx: Context) = withContext(Dispatchers.IO) {
        if (ready) return@withContext true
        arch = archName()
        if (arch == "unknown") {
            AppLogger.fatal("u", "F_1u3", "Архитектура не определена")
            return@withContext false
        }
        try {
            val dir = ctx.filesDir

            fun rawRes(name: String) = try {
                R.raw::class.java.getField(name).getInt(null)
            } catch (e: Exception) { 0 }

            fun extractFile(name: String, exe: Boolean, dstName: String? = null) {
                val resId = rawRes(name)
                require(resId != 0) { "Ресурс $name не найден" }
                val file = File(dir, dstName ?: name)
                if (file.exists()) file.delete()
                ctx.resources.openRawResource(resId).use { it.copyTo(file.outputStream()) }
                if (exe) {
                    Runtime.getRuntime().exec(arrayOf("/system/bin/sh", "-c", "chmod 755 ${file.absolutePath}")).waitFor()
                    file.setExecutable(true, true)
                }
            }

            // magiskboot
            extractFile("magiskboot_$arch", exe = true, dstName = "magiskboot")
            val bootFile = File(dir, "magiskboot")
            if (!bootFile.canExecute()) {
                AppLogger.error("u", "E_2u-p4", "Не удалось сделать magiskboot исполняемым")
                return@withContext false
            }
            boot = bootFile.absolutePath

            // magisk
            if (rawRes("magisk_$arch") != 0) extractFile("magisk_$arch", exe = true, dstName = "magisk")

            // magiskinit
            if (rawRes("magiskinit_$arch") != 0) extractFile("magiskinit_$arch", exe = true, dstName = "magiskinit")

            // init-ld
            if (rawRes("init_ld_$arch") != 0) extractFile("init_ld_$arch", exe = false, dstName = "init-ld")

            // stub.apk
            if (rawRes("stub") != 0) extractFile("stub", exe = false, dstName = "stub.apk")

            ready = true
            AppLogger.info("u", "I_3u6", "Компоненты извлечены ($arch)")
            true
        } catch (e: Exception) {
            AppLogger.fatal("u", "F_1u3", "Ошибка извлечения: ${e.message}")
            false
        }
    }

    suspend fun patchBootImage(ctx: Context, image: String): String? = withContext(Dispatchers.IO) {
        if (!ready && !extract(ctx)) return@withContext null
        val dir = ctx.filesDir

        val hasSu = runCatching { Runtime.getRuntime().exec(arrayOf("su", "--version")).waitFor() == 0 }.getOrDefault(false)

        // Функция выполнения команды с корректным экранированием для su -c
        fun sh(cmd: String): Pair<Int, String> {
            val finalCmd = if (hasSu) {
                // Экранируем двойные кавычки внутри cmd, чтобы su -c не сломался
                val escaped = cmd.replace("\"", "\\\"")
                "su -c \"$escaped\""
            } else {
                cmd
            }
            return runCatching {
                val p = Runtime.getRuntime().exec(arrayOf("/system/bin/sh", "-c", finalCmd), null, dir)
                val out = p.inputStream.bufferedReader().readText().trim()
                val err = p.errorStream.bufferedReader().readText().trim()
                p.waitFor()
                val full = if (err.isNotEmpty()) "$out\n$err" else out
                p.exitValue() to full
            }.getOrElse { 1 to it.message.toString() }
        }

        if (!hasSu) {
            AppLogger.warning("u", "W_4u9", "Нет root-прав – патч скорее всего не удастся из-за защиты W^X")
        }

        // 1. Unpack
        val (code1, msg1) = sh("$boot unpack $image")
        if (code1 != 0) {
            AppLogger.error("u", "E_2u-p4", "Ошибка unpack: $msg1")
            if (!hasSu) AppLogger.error("u", "E_2u-p4", "Без root запустить magiskboot невозможно")
            return@withContext null
        }

        // 2. Поиск ramdisk
        val ramdisk = listOf("ramdisk.cpio", "vendor_ramdisk/init_boot.cpio", "vendor_ramdisk/ramdisk.cpio")
            .map { File(dir, it) }.firstOrNull { it.exists() }
            ?: File(dir, "ramdisk.cpio").also { it.createNewFile() }

        // 3. Сжатие
        listOf("magisk" to "magisk.xz", "stub.apk" to "stub.xz", "init-ld" to "init-ld.xz").forEach { (src, dst) ->
            if (File(dir, src).exists()) sh("$boot compress=xz $src $dst")
        }

        // 4. Config
        File(dir, "config").writeText("KEEPVERITY=false\nKEEPFORCEENCRYPT=false\nRECOVERYMODE=false\nVENDORBOOT=false\n")

        // 5. Cpio – каждая команда обёрнута в двойные кавычки
        val cpioCommands = listOf(
            "add 0750 init magiskinit",
            "mkdir 0750 overlay.d",
            "mkdir 0750 overlay.d/sbin",
            "add 0644 overlay.d/sbin/magisk.xz magisk.xz",
            "add 0644 overlay.d/sbin/stub.xz stub.xz",
            "add 0644 overlay.d/sbin/init-ld.xz init-ld.xz",
            "patch",
            "mkdir 000 .backup",
            "add 000 .backup/.magisk config"
        ).joinToString(" ") { "\"$it\"" }   // двойные кавычки, экранируются в sh

        val (code2, msg2) = sh("$boot cpio ${ramdisk.absolutePath} $cpioCommands")
        if (code2 != 0) {
            AppLogger.error("u", "E_2u-p4", "Ошибка cpio: $msg2")
            return@withContext null
        }

        // Удаляем временные
        listOf("magisk.xz", "stub.xz", "init-ld.xz", "config").forEach { File(dir, it).delete() }

        // 6. DTB
        listOf("dtb", "kernel_dtb", "extra").forEach { dt ->
            if (File(dir, dt).exists()) sh("$boot dtb $dt patch")
        }

        // 7. Kernel hexpatch
        val kernelFile = File(dir, "kernel")
        if (kernelFile.exists()) {
            var patched = false
            val patches = listOf(
                "49010054011440B93FA00F71E9000054010840B93FA00F7189000054001840B91FA00F7188010054" to
                        "A1020054011440B93FA00F7140020054010840B93FA00F71E0010054001840B91FA00F7181010054",
                "821B8012" to "E2FF8F12",
                "70726F63615F636F6E66696700" to "70726F63615F6D616769736B00"
            )
            for ((from, to) in patches) {
                val (c, _) = sh("$boot hexpatch kernel $from $to")
                if (c == 0) patched = true
            }
            if (!patched) kernelFile.delete()
        }

        // 8. Repack
        val outputPath = File(dir, "new-boot.img").absolutePath
        val (code3, msg3) = sh("$boot repack $image $outputPath")
        if (code3 != 0) {
            AppLogger.error("u", "E_2u-p4", "Ошибка repack: $msg3")
            return@withContext null
        }

        AppLogger.info("u", "I_3u6", "Патч успешен: $outputPath")
        outputPath
    }
}