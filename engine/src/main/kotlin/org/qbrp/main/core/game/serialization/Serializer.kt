package org.qbrp.main.core.game.serialization

import org.qbrp.main.core.game.model.objects.BaseObject

abstract class Serializer<T : BaseObject> {
    abstract fun toJson(t: T): String
}