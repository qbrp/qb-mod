package org.qbrp.main.core.game.storage

import org.qbrp.main.core.components.serialization.Identifiable
import org.qbrp.main.core.dataformats.StringId

interface ObjectProvider<out T: Identifiable> {
    fun getById(id: String): T?
    fun getByIdOrThrow(id: String): T = getById(id)!!
    fun getById(id: StringId): T? = getById(id.value)
}