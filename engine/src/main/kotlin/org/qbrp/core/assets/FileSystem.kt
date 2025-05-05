package org.qbrp.core.assets

import org.qbrp.core.assets.common.Key
import java.io.File

object FileSystem {
    val PREFABS = getOrCreate("qbrp/assets/prefabs", true)

    fun ensureFileExists(path: String, defaultContent: String = ""): File {
        val file = File(path)
        file.parentFile?.let { parent ->
            if (!parent.exists()) {
                parent.mkdirs()  // Рекурсивно создаёт директории
            }
        }
        if (!file.exists()) {
            file.createNewFile()
            file.writeText(defaultContent)
        }
        return file
    }

    fun getOrCreate(path: String, isDirectory: Boolean): File {
        val file = File(path)

        return if (isDirectory) {
            if (!file.exists()) {
                file.mkdirs()
            }
            file
        } else {
            file.parentFile?.takeIf { !it.exists() }?.mkdirs()
            if (!file.exists()) {
                file.createNewFile()
            }
            file
        }
    }
}
