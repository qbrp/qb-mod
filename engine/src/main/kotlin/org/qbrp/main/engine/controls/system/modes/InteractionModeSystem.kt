package org.qbrp.main.engine.controls.system.modes

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.GameMode
import org.qbrp.main.core.fabricmc.player.hasPermission
import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.physics.player.MinecraftEntities
import org.qbrp.main.engine.physics.player.ServerMinecraftEntities
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.engine.players.model.service.PlayerList
import org.qbrp.main.engine.players.model.service.ServerPlayerList

class InteractionModeSystem {
    fun update(playerList: ServerPlayerList, entities: ServerMinecraftEntities) {
        for (player in playerList) {
            val interactionMode = player.getInteractionModeComponent()
            val isDirty = interactionMode.dirty
            if (!isDirty) continue
            val entity = entities.getEntity(player.playerId) ?: continue
            interactionMode.let {
                val mode = it.mode
                when (mode) {
                    InteractionMode.DEFAULT -> {
                        entity.interactionManager.changeGameMode(GameMode.SURVIVAL)
                    }

                    InteractionMode.BUILDER -> {
                        if (!player.hasPermission(BUILDER_MODE_PERMISSION)) throw IllegalStateException("Недостаточно прав")
                        entity.interactionManager.changeGameMode(GameMode.CREATIVE)
                    }

                    InteractionMode.CROW -> {
                        entity.interactionManager.changeGameMode(GameMode.SURVIVAL)
                        player.enableNoClip(entity)
                    }

                    InteractionMode.GM -> {
                        if (player.hasPermission(GM_MODE_PERMISSION)) throw IllegalStateException("Недостаточно прав")
                    }
                }
                when (it.noClip) {
                    true -> player.enableNoClip(entity)
                    false -> player.disableNoClip(entity, mode)
                }
            }
        }
    }

    fun ServerPlayerObject.enableNoClip(entity: ServerPlayerEntity) {
        if (!hasPermission(NO_CLIP_PERMISSION)) throw IllegalStateException("Недостаточно прав")
        entity.interactionManager.changeGameMode(GameMode.SPECTATOR)
    }

    fun ServerPlayerObject.disableNoClip(entity: ServerPlayerEntity, interactionMode: InteractionMode) {
        if (!hasPermission(NO_CLIP_PERMISSION)) throw IllegalStateException("Недостаточно прав")
        entity.interactionManager.changeGameMode(interactionMode.gameMode)
    }
}