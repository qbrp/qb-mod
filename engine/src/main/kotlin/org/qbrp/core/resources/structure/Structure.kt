package org.qbrp.core.resources.structure

import klite.NotFoundException
import org.qbrp.system.utils.keys.Key
import org.qbrp.core.resources.units.ContentUnit
import java.io.File
import kotlin.collections.mutableMapOf

open class Structure(path: File, clear: Boolean = false): Branch(path.toPath(), clear = clear) {
    var branchesRegistry: MutableMap<Key, Branch> = mutableMapOf()
    var contentRegistry: MutableMap<Key, ContentUnit> = mutableMapOf()

    fun registerBranch(branch: Branch, key: Key) { branchesRegistry[key] = branch.apply { initFile() } }

    fun registerContent(contentUnit: ContentUnit, key: Key) {
        if (contentRegistry.containsKey(key)) {
            logger.warn("[!] Объект ${contentUnit.javaClass.simpleName} заменил ключ $key объекта ${contentRegistry[key]} на свой")
        }
        contentRegistry[key] = contentUnit.apply { initFile() }
    }

    fun registry(key: Key): Branch { return branchesRegistry[key] ?: throw NotFoundException("Not found key $key") }
    fun registryOrNull(key: Key): Branch? { return branchesRegistry[key] }

    fun content(key: Key): ContentUnit { return contentRegistry[key] ?: throw NotFoundException("Not found key $key") }
}