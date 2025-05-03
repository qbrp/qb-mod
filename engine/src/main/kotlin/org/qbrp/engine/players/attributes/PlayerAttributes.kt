package org.qbrp.engine.players.attributes

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import net.minecraft.world.GameMode
import org.qbrp.core.mc.player.PlayerObject
import org.qbrp.core.mc.player.model.PlayerBehaviour
import org.qbrp.core.resources.ServerResources
import org.qbrp.core.resources.data.config.ConfigInitializationCallback

class PlayerAttributes: PlayerBehaviour() {
    @JsonProperty("speed") private var speed: Int? = null

    companion object {
        @JsonIgnore private lateinit var defaultSpeeds: Map<GameMode, Int>
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

    @JsonIgnore
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