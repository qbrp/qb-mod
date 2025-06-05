package org.qbrp.main.engine.items.components

import kotlinx.serialization.Transient
import org.qbrp.main.core.game.model.State
import org.qbrp.main.core.game.model.components.Behaviour
import org.qbrp.main.engine.items.ItemObject
import org.qbrp.main.engine.items.model.ServerItemObject

open class ItemBehaviour: Behaviour() {
    @Transient protected val item get() = requireState().obj as ItemObject
    @Transient protected val serverItem get() = requireState().obj as ServerItemObject
    open fun updatePlaceholders(placeholders: Map<String, String>): Map<String, String> { return emptyMap() }
}