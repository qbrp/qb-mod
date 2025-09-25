package org.qbrp.main.mechanics.characters.physical

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.engine.players.characters.components.CharacterBind
import org.qbrp.main.mechanics.characters.model.CharacterData

/**
 * # Физические показатели
 * Загружаются лишь один раз, после чего становятся полностью независимыми от CharacterData
 */
@Serializable
class CharacterPhysicalParameters(var mass: Double, var gripStrength: Double): DataComponent, CharacterBind {
    constructor(data: CharacterData): this(data.mass, data.gripStrength)
}