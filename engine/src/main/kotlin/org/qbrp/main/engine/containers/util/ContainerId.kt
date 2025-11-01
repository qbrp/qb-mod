package org.qbrp.main.engine.containers.util

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.util.IDGenerator

@Serializable
data class ContainerId(val raw: String) : Identifiable {
    override val id: String get() = raw
    override fun equals(other: Any?): Boolean {
        return other is ContainerId && other.raw == raw
    }

    override fun hashCode(): Int {
        var result = raw.hashCode()
        result = 31 * result + id.hashCode()
        return result
    }

    companion object {
        fun random() = ContainerId(IDGenerator.nextIdStr())
    }
}

fun containerComponentName(containerId: ContainerId, type: String = "Container") = "$type#$containerId"