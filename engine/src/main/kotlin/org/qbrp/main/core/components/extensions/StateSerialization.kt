package org.qbrp.main.core.components.extensions

import org.koin.core.context.GlobalContext
import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.exception.ComponentNotFoundException
import org.qbrp.main.core.components.serialization.ComponentJsonField
import org.qbrp.main.core.components.serialization.SaveListener
import org.qbrp.main.core.components.serialization.map.ComponentDtoMapper
import org.qbrp.main.core.components.serialization.map.ComponentMapContext
import org.qbrp.main.core.components.state.ComponentHolder
import org.qbrp.main.core.components.state.StateJsonField
import org.qbrp.main.core.networking.serialization.SerializersRegistry
import org.qbrp.main.core.util.log.Logger
import org.qbrp.main.core.util.log.LoggerUtil
import kotlin.reflect.KClass

class DtoMappingRules(
    val priorities: Map<KClass<out AnyComponent>, Int>
)

class DtoLoadRules(
    val priorities: Map<String, Int>
)

fun ComponentManager.toDTO(
    serializersRegistry: SerializersRegistry = GlobalContext.get().get(),
    componentDtoMapper: ComponentDtoMapper = GlobalContext.get().get(),
    rules: DtoMappingRules? = null
): StateJsonField {
    val copyState = this.copy(
        ComponentHolder(),
        { n, c -> c.shouldSave && componentDtoMapper.hasToDtoStrategy(c::class) && c.dto != null }
    )

    copyState.forEach {
        if (it is SaveListener) it.onSave(copyState)
    }

    val seq = copyState.namedIterator().asSequence()
    val components = rules?.let {
        r -> seq.sortedByDescending { r.priorities[it.second::class] ?: 0 }
    }
        ?: seq

    val errors = mutableListOf<Exception>()
    return StateJsonField(components
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
    rules: DtoLoadRules? = null,
    notFound: (ComponentJsonField) -> Unit, error: (ComponentJsonField, Exception) -> Unit
) {

    val seq = state.components
    val components = rules?.let {
        r -> seq.sortedByDescending { r.priorities[it.type] ?: 0 }
    }
        ?: seq

    components.forEach {
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
    rules: DtoLoadRules? = null,
    serializersRegistry: SerializersRegistry = GlobalContext.get().get()
) {
    loadJson(list, operator, mapper, serializersRegistry, rules, {
        fallbackLogger.warn("Компонент ${it.type} не был найден в реестре и был пропущен")
    }, { c, ex ->
        fallbackLogger.warn("Произошла ошибка загрузки ${c.type}, компонент пропущен")
        ex.printStackTrace()
    })
}