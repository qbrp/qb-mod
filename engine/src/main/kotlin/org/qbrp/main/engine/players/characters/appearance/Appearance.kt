package org.qbrp.main.engine.players.characters.appearance

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import org.qbrp.main.core.mc.player.service.PlayerBehaviour
import org.qbrp.main.engine.characters.model.AppearanceData
import org.qbrp.main.engine.characters.model.AppearanceData.Look

@Serializable
class Appearance: PlayerBehaviour() {
    @Transient var look: Look? = null
    var description = ""

    var tooltip = description

    private var skinUrl: String? = null
    private var model = "slim"

    fun updateLook(look: Look) {
        this.look = look
        skinUrl = look.skinUrl
        applyLook()
    }

    fun setModelFromAppearance(appearanceData: AppearanceData) {
        model = appearanceData.model
    }

    fun applyLook() {
        look?.let {
            try {
                if (skinUrl != null) {
                    player.executeCommand("""skin url "$skinUrl" $model""")
                }
                sendMessage("<gray>Применён облик ${it.name}")
            } catch (e: NullPointerException) {
                sendMessage("<gray>Персонаж не найден.")
            }
        }
    }
}