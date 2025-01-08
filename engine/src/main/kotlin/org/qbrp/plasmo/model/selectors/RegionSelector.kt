package org.qbrp.plasmo.model.selectors

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.plasmo.contoller.AddonStorage

class RegionSelector(
    override val params: MutableMap<String, String> = mutableMapOf("name" to ""),
) : Selector() {
    constructor(region : String) : this(mutableMapOf("name" to region))
    override fun match(player: ServerPlayerEntity): Boolean {
        val region = AddonStorage.getRegion(params["name"].toString()) ?: let { return false }
        return region.contains(player.blockX, player.blockY, player.blockZ)
    }
}