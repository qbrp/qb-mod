package org.qbrp.main.engine.items.model

import org.qbrp.main.core.game.lifecycle.LifecycleManager
import org.qbrp.main.engine.items.ItemsModule

class ItemLifecycle(storage: ItemStorage<ServerItemObject>,
                    repository: ItemRepository
): LifecycleManager<ServerItemObject, ItemsModule>(storage, repository) {

}