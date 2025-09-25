package org.qbrp.main.core.mc.player.exception

import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import java.lang.RuntimeException

class PlayerNotFoundException(val id: MinecraftPlayerId): RuntimeException("Minecraft player $id not found")