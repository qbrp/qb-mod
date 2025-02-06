package org.qbrp.engine.music.plasmo.model.selectors

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonSubTypes.Type
import com.fasterxml.jackson.annotation.JsonTypeInfo
import net.minecraft.server.network.ServerPlayerEntity

@JsonTypeInfo(
   use = JsonTypeInfo.Id.NAME,
   include = JsonTypeInfo.As.PROPERTY,
   property = "type"
)
@JsonSubTypes(Type(value = GroupSelector::class, name = "group"),
   Type(value = RegionSelector::class, name = "region"),
   Type(value = PlayerSelector::class, name = "player"))
abstract class Selector {
   open val type: String = "selector"
   abstract val params: List<String>
   abstract fun match(player: ServerPlayerEntity): Boolean
}