package org.qbrp.main.engine.containers.v2.event

import org.qbrp.main.core.events.Event
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.containers.v2.entity.ContainerEntry

data class ItemAddedInContainerEvent(val entry: ContainerEntry, val containerId: ContainerId): Event