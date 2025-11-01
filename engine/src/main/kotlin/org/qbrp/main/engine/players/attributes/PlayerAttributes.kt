package org.qbrp.main.engine.players.attributes

import kotlinx.serialization.Serializable
import org.qbrp.main.deprecated.resources.ServerResources
import org.qbrp.deprecated.resources.data.config.ConfigInitializationCallback
import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.engine.controls.entity.InteractionMode

@Serializable
data class PlayerAttributes(
    var speed: Int? = null
) : DataComponent {
    fun resetSpeed() {
        speed = null
    }

    companion object {
        private lateinit var defaultSpeeds: Map<InteractionMode, Int>

        init {
            loadDefaultSpeeds()
            ConfigInitializationCallback.EVENT.register {
                loadDefaultSpeeds()
            }
        }

        private fun loadDefaultSpeeds() {
            defaultSpeeds = mapOf(
                InteractionMode.DEFAULT to ServerResources.getConfig().players.defaultSurvivalSpeed,
                InteractionMode.BUILDER to ServerResources.getConfig().players.defaultCreativeSpeed,
                InteractionMode.CROW to ServerResources.getConfig().players.defaultSpectatorSpeed,
                InteractionMode.GM to ServerResources.getConfig().players.defaultCreativeSpeed,
            )
        }
    }
}