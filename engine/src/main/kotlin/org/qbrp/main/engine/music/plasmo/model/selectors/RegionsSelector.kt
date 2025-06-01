package org.qbrp.main.engine.music.plasmo.model.selectors

import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.main.core.regions.RegionsAPI

class RegionsSelector(
    override val params: List<String>,
) : Selector(), KoinComponent {
    override val type: String = "regions"
    constructor(region : String) : this(listOf(region))
    override fun match(player: ServerPlayerEntity): Boolean {
        val regs = params[0].trim().split(",").mapNotNull { get<RegionsAPI>().getRegion(it) }
        return regs.any { it.contains(player.blockPos.x, player.blockPos.y, player.blockPos.z) }
    }
}