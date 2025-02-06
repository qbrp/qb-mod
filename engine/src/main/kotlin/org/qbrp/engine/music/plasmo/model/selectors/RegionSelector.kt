package org.qbrp.engine.music.plasmo.model.selectors

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.core.regions.Regions

class RegionSelector(
    override val params: List<String>,
) : Selector() {
    override val type: String = "region"
    constructor(region : String) : this(listOf(region))
    override fun match(player: ServerPlayerEntity): Boolean {
        val reg = Regions.getRegion(params[0]) ?: return false
        return reg.contains(player.blockPos.x, player.blockPos.y, player.blockPos.z)
    }
}