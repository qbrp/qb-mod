package org.qbrp.engine.anticheat

import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.system.utils.format.Format.asMiniMessage

class UndesirableMod(override val id: String, val warn: String): Mod {
    override fun ifFounded(player: ServerPlayerEntity) {
        player.sendMessage(warn.asMiniMessage())
    }
}
