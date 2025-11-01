package org.qbrp.main.engine.containers.event

import org.qbrp.main.core.events.Event
import org.qbrp.main.engine.containers.entity.ContainerEntry
import org.qbrp.main.engine.containers.util.ContainerId

data class ItemRemovedFromContainerEvent(val item: ContainerEntry, val containerId: ContainerId): Event