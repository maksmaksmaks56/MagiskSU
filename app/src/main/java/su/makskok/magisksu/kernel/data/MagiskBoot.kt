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

    private fun arch() = when (Build.SUPPORTED_ABIS.firstOrNull()) {
        "arm64-v8a" -> "arm64"
        "armeabi-v7a" -> "armv7"
        "x86" -> "x86"
        "x86_64" -> "x64"
        else -> "unknown"
    }

    suspend fun extract(ctx: Context) = withContext(Dispatchers.IO) {
        if (ready) return@withContext true
        arch = arch()
        if (arch == "unknown") {
            AppLogger.fatal("r", "F_1r0", "Архитектура не определена")
            return@withContext false
        }
        try {
            val dir = ctx.filesDir

            fun rawRes(name: String) = try {
                R.raw::class.java.getField(name).getInt(null)
            } catch (e: Exception) { 0 }

            fun extract(name: String, exe: Boolean, dstName: String? = null) {
                val resId = rawRes(name) ?: throw IllegalStateException("Ресурс $name не найден")
                val file = File(dir, dstName ?: name)
                ctx.resources.openRawResource(resId).use { it.copyTo(file.outputStream()) }
                if (exe) file.setExecutable(true, true)
            }

            // magiskboot
            extract("magiskboot_$arch", exe = true, dstName = "magiskboot")
            boot = File(dir, "magiskboot").absolutePath

            // magisk
            extract("magisk_$arch", exe = true, dstName = "magisk")

            // magiskinit
            extract("magiskinit_$arch", exe = true, dstName = "magiskinit")

            // init-ld (если есть)
            if (rawRes("init_ld_$arch") != 0) extract("init_ld_$arch", exe = false, dstName = "init-ld")

            // stub.apk (общий)
            if (rawRes("stub") != 0) extract("stub", exe = false, dstName = "stub.apk")

            ready = true
            AppLogger.info("r", "I_3r7", "Готово: архитектура $arch")
            true
        } catch (e: Exception) {
            AppLogger.fatal("r", "F_1r0", "Ошибка извлечения: ${e.message}")
            false
        }
    }

    suspend fun patchBootImage(ctx: Context, image: String): String? = withContext(Dispatchers.IO) {
        if (!ready && !extract(ctx)) return@withContext null
        val dir = ctx.filesDir

        // unpack
        fun sh(cmd: String): Pair<Int, String> = runCatching {
            val p = Runtime.getRuntime().exec(arrayOf("/system/bin/sh", "-c", cmd), null, dir)
            val out = p.inputStream.bufferedReader().readText().trim()
            val err = p.errorStream.bufferedReader().readText().trim()
            p.waitFor()
            (p.exitValue() to (out + "\n" + err))   // объединяем stdout и stderr
        }.getOrElse { 1 to it.message.toString() }

        val (code1, _) = sh("$boot unpack $image")
        if (code1 != 0) { AppLogger.error("r", "E_2r-p1", "Ошибка unpack"); return@withContext null }

        // ramdisk
        val ramdisk = listOf("ramdisk.cpio", "vendor_ramdisk/init_boot.cpio", "vendor_ramdisk/ramdisk.cpio")
            .map { File(dir, it) }.firstOrNull { it.exists() } ?: File(dir, "ramdisk.cpio").also { it.createNewFile() }

        // сжатие
        listOf("magisk" to "magisk.xz", "stub.apk" to "stub.xz", "init-ld" to "init-ld.xz").forEach { (src, dst) ->
            if (File(dir, src).exists()) sh("$boot compress=xz $src $dst")
        }

        // config
        File(dir, "config").writeText("KEEPVERITY=false\nKEEPFORCEENCRYPT=false\nRECOVERYMODE=false\nVENDORBOOT=false\n")

        // cpio
        val cpioCmd = "$boot cpio ${ramdisk.absolutePath} 'add 0750 init magiskinit' 'mkdir 0750 overlay.d' 'mkdir 0750 overlay.d/sbin' 'add 0644 overlay.d/sbin/magisk.xz magisk.xz' 'add 0644 overlay.d/sbin/stub.xz stub.xz' 'add 0644 overlay.d/sbin/init-ld.xz init-ld.xz' 'patch' 'mkdir 000 .backup' 'add 000 .backup/.magisk config'"
        val (code, msg) = sh("$boot unpack $image")
        if (code != 0) {
            AppLogger.error("u", "E_2u-p4", "Ошибка unpack: $msg")
            return@withContext null
        }

        // очистка временных
        listOf("magisk.xz", "stub.xz", "init-ld.xz", "config").forEach { File(dir, it).delete() }

        // dtb
        listOf("dtb", "kernel_dtb", "extra").forEach { dt ->
            if (File(dir, dt).exists()) sh("$boot dtb $dt patch")
        }

        // kernel hexpatch
        val kernel = File(dir, "kernel")
        if (kernel.exists()) {
            var patched = false
            listOf(
                "49010054011440B93FA00F71E9000054010840B93FA00F7189000054001840B91FA00F7188010054 A1020054011440B93FA00F7140020054010840B93FA00F71E0010054001840B91FA00F7181010054",
                "821B8012 E2FF8F12",
                "70726F63615F636F6E66696700 70726F63615F6D616769736B00"
            ).forEach {
                val (c, _) = sh("$boot hexpatch kernel $it")
                if (c == 0) patched = true
            }
            if (!patched) kernel.delete()
        }

        // repack
        val out = File(dir, "new-boot.img").absolutePath
        val (code3, _) = sh("$boot repack $image $out")
        if (code3 != 0) { AppLogger.error("r", "E_2r-p1", "Ошибка repack"); return@withContext null }

        AppLogger.info("r", "I_3r7", "Патч успешен: $out")
        out
    }
}