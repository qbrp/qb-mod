package org.qbrp.main.engine.assets.resourcepack

import org.qbrp.main.core.modules.ModuleAPI

interface ResourcePackAPI: ModuleAPI {
    fun scanNodes(): List<Node>
    fun createModelPackFiles(resourcePack: ResourcePackBuilder, nodes: List<Node> = scanNodes())
    fun createModelsList(nodes: List<Node>): ModelsList
}