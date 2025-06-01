package org.qbrp.main.core.game.model.objects

import com.fasterxml.jackson.annotation.JsonIgnore
import org.qbrp.main.core.game.lifecycle.Lifecycle
import org.qbrp.main.core.game.serialization.Identifiable

abstract class BaseEntity<T : BaseEntity<T>>(
    open override val id: Any,
    @Transient open val lifecycle: Lifecycle<T>
): Identifiable {
    open fun unload() {
        lifecycle.unload(this as T)
    }

    open fun save() {
        lifecycle.save(this as T)
    }
}
