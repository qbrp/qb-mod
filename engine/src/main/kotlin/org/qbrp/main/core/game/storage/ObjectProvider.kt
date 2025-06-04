package org.qbrp.main.core.game.storage

import org.qbrp.main.core.game.serialization.Identifiable

interface ObjectProvider<T: Identifiable> {
    fun getById(id: String): T?
    fun getByIdOrThrow(id: String): T = getById(id)!!
}