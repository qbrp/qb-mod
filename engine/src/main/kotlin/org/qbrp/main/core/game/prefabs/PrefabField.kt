package org.qbrp.main.core.game.prefabs

import org.qbrp.main.core.game.model.components.Component

fun interface PrefabField {
    fun component(): Component

    companion object {
        operator fun invoke(factory: () -> Component): PrefabField = PrefabField { factory() }
    }
}