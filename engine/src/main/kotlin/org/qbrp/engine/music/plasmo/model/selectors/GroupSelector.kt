package org.qbrp.engine.music.plasmo.model.selectors
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.engine.Engine

class GroupSelector(
    override val params: List<String>,
) : Selector() {
    override val type: String = "group"
    constructor(name : String) : this(listOf(name))
    override fun match(player: ServerPlayerEntity): Boolean {
        return false
    }
}