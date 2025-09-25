package org.qbrp.main.engine.containers.v2.components

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.engine.containers.util.ContainerId

@Serializable
data class ContainedIn(val id: ContainerId): DataComponent