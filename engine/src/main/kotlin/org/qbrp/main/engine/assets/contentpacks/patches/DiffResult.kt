package org.qbrp.main.engine.assets.contentpacks.patches

import kotlinx.serialization.Serializable

@Serializable
data class DiffResult(
    val added: List<String>,
    val changed: List<String>,
    val deleted: List<String>
)