package org.qbrp.system.networking.messages.components

import org.qbrp.system.networking.messages.types.SendContent

open class ClusterBuilder {
    protected val components: MutableList<Component> = mutableListOf()

    fun header(name: String, data: SendContent): ClusterBuilder {
        component(Component(name,  data, mapOf("type" to "header")))
        return this
    }

    fun component(name: String, data: SendContent, meta: Map<String, String> = emptyMap()): ClusterBuilder {
        component(Component(name,  data, meta))
        return this
    }

    fun component(component: Component): ClusterBuilder {
        components.removeIf { it.name == component.name }
        components.add(component)
        return this
    }

    fun components(components: List<Component>): ClusterBuilder {
        components.forEach { component(it) }
        return this
    }

    open fun build(): Cluster {
        return Cluster(components)
    }
}