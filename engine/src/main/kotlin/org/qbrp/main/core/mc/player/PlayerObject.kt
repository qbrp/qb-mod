package org.qbrp.main.core.mc.player

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.world.ServerWorld
import org.qbrp.main.core.game.model.State
import org.qbrp.main.core.game.loop.Tick
import org.qbrp.main.core.game.model.objects.BaseObject
import org.qbrp.main.core.game.serialization.Identifiable

abstract class PlayerObject(
    override val state: State = State(),
): BaseObject(), Tick<ServerWorld>, Identifiable {
    abstract val entity: PlayerEntity?
    abstract val entityName: String

    override fun tick(context: ServerWorld) = tickState(context)
}
