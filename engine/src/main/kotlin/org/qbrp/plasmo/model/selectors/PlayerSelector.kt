package org.qbrp.plasmo.model.selectors
import net.minecraft.server.network.ServerPlayerEntity

class PlayerSelector(
    override val params: MutableMap<String, String>
) : Selector() {
    constructor(nickname : String) : this(mutableMapOf("nickname" to nickname))
    override fun match(player: ServerPlayerEntity): Boolean {
        return params["nickname"] == player.name.string
    }

}