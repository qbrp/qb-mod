package org.qbrp.main.engine.players.model.components

import org.qbrp.main.core.components.AssociatedComponentState
import org.qbrp.main.core.components.InstantiationContext
import org.qbrp.main.engine.players.model.PlayerObject

typealias PlayerState = AssociatedComponentState<PlayerObject>

open class PlayerComponentInstantiateContext(val player: PlayerObject,
                                             override val componentManager: PlayerState) : InstantiationContext<PlayerState>, PlayerState by componentManager