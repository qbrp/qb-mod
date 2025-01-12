package org.qbrp.plasmo.model.selectors

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.plasmo.MusicStorage

class RegionSelector(
    override val params: List<String>,
    val type: String = "region"
) : Selector() {
    constructor(region : String) : this(listOf(region))
    override fun match(player: ServerPlayerEntity): Boolean {
        val region = MusicStorage.getRegion(params[0]) ?: let { return false }
        return region.contains(player.blockX, player.blockY, player.blockZ)
    }
}