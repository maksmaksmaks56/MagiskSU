package su.makskok.magisksu.data.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext



suspend fun readModuleCount(): Int = withContext(Dispatchers.IO) {
    val raw = runSuCommand("ls -1F /data/adb/modules").trim()
    if (raw.isBlank() || raw.startsWith("Ошибка")) 0
    else raw.lines().count { it.trim().endsWith("/") }
}

suspend fun readModules(): List<Module> = withContext(Dispatchers.IO) {
    val script = """
        for d in /data/adb/modules/*/; do
          id=${'$'}(basename "${'$'}d")
          echo "---MODULE_START---"
          echo "id=${'$'}{id}"
          cat "${'$'}{d}module.prop"
          if [ -f "${'$'}{d}action.sh" ]; then echo "hasAction=yes"; else echo "hasAction=no"; fi
          echo "---MODULE_END---"
        done
    """.trimIndent()

    val raw = runSuCommand(script).trim()
    if (raw.isBlank() || raw.startsWith("Ошибка") || raw.startsWith("E_") || raw.startsWith("F_")) {
        return@withContext emptyList()
    }

    val modules = mutableListOf<Module>()
    val entries = raw.split("---MODULE_START---").drop(1)
    for (entry in entries) {
        val lines = entry.lines()
        val endIdx = lines.indexOf("---MODULE_END---")
        if (endIdx == -1) continue
        val relevantLines = lines.subList(0, endIdx)
        var id = ""
        val props = mutableMapOf<String, String>()
        var hasAction = false

        for (line in relevantLines) {
            when {
                line.startsWith("id=") -> id = line.substringAfter("id=")
                line == "hasAction=yes" -> hasAction = true
                line == "hasAction=no" -> hasAction = false
                '=' in line -> {
                    val idx = line.indexOf('=')
                    props[line.substring(0, idx).trim()] = line.substring(idx + 1).trim()
                }
            }
        }

        if (id.isNotEmpty()) {
            modules.add(
                Module(
                    id          = id,
                    name        = props["name"] ?: id,
                    version     = props["version"] ?: "?",
                    author      = props["author"] ?: "unknown",
                    description = props["description"] ?: "",
                    hasAction   = hasAction
                )
            )
        }
    }
    modules
}