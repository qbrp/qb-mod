package org.qbrp.deprecated.resources.structure

import klite.NotFoundException
import org.qbrp.deprecated.resources.units.TextUnit
import java.io.File
import kotlin.collections.mutableMapOf

open class Structure(path: File, clear: Boolean = false): Branch(path.toPath(), clear = clear) {
    var branchesRegistry: MutableMap<Any, Branch> = mutableMapOf()
    var contentRegistry: MutableMap<Any, TextUnit> = mutableMapOf()

    fun registerBranch(branch: Branch, key: Any) { branchesRegistry[key] = branch.apply { initFile() } }

    fun registerContent(textUnit: TextUnit, key: Any) {
        if (contentRegistry.containsKey(key)) {
            logger.warn("[!] Объект ${textUnit.javaClass.simpleName} заменил ключ $key объекта ${contentRegistry[key]} на свой")
        }
    }

    fun registry(key: Any): Branch { return branchesRegistry[key] ?: throw NotFoundException("Not found key $key") }
    fun registryOrNull(key: Any): Branch? { return branchesRegistry[key] }

    fun content(key: Any): TextUnit { return contentRegistry[key] ?: throw NotFoundException("Not found key $key") }
}