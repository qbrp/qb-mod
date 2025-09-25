package org.qbrp.main.mechanics.characters.appearance.looks

import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.executeCommand
import org.qbrp.main.core.mc.server.command.RawMinecraftCommand
import org.qbrp.main.engine.players.characters.components.CharacterComponent
import org.qbrp.main.engine.players.characters.components.CharacterComponentInstantiateContext

class Skin(ctx: CharacterComponentInstantiateContext): CharacterComponent(ctx) {
    val model get() = character.appearance.model

    context(ctx: MinecraftContext)
    fun apply(skinUrl: String) {
        serverPlayer.executeCommand(RawMinecraftCommand("""skin url "$skinUrl" $model"""))
    }
}