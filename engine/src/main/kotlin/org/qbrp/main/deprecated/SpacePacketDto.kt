package org.qbrp.main.deprecated

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.serialization.DTO

@Serializable
class SpacePacketDto(val ids: List<String>, val name: String, val objectId: String, val containerId: String, val fulness: Double, val info: String): DTO {}