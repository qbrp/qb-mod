package org.qbrp.plasmo.model.selectors
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.plasmo.contoller.AddonStorage

class GroupSelector(
    override val params: MutableMap<String, String>
) : Selector() {
    constructor(name : String) : this(mutableMapOf("name" to name))
    override fun match(player: ServerPlayerEntity): Boolean {
        return AddonStorage.groups.any { params["name"] in it.players }
    }
}