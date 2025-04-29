package org.qbrp.core.game.lifecycle

import org.qbrp.core.game.model.objects.BaseObject

interface Lifecycle<T : BaseObject> {
    fun onCreated(obj: T)
    fun unload(obj: T)
    fun save(obj: T)
}