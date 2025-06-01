package org.qbrp.main.core.utils.networking.messages.components

import net.minecraft.text.Text
import org.qbrp.main.engine.chat.core.messages.ChatMessageTagsCluster
import org.qbrp.main.core.utils.networking.messages.types.BooleanContent
import org.qbrp.main.core.utils.networking.messages.types.ClusterListContent
import org.qbrp.main.core.utils.networking.messages.types.Content
import org.qbrp.main.core.utils.networking.messages.types.DoubleContent
import org.qbrp.main.core.utils.networking.messages.types.IntContent
import org.qbrp.main.core.utils.networking.messages.types.LongContent
import org.qbrp.main.core.utils.networking.messages.types.StringContent
import org.qbrp.main.core.utils.networking.messages.types.TextContent
import kotlin.reflect.KClass

class ComponentTypeFactory {
    private val componentsTypeMap: MutableMap<String, () -> Content> = mutableMapOf()
    private val componentTypeNameMap: MutableMap<KClass<out Content>, String> = mutableMapOf()
    // Две коллекции ради так называемой производительности

    init {
        registerComponentType("string", ::StringContent)
        registerComponentType("text", ::TextContent)
        registerComponentType("boolean", ::BooleanContent)
        registerComponentType("int", ::IntContent)
        registerComponentType("integer", ::IntContent)
        registerComponentType("long", ::LongContent)
        registerComponentType("double", ::DoubleContent)
        registerComponentType("cluster", ::Cluster)
        registerComponentType("clusterList", ::ClusterListContent)
        registerComponentType("chatMessageTagsCluster", ::ChatMessageTagsCluster)
    }

    private fun registerComponentType(name: String, constructor: () -> Content) {
        componentsTypeMap[name] = constructor
        componentTypeNameMap[constructor()::class] = name // Сопоставляем тип с ID
    }

    fun buildComponentType(clazz: Class<*>): Content {
        val constructor = componentsTypeMap[clazz.simpleName.replaceFirstChar { it.lowercase() }] ?: throw IllegalArgumentException("Компонент '${clazz.simpleName}' не найден")
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
