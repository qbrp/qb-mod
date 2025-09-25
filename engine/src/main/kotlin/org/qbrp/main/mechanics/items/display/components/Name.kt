package org.qbrp.main.mechanics.items.display.components

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.mechanics.items.display.NameData

/** Статическое имя, отображаемое для всех игроков одинаково **/
@Serializable
class Name(override val text: String): DataComponent, NameData