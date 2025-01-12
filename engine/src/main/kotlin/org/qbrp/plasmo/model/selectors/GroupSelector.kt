package org.qbrp.plasmo.model.selectors
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.plasmo.MusicStorage

class GroupSelector(
    override val params: List<String>,
    val type: String = "group"
) : Selector() {
    constructor(name : String) : this(listOf(name))
    override fun match(player: ServerPlayerEntity): Boolean {
        return MusicStorage.groups.any { params[0] in it.players }
    }
}