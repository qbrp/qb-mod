package org.qbrp.main.core.game.prefabs

import org.koin.core.context.GlobalContext
import org.qbrp.main.core.components.AnyComponent
import org.qbrp.main.core.components.ComponentRegistry

fun interface PrefabField {
    val componentName: String get() = GlobalContext.get().get<ComponentRegistry>().getComponentName(component())
    fun component(): AnyComponent

    companion object {
        operator fun invoke(factory: () -> AnyComponent): PrefabField = PrefabField { factory() }
    }
}