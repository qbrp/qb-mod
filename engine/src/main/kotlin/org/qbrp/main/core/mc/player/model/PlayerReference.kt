package org.qbrp.main.core.mc.player.model

import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId

interface PlayerReference {
    val uuid: MinecraftPlayerId
}