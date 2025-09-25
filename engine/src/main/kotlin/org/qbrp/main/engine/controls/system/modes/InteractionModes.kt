package org.qbrp.main.engine.controls.system.modes

import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.gamemode.MinecraftGameMode
import org.qbrp.main.core.mc.player.hasPermission
import org.qbrp.main.core.mc.player.permission.Permission
import org.qbrp.main.core.mc.player.setGameMode
import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.players.model.ServerPlayerObject

context(ctx: MinecraftContext)
fun setDefaultMode(player: ServerPlayerObject) {
    player.setGameMode(MinecraftGameMode.SURVIVAL)
}

context(ctx: MinecraftContext)
fun setCrowMode(player: ServerPlayerObject) {
    enableNoClip(player)
}

context(ctx: MinecraftContext)
fun setBuilderMode(player: ServerPlayerObject) {
    if (!player.hasPermission(BUILDER_MODE_PERMISSION)) throw IllegalStateException("Недостаточно прав")
    player.setGameMode(MinecraftGameMode.CREATIVE)
}

context(ctx: MinecraftContext)
fun setGmMode(player: ServerPlayerObject) {
    if (!player.hasPermission(GM_MODE_PERMISSION)) throw IllegalStateException("Недостаточно прав")
}

context(ctx: MinecraftContext)
fun enableNoClip(player: ServerPlayerObject) {
    if (!player.hasPermission(NO_CLIP_PERMISSION)) throw IllegalStateException("Недостаточно прав")
    player.setGameMode(MinecraftGameMode.SPECTATOR)
}

context(ctx: MinecraftContext)
fun disableNoClip(player: ServerPlayerObject, interactionMode: InteractionMode) {
    if (!player.hasPermission(NO_CLIP_PERMISSION)) throw IllegalStateException("Недостаточно прав")
    player.setGameMode(interactionMode.gameMode)
}

internal val InteractionMode.gameMode
    get() = when (this) {
        InteractionMode.DEFAULT -> MinecraftGameMode.SURVIVAL
        InteractionMode.BUILDER -> MinecraftGameMode.CREATIVE
        InteractionMode.CROW -> MinecraftGameMode.ADVENTURE
        InteractionMode.GM -> MinecraftGameMode.CREATIVE
    }

val NO_CLIP_PERMISSION = Permission("no-clip")
val BUILDER_MODE_PERMISSION = Permission("builder-mode")
val GM_MODE_PERMISSION = Permission("gm-mode")
val CROW_MODE_PERMISSION = Permission("crow-mode")