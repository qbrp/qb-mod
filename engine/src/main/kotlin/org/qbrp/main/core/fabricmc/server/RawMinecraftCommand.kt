package org.qbrp.main.core.fabricmc.server

import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.core.components.extensions.add
import org.qbrp.main.core.components.extensions.require
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.world
import java.util.concurrent.ConcurrentLinkedQueue

@JvmInline
value class RawMinecraftCommand(val value: String)

sealed class CommandActor {
    data class Player(val player: ServerPlayerObject) : CommandActor()
    object Root : CommandActor()
}

data class CommandInvoke(
    val command: RawMinecraftCommand,
    val actor: CommandActor
)

data class CommandHolder(
    val commands: ConcurrentLinkedQueue<CommandInvoke> = ConcurrentLinkedQueue()
) : DataComponent

fun ServerPlayerObject.invokeCommandAsSelf(command: String) {
    world.require<CommandHolder>().commands += CommandInvoke(RawMinecraftCommand(command), CommandActor.Player(this))
}

fun ServerPlayerObject.invokeCommandAsRoot(command: String) {
    world.require<CommandHolder>().commands += CommandInvoke(RawMinecraftCommand(command), CommandActor.Root)
}