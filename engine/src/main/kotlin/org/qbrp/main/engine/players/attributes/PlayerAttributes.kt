package org.qbrp.main.engine.players.attributes

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import net.minecraft.world.GameMode
import org.qbrp.main.core.mc.player.service.PlayerBehaviour
import org.qbrp.main.deprecated.resources.ServerResources
import org.qbrp.deprecated.resources.data.config.ConfigInitializationCallback

@Serializable
class PlayerAttributes: PlayerBehaviour() {
    private var speed: Int? = null

    companion object {
        @Transient private lateinit var defaultSpeeds: Map<GameMode, Int>
        private fun loadDefaultSpeeds() {
            defaultSpeeds = mapOf(
                GameMode.SURVIVAL to ServerResources.getConfig().players.defaultSurvivalSpeed,
                GameMode.CREATIVE to ServerResources.getConfig().players.defaultCreativeSpeed,
                GameMode.SPECTATOR to ServerResources.getConfig().players.defaultSpectatorSpeed,
                GameMode.ADVENTURE to ServerResources.getConfig().players.defaultCreativeSpeed,
            )
        }
        init {
            loadDefaultSpeeds()
            ConfigInitializationCallback.EVENT.register {
                loadDefaultSpeeds()
            }
        }
    }

    fun getDefaultSpeed(): Int {
        return defaultSpeeds[player.entity.interactionManager.gameMode]!!
    }

    fun resetSpeed() {
        speed = null
    }

    fun setSpeed(speed: Int) {
        this.speed = speed
    }

    fun getSpeed(): Int {
        return speed ?: getDefaultSpeed()
    }
}