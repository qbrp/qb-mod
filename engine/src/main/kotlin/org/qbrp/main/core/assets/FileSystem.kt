package org.qbrp.main.core.assets

import org.qbrp.main.core.modules.ModuleFileSystemAPI
import org.qbrp.main.core.modules.QbModule
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object FileSystem: ModuleFileSystemAPI {
    val ASSETS = getOrCreate("qbrp/assets", true)
    val PREFABS = getOrCreate("qbrp/assets/prefabs", true)
    val MODULES = getOrCreate("qbrp/modules", true)

    val MINECRAFT_RESOURCEPACKS =
        if (System.getenv("DEVELOPMENT")?.lowercase() != "true") getOrCreate("resourcepacks", true)
        else getOrCreate("client/resourcepacks")

    val HTTP_SERVER_ASSETS = getOrCreate("qbrp/server", true)

    val CLIENT_SERVER_PACKS = getOrCreate("qbrp-client/serverpacks", true)

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

    fun zipDirectoryTo(outputZip: File, baseDir: Path) {
        ZipOutputStream(FileOutputStream(outputZip)).use { zos ->
            baseDir.toFile().walkTopDown().filter { it.isFile }.forEach { file ->
                val relPath = baseDir.relativize(file.toPath()).toString().replace(File.separatorChar, '/')
                zos.putNextEntry(ZipEntry(relPath))
                file.inputStream().use { it.copyTo(zos) }
                zos.closeEntry()
            }
        }
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

    override fun createModuleFile(name: String) {
        getOrCreate(MODULES.resolve(name), true)
    }

    override fun getModuleFile(name: String): File {
        return MODULES.resolve(name)
    }

    override fun getModuleFile(module: QbModule): File {
        return getModuleFile(module.getName())
    }
}
