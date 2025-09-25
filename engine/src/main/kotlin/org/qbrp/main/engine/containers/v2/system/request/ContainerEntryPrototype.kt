package org.qbrp.main.engine.containers.v2.system.request

import org.qbrp.main.engine.containers.v2.entity.ContainerEntry

interface ContainerEntryPrototype {
    fun copy(item: ContainerEntry): ContainerEntry
}