package org.qbrp.engine.assets.resourcepack.baking

import com.fasterxml.jackson.annotation.JsonIgnore
import kotlinx.serialization.Serializable

@Serializable
data class Model(
    val parent: String,
    val model: String,
    val display: DisplayConfig? = null,
) {
    @JsonIgnore fun getName(): String {
        return model.split("/").last().split(".").first()
    }
}