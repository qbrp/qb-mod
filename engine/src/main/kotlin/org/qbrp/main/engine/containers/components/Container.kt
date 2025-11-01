package org.qbrp.main.engine.containers.components

import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.engine.containers.util.ContainerId

data class Container(override val id: ContainerId): DataComponent, ContainerAnchor {
    companion object {
        fun name(alias: String) = "Container#$alias"
    }
}