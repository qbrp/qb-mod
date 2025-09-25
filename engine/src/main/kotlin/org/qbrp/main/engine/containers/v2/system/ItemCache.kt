package org.qbrp.main.engine.containers.v2.system

import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.components.ContainedIn
import org.qbrp.main.engine.containers.v2.entity.ContainerEntry
import org.qbrp.main.engine.items.model.ServerItemObject
import org.qbrp.main.engine.items.service.ItemStorage

class ItemCache(
    private val itemStorage: ItemStorage<ServerItemObject>
) : ContainerCache {
    private val containerItems: MutableMap<ContainerId, MutableSet<ContainerEntry>> = mutableMapOf()
    private val reverse: MutableMap<ContainerEntry, ContainerId> = mutableMapOf()

    override val itemToContainerMap: Map<ContainerEntry, ContainerId>
        get() = reverse

    override fun add(container: ContainerId, entry: ContainerEntry) {
        containerItems.getOrPut(container) { LinkedHashSet() }.add(entry)
        reverse[entry] = container
    }

    override fun remove(container: ContainerId, entry: ContainerEntry): Boolean {
        val removed = containerItems[container]?.remove(entry) == true
        if (removed && reverse[entry] == container) {
            reverse.remove(entry)
        }
        return removed
    }

    override fun invalidate(container: ContainerId) {
        val fresh: MutableSet<ContainerEntry> =
            itemStorage.asSequence()
                .filter { it.get<ContainedIn>()?.id == container }
                .map { it as ContainerEntry }   // если совместимые типы
                .toCollection(LinkedHashSet())

        containerItems[container]?.forEach { e ->
            if (reverse[e] == container) reverse.remove(e)
        }

        containerItems[container] = fresh
        fresh.forEach { e -> reverse[e] = container }
    }

    override fun get(containerId: ContainerId): List<ContainerEntry> {
        return containerItems[containerId]?.toList() ?: emptyList()
    }
}