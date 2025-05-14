package org.qbrp.engine.items.model

import org.qbrp.core.game.serialization.ObjectJsonField
import org.qbrp.core.game.model.State

class ItemJsonField(id: Long, state: State): ObjectJsonField(id, state, false) {


}