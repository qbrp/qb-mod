package org.qbrp.engine.music.plasmo.model.selectors

import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class SelectorBuilder {
    companion object {
        val selectorMap = mapOf(
            "players" to PlayersSelector::class,
            "group" to GroupSelector::class,
            "region" to RegionSelector::class,
            "regions" to RegionsSelector::class
        )
    }

    fun createSelector(name: String, param: String): Selector? {
        val kClass = selectorMap[name] ?: return null
        val secondaryConstructor = kClass.constructors.firstOrNull {
            it.parameters.size == 1 && it.parameters[0].type.classifier == String::class
        } ?: return null
        return secondaryConstructor.call(param)
    }

    fun createSelector(name: String, params: List<String>): Selector? {
        return selectorMap[name]?.primaryConstructor?.call(params)
    }

    fun getSelectorName(selectorClass: KClass<out Selector>): String? {
        return selectorMap.entries.find { it.value == selectorClass }?.key
    }
}
