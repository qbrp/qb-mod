package org.qbrp.engine.assets.patches

import kotlinx.serialization.Serializable

@Serializable
data class DiffResult(
    val added: List<String>,
    val changed: List<String>,
    val deleted: List<String>
)