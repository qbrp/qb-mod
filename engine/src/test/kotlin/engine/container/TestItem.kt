package engine.container

import org.qbrp.main.core.util.StringId
import org.qbrp.main.engine.items.model.ItemState
import org.qbrp.main.engine.items.model.ServerItemObject

class TestItem(id: StringId, state: ItemState): ServerItemObject(id.value, state)