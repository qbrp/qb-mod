package org.qbrp.main.engine.music.plasmo.model.selectors
import net.minecraft.server.network.ServerPlayerEntity

class GroupSelector(
    override val params: List<String>,
) : Selector() {
    override val type: String = "group"
    constructor(name : String) : this(listOf(name))
    override fun match(player: ServerPlayerEntity): Boolean {
        //return Groups.getGroup(params[0])?.contains(player) == true
        TODO()
    }
}