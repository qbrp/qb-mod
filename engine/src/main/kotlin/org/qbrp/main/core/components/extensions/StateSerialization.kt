package org.qbrp.main.core.components.extensions

import org.koin.core.context.GlobalContext
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.components.DtoProvider
import org.qbrp.main.core.components.construct.ComponentBuilderOperator
import org.qbrp.main.core.components.exception.ComponentNotFoundException
import org.qbrp.main.core.components.serialization.ComponentJsonField
import org.qbrp.main.core.components.serialization.SaveListener
import org.qbrp.main.core.components.serialization.map.ComponentDtoMapper
import org.qbrp.main.core.components.serialization.map.ComponentMapContext
import org.qbrp.main.core.components.state.ComponentHolder
import org.qbrp.main.core.components.state.StateJsonField
import org.qbrp.main.core.networking.serialization.SerializersRegistry
import org.qbrp.main.core.utils.log.Logger
import org.qbrp.main.core.utils.log.LoggerUtil

fun ComponentManager.toDTO(
    serializersRegistry: SerializersRegistry = GlobalContext.get().get(),
    componentDtoMapper: ComponentDtoMapper = GlobalContext.get().get()
): StateJsonField {
    val copyState = this.copy(ComponentHolder(), { n, c -> c.shouldSave && componentDtoMapper.hasToDtoStrategy(c::class) || c.dto != null })

    copyState.forEach {
        if (it is SaveListener) it.onSave(copyState)
    }

    val errors = mutableListOf<Exception>()
    return StateJsonField(copyState
        .namedIterator()
        .asSequence()
        .mapNotNull { (n, c) ->
            try {
                val context = ComponentMapContext(this, n.type)
                ComponentJsonField.from(c, n, context, serializersRegistry)
            } catch (e: Exception) {
                LoggerUtil.get("serialization").error("Ошибка сериализации компонента ${n.full}: ${e.message}")
                errors += e
                e.printStackTrace()
                null
            }
        }
        .toList()
    ).also {
        if (errors.count() > 0) throw RuntimeException("При сериализации данных возникли ошибки")
    }
}

fun ComponentManager.loadJson(
    state: StateJsonField,
    operator: ComponentManager,
    mapper: ComponentDtoMapper,
    serializers: SerializersRegistry,
    notFound: (ComponentJsonField) -> Unit, error: (ComponentJsonField, Exception) -> Unit
) {
    state.components.forEach {
        val name = it.type
        try {
            addComponent(it.toComponent(operator, serializers, mapper), name = name)
        } catch (ex: Exception) {
            if (ex is ComponentNotFoundException) {
                notFound(it)
            } else {
                error(it, ex)
            }
        }
    }
}

fun ComponentManager.loadJson(
    list: StateJsonField,
    operator: ComponentManager,
    fallbackLogger: Logger = LoggerUtil.get("components", "json"),
    mapper: ComponentDtoMapper = GlobalContext.get().get(),
    serializersRegistry: SerializersRegistry = GlobalContext.get().get()
) {
    loadJson(list, operator, mapper, serializersRegistry, {
        fallbackLogger.warn("Компонент ${it.type} не был найден в реестре и был пропущен")
    }, { c, ex ->
        fallbackLogger.warn("Произошла ошибка загрузки ${c.type}, компонент пропущен")
        ex.printStackTrace()
    })
}

fun <T: ComponentBuilderOperator<*>> StateJsonField.build(factory: () -> T): T {
    return factory().apply {
        loadJson(this@build, this)
    }
}

fun <T: ComponentBuilderOperator<*>> StateJsonField?.buildNull(factory: () -> T): T {
    return factory().apply {
        if (this@buildNull != null) loadJson(this@buildNull, this)
    }
}