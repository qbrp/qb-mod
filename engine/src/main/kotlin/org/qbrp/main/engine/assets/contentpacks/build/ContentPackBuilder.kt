package org.qbrp.main.engine.assets.contentpacks.build

import org.qbrp.main.engine.assets.contentpacks.PackManifest
import org.qbrp.main.engine.assets.resourcepack.Node
import org.qbrp.main.engine.assets.resourcepack.ResourcePackBuilder
import org.qbrp.main.engine.assets.resourcepack.ResourcePackAPI
import java.io.File

class ContentPackBuilder(val file: File,
                         val resourcePackAPI: ResourcePackAPI) {
    private val resourcePackFile = file.resolve("qbrp-pack")
    private var resourcePack: ResourcePackBuilder? = null

    private val manifestFile = file.resolve("manifest.json")
    private var manifest: PackManifest? = null

    private val modelsListFile = file.resolve("modellist.json")
    private var modelsList: ModelsList? = null

    private fun getFolderAfter(file: File, folderName: String): String? {
        val path = file.absoluteFile.toPath()
        for (i in 0 until path.nameCount - 1) {
            if (path.getName(i).toString() == folderName) {
                return path.getName(i + 1).toString()
            }
        }
        return null // если не найдено
    }

    private fun getFoldersAfter(file: File, folderName: String): File? {
        val path = file.absoluteFile.toPath()
        for (i in 0 until path.nameCount - 1) {
            if (path.getName(i).toString() == folderName) {
                // Собираем путь начиная со следующего элемента
                val subPath = path.subpath(i + 1, path.nameCount)
                return File(path.root?.resolve(subPath)?.toString() ?: subPath.toString())
            }
        }
        return null
    }

    fun bakeResourcePack(nodes: List<Node>, overrides: List<File>): ContentPackBuilder {
        resourcePack = ResourcePackBuilder(resourcePackFile)
        resourcePackAPI.createModelPackFiles(resourcePack!!, nodes)
        resourcePackAPI.putOverrides(overrides, resourcePack!!)
        return this
    }

    fun bakeModelsList(nodes: List<Node>, overrides: List<File>): ContentPackBuilder {
        val ids: MutableMap<String, String> = mutableMapOf()
        nodes.forEach { ids[it.modelId] = it.getModelListId() }
        overrides
            .filter { it.extension == "json" }
            .forEach {
            val folder = getFolderAfter(it, "overrides")
            val name = it.nameWithoutExtension
            ids[name] = name
        }

        modelsList = ModelsList(ids)

        modelsList!!.create(modelsListFile)
        return this
    }

    fun bakeManifest(version: String): ContentPackBuilder {
        manifest = PackManifest(version)
        manifest!!.create(manifestFile)
        return this
    }

    fun build(): ContentPack = ContentPack(file, manifest!!, modelsList!!)
}