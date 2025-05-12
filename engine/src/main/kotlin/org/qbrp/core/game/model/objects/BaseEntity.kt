package org.qbrp.core.game.model.objects

import com.fasterxml.jackson.annotation.JsonIgnore
import org.qbrp.core.game.lifecycle.Lifecycle

abstract class BaseEntity<T : BaseEntity<T>>(
    open val name: String,
    @JsonIgnore open val lifecycle: Lifecycle<T>
) {
    open fun unload() {
        lifecycle.unload(this as T)
    }

    open fun save() {
        lifecycle.save(this as T)
    }
}
