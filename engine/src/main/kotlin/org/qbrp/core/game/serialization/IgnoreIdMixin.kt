package org.qbrp.core.game.serialization

import com.fasterxml.jackson.annotation.JsonIgnore

abstract class IgnoreIdMixin {
    @JsonIgnore
    var _id: String? = null
}