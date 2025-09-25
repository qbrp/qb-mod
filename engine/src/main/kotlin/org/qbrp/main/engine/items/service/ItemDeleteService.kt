package org.qbrp.main.engine.items.service

import org.qbrp.main.engine.items.model.ServerItemObject

interface ItemDeleteService {
    fun delete(item: ServerItemObject)
}