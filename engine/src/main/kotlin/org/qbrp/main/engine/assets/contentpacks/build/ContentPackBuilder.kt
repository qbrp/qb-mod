package org.qbrp.main.engine.assets.contentpacks.build

import org.qbrp.main.engine.assets.contentpacks.versioning.PackManifest
import org.qbrp.main.engine.assets.resourcepack.ModelsList
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

    fun bake(version: String) {
        val nodes = resourcePackAPI.scanNodes()
        bakeResourcePack(nodes)
        bakeModelsList(nodes)
        bakeManifest(version)
    }

    fun bakeResourcePack(nodes: List<Node>): ContentPackBuilder {
        resourcePack = ResourcePackBuilder(resourcePackFile)
        resourcePackAPI.createModelPackFiles(resourcePack!!, nodes)
        return this
    }

    fun bakeModelsList(nodes: List<Node>): ContentPackBuilder {
        modelsList = resourcePackAPI.createModelsList(nodes)
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