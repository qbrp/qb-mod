package org.qbrp.plasmo.model.selectors

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import net.minecraft.server.network.ServerPlayerEntity

@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   include = JsonTypeInfo.As.PROPERTY,
   property = "type"
)
@JsonSubTypes(
   JsonSubTypes.Type(value = GroupSelector::class, name = "group"),
   JsonSubTypes.Type(value = RegionSelector::class, name = "region"),
   JsonSubTypes.Type(value = PlayerSelector::class, name = "player")
)
abstract class Selector() {
   companion object { const val name: String = "selector" }
   abstract val params: List<String>
   abstract fun match(player: ServerPlayerEntity): Boolean
}