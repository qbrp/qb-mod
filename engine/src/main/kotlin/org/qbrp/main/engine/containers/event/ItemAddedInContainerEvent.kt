package org.qbrp.main.engine.containers.event

import org.qbrp.main.core.events.Event
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.entity.ContainerEntry

data class ItemAddedInContainerEvent(val entry: ContainerEntry, val containerId: ContainerId): Event