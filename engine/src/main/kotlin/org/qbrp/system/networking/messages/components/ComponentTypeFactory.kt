package org.qbrp.system.networking.messages.components

import org.qbrp.engine.chat.messages.ChatMessageTagsCluster
import org.qbrp.system.networking.messages.types.BooleanContent
import org.qbrp.system.networking.messages.types.Content
import org.qbrp.system.networking.messages.types.IntContent
import org.qbrp.system.networking.messages.types.StringContent
import kotlin.reflect.KClass

class ComponentTypeFactory {
    private val componentsTypeMap: MutableMap<String, () -> Content> = mutableMapOf()
    private val componentTypeNameMap: MutableMap<KClass<out Content>, String> = mutableMapOf()
    // Две коллекции ради так называемой производительности

    init {
        registerComponentType("string", ::StringContent)
        registerComponentType("boolean", ::BooleanContent)
        registerComponentType("int", ::IntContent)
        registerComponentType("cluster", ::Cluster)
        registerComponentType("chatMessageTagsCluster", ::ChatMessageTagsCluster)
    }

    private fun registerComponentType(name: String, constructor: () -> Content) {
        componentsTypeMap[name] = constructor
        componentTypeNameMap[constructor()::class] = name // Сопоставляем тип с ID
    }

    fun buildComponentType(clazz: Class<*>): Content {
        val constructor = componentsTypeMap[clazz.simpleName] ?: throw IllegalArgumentException("Компонент '${clazz.simpleName}' не найден")
        return constructor()
    }

    fun buildComponentType(name: String): Content {
        val constructor = componentsTypeMap[name] ?: throw IllegalArgumentException("Компонент '$name' не найден")
        return constructor()
    }

    fun getComponentId(component: Content): String {
        return componentTypeNameMap[component::class]
            ?: throw IllegalArgumentException("Компонент '${component::class}' не зарегистрирован")
    }
}
