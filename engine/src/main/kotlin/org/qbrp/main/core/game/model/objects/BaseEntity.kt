package org.qbrp.main.core.game.model.objects

import org.qbrp.main.core.game.serialization.Identifiable

abstract class BaseEntity<T : BaseEntity<T>>(
    open override val id: String,
): Identifiable {
}
