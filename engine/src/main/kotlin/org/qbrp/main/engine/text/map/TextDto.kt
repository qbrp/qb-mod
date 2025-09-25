package org.qbrp.main.engine.text.map

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.serialization.DTO

@Serializable
sealed interface TextDto: DTO {
    val style: StyleDto
}