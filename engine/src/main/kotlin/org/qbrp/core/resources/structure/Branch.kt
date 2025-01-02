package org.qbrp.core.resources.structure

import com.google.gson.Gson
import org.qbrp.core.resources.ISavable
import org.qbrp.core.resources.ServerResources
import org.qbrp.core.resources.units.ContentUnit
import org.qbrp.core.resources.units.ResourceUnit
import org.qbrp.core.resources.data.Data
import org.qbrp.core.resources.parsing.Parser
import org.qbrp.system.utils.format.getExtension
import org.qbrp.system.utils.format.removeExtensions
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.io.path.name
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.primaryConstructor

open class Branch(
    path: Path,
    val name: String = path.name,
    val clear: Boolean = false
) : ResourceUnit(path) {
    val logger = ServerResources.getLogger()
    val children: MutableList<ResourceUnit> = mutableListOf()

    init {
        if (clear) {
            try {
                if (Files.exists(path)) {
                    Files.walk(path)
                        .sorted(Comparator.reverseOrder())
                        .forEach(Files::delete)
                }
                Files.createDirectories(path)
            } catch (e: Exception) {
                throw IllegalStateException("Не удалось очистить директорию: $path", e)
            }
        }
    }

    override fun handle(): Branch {
        try {
            if (Files.notExists(path)) {
                Files.createDirectories(path) // Создаёт директории, если их нет
            }
        } catch (e: Exception) {
            throw IllegalStateException("Не удалось создать директорию: $path", e)
        }
        return this
    }

    fun addContainer(): UnitContainer {
        return add(UnitContainer(path)) as UnitContainer
    }

    fun addBranch(name: String): Branch {
        return add(Branch(path.resolve(name), name = name)) as Branch
    }

    fun parse(parser: Parser): Branch {
        parser.parse(path.toFile()).forEach { add(it) }
        return this
    }

    fun addUnit(data: Data, name: String, extension: String): ContentUnit {
        val clazz = data.unit.kotlin
        val constructor = clazz.primaryConstructor ?: throw IllegalArgumentException("Конструктор не найден")
        return add(constructor.call(path, name, extension, data) as ResourceUnit) as ContentUnit
    }

    fun addStructure(name: String): Structure {
        return add(Structure(path.resolve(name).toFile())) as Structure
    }

    fun addStructure(structure: Structure): Structure {
        return add(structure) as Structure
    }

    fun open(filename: String, clazz: Class<*>, gson: Gson = Gson()): ContentUnit {
        val path = path.resolve(filename)
        if (!Files.exists(path)) { addUnit(clazz.getConstructor().newInstance() as Data, filename.removeExtensions(), filename.getExtension()) }
        val file = path.toFile()
        val fileContent = file.takeIf { it.exists() }?.readText() ?: ""

        val data = try {
            if (Data::class.java.isAssignableFrom(clazz)) {
                clazz.kotlin.companionObjectInstance?.let { companion ->
                    companion::class.java.getDeclaredMethod("fromFile", File::class.java).apply { isAccessible = true }
                        .invoke(companion, file) as Data
                } ?: gson.fromJson(fileContent, clazz) as Data // Если компаньона нет, используем Gson
            } else {
                gson.fromJson(fileContent, clazz) as Data
            }
        } catch (e: Exception) {
            throw IllegalStateException("Error deserializing file '$filename': ${e.message}", e)
        }

        return addUnit(data, filename.removeExtensions(), filename.getExtension())
    }

    fun add(unit: ResourceUnit, log: Boolean = true): ResourceUnit {
        if (log) { logger.log("<<[+]>> ${unit.path} <<(${unit.javaClass.simpleName})>>") }
        return unit.handle().also { children.add(it)
        }
    }

    fun zip(outputZip: File, sourceFolder: File = path.toFile(), createContainer: Boolean = false, containerName: String? = null) {
        require(sourceFolder.exists() && sourceFolder.isDirectory) { "Исходная папка должна существовать: ${sourceFolder.absolutePath}" }
        outputZip.parentFile?.mkdirs() ?: error("Не удалось создать директорию для архива.")

        ZipOutputStream(outputZip.outputStream()).use { zipOut ->
            val basePath = if (createContainer) (containerName ?: sourceFolder.name) + "/" else ""
            sourceFolder.walkTopDown().forEach { file ->
                val entryPath = basePath + file.relativeTo(sourceFolder).path + if (file.isDirectory) "/" else ""
                zipOut.putNextEntry(ZipEntry(entryPath))
                if (file.isFile) file.inputStream().copyTo(zipOut)
                zipOut.closeEntry()
            }
        }
        println("Архив создан: ${outputZip.absolutePath}")
    }

    // Не рекомендую использовать
    fun pasteNonStructured(sourcePath: Path) {
        if (!Files.exists(sourcePath)) {
            logger.error("[!] Путь не найден: $sourcePath")
            return
        }
        Files.walk(sourcePath).forEach { currentPath ->
            val targetPath = path.resolve(sourcePath.relativize(currentPath))
            if (Files.isDirectory(currentPath)) {
                Files.createDirectories(targetPath)
            } else {
                Files.copy(currentPath, targetPath)
            }
        }
    }

    fun save(): Boolean {
        try {
            children.forEach { child ->
                when (child) {
                    is ISavable -> child.save() // Сохраняем объект, реализующий ISavable
                    is Branch -> if (child !is Structure) child.save() // Если это не Structure, вызываем save
                }
            }
            return true
        } catch (e: Exception) {
            logger.error("Возникла ошибка при сборке пакета ресурсов: ${e.stackTrace}")
            return false
        }
    }

}