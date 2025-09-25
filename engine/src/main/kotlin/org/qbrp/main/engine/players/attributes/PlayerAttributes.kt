package org.qbrp.main.engine.players.attributes

import kotlinx.serialization.Serializable
import org.qbrp.main.engine.players.model.components.PlayerComponent
import org.qbrp.main.deprecated.resources.ServerResources
import org.qbrp.deprecated.resources.data.config.ConfigInitializationCallback
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.core.mc.player.gamemode.MinecraftGameMode
import org.qbrp.main.engine.players.model.components.PlayerComponentInstantiateContext

class PlayerAttributes(ctx: PlayerComponentInstantiateContext, private var speed: Int? = null): PlayerComponent(ctx) {
    @Serializable
    data class Data(val speed: Int?): DTO
    override val dto: DTO get() = Data(speed)

    companion object {
        private lateinit var defaultSpeeds: Map<MinecraftGameMode, Int>
        private fun loadDefaultSpeeds() {
            defaultSpeeds = mapOf(
                MinecraftGameMode.SURVIVAL to ServerResources.getConfig().players.defaultSurvivalSpeed,
                MinecraftGameMode.CREATIVE to ServerResources.getConfig().players.defaultCreativeSpeed,
                MinecraftGameMode.SPECTATOR to ServerResources.getConfig().players.defaultSpectatorSpeed,
                MinecraftGameMode.ADVENTURE to ServerResources.getConfig().players.defaultCreativeSpeed,
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
        return defaultSpeeds[serverPlayer.serverState.gameMode]!!
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