package org.qbrp.main.engine.assets.resourcepack

import net.minecraft.resource.ResourcePack
import org.qbrp.main.core.modules.ModuleAPI
import java.io.File

interface ResourcePackAPI: ModuleAPI {
    fun scanNodes(): List<Node>
    fun scanOverrides(): List<File>
    fun putOverrides(overrides: List<File>, resourcePack: ResourcePackBuilder)
    fun createModelPackFiles(resourcePack: ResourcePackBuilder, nodes: List<Node> = scanNodes())
}