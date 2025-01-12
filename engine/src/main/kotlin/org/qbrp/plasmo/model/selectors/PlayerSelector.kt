package org.qbrp.plasmo.model.selectors
import net.minecraft.server.network.ServerPlayerEntity

class PlayerSelector(
    override val params: List<String>,
    val type: String = "player"
) : Selector() {
    constructor(nickname : String) : this(listOf(nickname))
    override fun match(player: ServerPlayerEntity): Boolean {
        return params[0] == player.name.string
    }

}