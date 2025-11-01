package org.qbrp.main.engine.containers.system.request

import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.engine.containers.entity.ContainerEntry
import org.qbrp.main.engine.items.components.meta.Meta
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.service.ItemFactory
import javax.naming.OperationNotSupportedException

class ItemPrefabPrototype(private val factory: ItemFactory): ContainerEntryPrototype {
    override fun copy(item: ContainerEntry): ContainerEntry {
        if (item !is ServerItemObject) throw OperationNotSupportedException()
        val meta = item.require<Meta>()
        val itemInstance = factory.newPrefabInstance(meta.tag)
        return itemInstance
    }
}