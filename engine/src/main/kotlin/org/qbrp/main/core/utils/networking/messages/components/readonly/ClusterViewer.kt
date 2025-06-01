package org.qbrp.main.core.utils.networking.messages.components.readonly

import io.netty.util.internal.UnstableApi
import org.qbrp.main.core.utils.networking.messages.components.ClusterEntry
import org.qbrp.main.core.utils.networking.messages.components.Component
import org.qbrp.main.core.utils.networking.messages.types.ReceiveContent
import org.qbrp.main.core.utils.networking.messages.types.StringContent

open class ClusterViewer(@UnstableApi val components: List<Component> = listOf()) {

    fun isComponentExists(name: String): Boolean {
        return components.stream().anyMatch { component -> component.name == name }
    }

    fun <T> getEntry(entry: ClusterEntry<T>): T? {
        return getComponentData<T>(entry.name)
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

    fun <T> getComponentsData(name: String): List<T?>? {
        return components.filter { it.name == name }
            .map { (it.content as? ReceiveContent)?.getData() as? T }
    }

    fun getIntComponentsData(name: String): List<Int?> {
        return components.filter { it.name == name }
            .map {
                val value = (it.content as? ReceiveContent)?.getData()
                when (value) {
                    is Int -> value
                    is Double -> value.toInt() // Безопасное приведение Double в Int
                    else -> null // Если тип неизвестен, возвращаем null
                }
            }
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