package org.qbrp.system.networking.messages.components.readonly

import org.qbrp.system.networking.messages.components.Component
import org.qbrp.system.networking.messages.types.ReceiveContent
import org.qbrp.system.networking.messages.types.StringContent

class ClusterViewer(private val components: List<Component> = listOf()) {

    fun isComponentExists(name: String): Boolean {
        return components.stream().anyMatch { component -> component.name == name }
    }

    fun <T> getHeader(index: Int = 0): T? {
        return components
            .filter { it.meta == mapOf("type" to "header") }
            .elementAtOrNull(index)
            ?.let { (it.content as? ReceiveContent)?.getData() as? T }
    }

    fun <T> getComponentData(name: String): T? {
        return components.find { it.name == name }
        ?.let { (it.content as? ReceiveContent)?.getData() as? T }
    }

    fun getValueComponents(name: String): Map<String, String> =
        components.filter { it.name.startsWith("$name.") }
            .associate { it.name.substringAfter('.') to (it.content as? StringContent)?.getData()!!  }

    fun <T> getCluster(name: String): T? {
        return components.find { it.name == name }
            ?.let { (it.content as? T) }
    }

    fun getComponentMeta(name: String): Map<String, String>? {
        return components.find { it.name == name }?.meta
    }

    fun toList(): List<Component> {
        return components.toList()
    }

    fun List<Component>.removeComponents(vararg names: String): List<Component> {
        val mutableComponents = this.toMutableList()
        mutableComponents.removeAll { component -> names.contains(component.name) }
        return mutableComponents
    }

}