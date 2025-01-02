package org.qbrp.core.resources.structure

import org.qbrp.core.resources.ISavable
import org.qbrp.system.utils.keys.Key
import org.qbrp.core.resources.units.ContentUnit
import java.io.File
import kotlin.collections.mutableMapOf

open class Structure(path: File, clear: Boolean = false): Branch(path.toPath(), clear = clear) {
    var branchesRegistry: MutableMap<Key, Branch> = mutableMapOf()
    var contentRegistry: MutableMap<Key, ContentUnit> = mutableMapOf()

    fun registerBranch(branch: Branch, key: Key) { branchesRegistry[key] = branch }

    fun registerContent(contentUnit: ContentUnit, key: Key) {
        if (contentRegistry.containsKey(key)) {
            logger.warn("[!] Объект ${contentUnit.javaClass.simpleName} заменил ключ $key объекта ${contentRegistry[key]} на свой")
        }
        contentRegistry[key] = contentUnit
    }

    fun registry(key: Key): Branch { return branchesRegistry[key]!! }

    fun content(key: Key): ContentUnit { return contentRegistry[key]!! }
}