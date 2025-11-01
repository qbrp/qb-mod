package org.qbrp.main.engine.containers.system.request

import org.qbrp.main.engine.containers.entity.ContainerEntry

interface ContainerEntryPrototype {
    fun copy(item: ContainerEntry): ContainerEntry
}