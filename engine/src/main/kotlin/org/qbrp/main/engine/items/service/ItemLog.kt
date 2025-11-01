package org.qbrp.main.engine.items.service

import org.qbrp.main.core.assets.prefabs.Prefab
import org.qbrp.main.core.components.Component
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.util.log.Logger
import org.qbrp.main.core.util.log.LoggerUtil
import org.qbrp.main.engine.items.components.meta.Meta
import org.qbrp.main.engine.items.model.ItemObject
import org.qbrp.main.engine.items.model.ItemStateImpl
import org.qbrp.main.engine.items.model.ServerItemObject
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties

data class Node(val name: String, val properties: List<Property>)
sealed class Property(val name: String, open val value: Any?) {
    class Val(name: String, value: Any?): Property(name, value)
    class Node(name: String, override val value: org.qbrp.main.engine.items.service.Node?): Property(name, value)
}

@Suppress("UNCHECKED_CAST")
fun printItemComponents(item: ItemObject, logger: Logger) {
    for ((name, component) in item.state.namedIterator()) {
        val component = component as? Component<ComponentManager> ?: continue
        val componentClass = component::class
        fun toString(spaces: Int, properties: List<Property>): String {
            return properties.joinToString(",\n") {
                val base = when(it) {
                    is Property.Node -> {
                        if (it.value == null) {
                            "null"
                        } else {
                            val propertiesEmpty = it.value.properties.isEmpty()
                            val n = if(!propertiesEmpty) "\n" else ""
                            val closeBracket = if (!propertiesEmpty) " ".repeat((spaces - 2).coerceAtLeast(0)) else ""
                            "${it.name}=${it.value.name}($n${toString(spaces + 2, it.value.properties)}$n$closeBracket)"
                        }
                    }
                    is Property.Val -> "${it.name}=${it.value}"
                }
                "${" ".repeat(spaces)}$base"
            }
        }
        val componentProperties = toString(0, getProperties(component, componentClass))

        logger.log("${name.full} : [\n$componentProperties]")
    }
}

private fun isLeaf(v: Any): Boolean =
    v is String || v is Number || v is Boolean || v is Char || v.javaClass.isEnum

// Основная функция
internal fun getProperties(
    entity: Any,
    kclass: KClass<out Any> = entity::class,
    maxDepth: Int = 5,                                // лимит глубины
    visited: MutableSet<Int> = mutableSetOf(),        // identity set
    collectionLimit: Int = 10                         // сколько элементов коллекций раскрывать
): List<Property> {

    // отсечём циклы по identity
    val id = System.identityHashCode(entity)
    if (!visited.add(id)) {
        return listOf(Property.Val("<cycle>", "↩ already visited"))
    }

    // предохраняемся от слишком глубокой вложенности
    if (maxDepth <= 0) {
        return listOf(Property.Val("<depth>", "…max depth reached"))
    }

    val list = mutableListOf<Property>()
    val errors = mutableListOf<Exception>() // если нужно — можешь вернуть/логировать

    for (property in kclass.memberProperties) {
        try {
            val name = property.name
            val value = runCatching { property.getter.call(entity) }.getOrNull()

            if (value == null || isLeaf(value)) {
                list.add(Property.Val(name, value))
                continue
            }

            // Коллекции/масивы/мапы
            when (value) {
                is Iterable<*> -> {
                    val items = value.take(collectionLimit).mapIndexed { idx, elem ->
                        if (elem == null || isLeaf(elem)) {
                            Property.Val("[$idx]", elem)
                        } else {
                            val sub = getProperties(
                                elem,
                                elem::class,
                                maxDepth - 1,
                                visited,
                                collectionLimit
                            )
                            Property.Node("[$idx]", Node(elem::class.simpleName ?: "Anonymous", sub))
                        }
                    }
                    list.add(Property.Node(name, Node("Iterable", items)))
                }
                is Map<*, *> -> {
                    val entries = value.entries.take(collectionLimit).map { (k, v) ->
                        val key = k?.toString() ?: "null"
                        if (v == null || isLeaf(v)) {
                            Property.Val(key, v)
                        } else {
                            val sub = getProperties(
                                v,
                                v::class,
                                maxDepth - 1,
                                visited,
                                collectionLimit
                            )
                            Property.Node(key, Node(v::class.simpleName ?: "Anonymous", sub))
                        }
                    }
                    list.add(Property.Node(name, Node("Map", entries)))
                }
                is Array<*> -> {
                    val items = value.take(collectionLimit).mapIndexed { idx, elem ->
                        if (elem == null || isLeaf(elem)) {
                            Property.Val("[$idx]", elem)
                        } else {
                            val sub = getProperties(elem, elem::class, maxDepth - 1, visited, collectionLimit)
                            Property.Node("[$idx]", Node(elem::class.simpleName ?: "Anonymous", sub))
                        }
                    }
                    list.add(Property.Node(name, Node("Array", items)))
                }
                else -> {
                    // Обычный объект
                    val valueClass = value::class
                    val props = valueClass.memberProperties
                    if (props.isEmpty()) {
                        list.add(Property.Val(name, value.toString()))
                    } else {
                        val sub = getProperties(value, valueClass, maxDepth - 1, visited, collectionLimit)
                        val className = valueClass.simpleName ?: "Anonymous"
                        list.add(Property.Node(name, Node(className, sub)))
                    }
                }
            }
        } catch (e: Exception) {
            errors.add(e)
        }
    }

    // можно здесь как-то использовать errors (лог/метрика/спец-нода)
    return list
}

fun main() {
    val state = ItemStateImpl()
    val itemObject = ServerItemObject("test", state)
    state.parent = itemObject
    val prefab = Prefab("id1", listOf(), listOf(Prefab.Tag("id", listOf())))
    itemObject.addComponent(Meta(prefab.tags.first()), "Meta")
    printItemComponents(itemObject, LoggerUtil.get())
}