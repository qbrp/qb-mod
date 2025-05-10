package org.qbrp.core.assets

import org.qbrp.core.assets.common.Key
import java.io.File
import java.nio.file.Path

object FileSystem {
    val ASSETS = getOrCreate("qbrp/assets", true)
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

    fun getOrCreate(path: String, isDirectory: Boolean = false): File {
        return getOrCreate(File(path), isDirectory)
    }

    fun getOrCreate(file: File, isDirectory: Boolean = false): File {
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

    fun getOrCreate(path: Path, isDirectory: Boolean): File {
        return getOrCreate(path.toFile(), isDirectory)
    }
}
