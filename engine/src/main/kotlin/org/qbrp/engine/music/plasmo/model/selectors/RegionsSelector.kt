package org.qbrp.engine.music.plasmo.model.selectors

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.core.regions.Regions

class RegionsSelector(
    override val params: List<String>,
) : Selector() {
    override val type: String = "regions"
    constructor(region : String) : this(listOf(region))
    override fun match(player: ServerPlayerEntity): Boolean {
        val regs = params[0].trim().split(",").mapNotNull { Regions.getRegion(it) }
        return regs.any { it.contains(player.blockPos.x, player.blockPos.y, player.blockPos.z) }
    }
}