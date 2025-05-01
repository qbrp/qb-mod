package org.qbrp.core.game.prefabs

import org.qbrp.core.game.model.components.Component

fun interface PrefabField {
    fun component(): Component

    companion object {
        operator fun invoke(factory: () -> Component): PrefabField = PrefabField { factory() }
    }
}