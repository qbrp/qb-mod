package org.qbrp.main.engine.music.plasmo.model.selectors

import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.main.core.regions.RegionsAPI

class RegionSelector(
    override val params: List<String>,
) : Selector(), KoinComponent {
    override val type: String = "region"
    constructor(region : String) : this(listOf(region))
    override fun match(player: ServerPlayerEntity): Boolean {
        val reg = get<RegionsAPI>().getRegion(params[0]) ?: return false
        return reg.contains(player.blockPos.x, player.blockPos.y, player.blockPos.z)
    }
}