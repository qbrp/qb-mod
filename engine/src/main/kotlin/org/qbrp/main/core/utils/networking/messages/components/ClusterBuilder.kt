package org.qbrp.main.core.utils.networking.messages.components

import org.qbrp.main.core.utils.networking.messages.types.BooleanContent
import org.qbrp.main.core.utils.networking.messages.types.Content
import org.qbrp.main.core.utils.networking.messages.types.IntContent
import org.qbrp.main.core.utils.networking.messages.types.SendContent
import org.qbrp.main.core.utils.networking.messages.types.StringContent

open class ClusterBuilder {
    protected val components: MutableList<Component> = mutableListOf()
    protected var override: Boolean = true

    fun header(name: String, data: SendContent): ClusterBuilder {
        component(Component(name,  data, mapOf("type" to "header")))
        return this
    }

    open fun override(state: Boolean): ClusterBuilder { this.override = state; return this }

    fun component(name: String, stringData: String): ClusterBuilder {
        return component(name, StringContent(stringData))
    }

    fun component(name: String, booleanData: Boolean): ClusterBuilder {
        return component(name, BooleanContent(booleanData))
    }

    fun component(name: String, intData: Int): ClusterBuilder {
        return component(name, IntContent(intData))
    }

    fun component(entry: ClusterEntry<*>, value: String): ClusterBuilder {
        return component(entry.name, value)
    }

    fun component(entry: ClusterEntry<*>, value: Boolean): ClusterBuilder {
        return component(entry.name, value)
    }

    fun component(entry: ClusterEntry<*>, value: Int): ClusterBuilder {
        return component(entry.name, value)
    }

    fun component(entry: ClusterEntry<*>, content: SendContent): ClusterBuilder {
        return component(entry.name, content)
    }

    fun component(name: String, data: SendContent, meta: Map<String, String> = emptyMap()): ClusterBuilder {
        return component(Component(name,  data, meta))
    }

    fun component(component: Component): ClusterBuilder {
        if (override) components.removeIf { it.name == component.name }
        components.add(component)
        return this
    }

    fun components(components: List<Component>): ClusterBuilder {
        components.forEach { component(it) }
        return this
    }

    open fun copy(): ClusterBuilder {
        val copiedBuilder = ClusterBuilder()
        copiedBuilder.components.addAll(this.components.map { it.copy() })
        return copiedBuilder
    }

    open fun build(): Cluster {
        return Cluster(components)
    }

    companion object {
        fun concat(cluster1: ClusterBuilder, cluster2: ClusterBuilder): ClusterBuilder {
            val newBuilder = ClusterBuilder()
            newBuilder.components.addAll(cluster1.components.map { it.copy() })
            newBuilder.components.addAll(cluster2.components.map { it.copy() })
            return newBuilder
        }
    }
}