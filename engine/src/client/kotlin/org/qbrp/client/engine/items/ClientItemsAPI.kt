package org.qbrp.client.engine.items

import org.qbrp.main.core.game.storage.Storage

interface ClientItemsAPI {
    val storage: Storage<ClientItemObject>
}