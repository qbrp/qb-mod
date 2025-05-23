package org.qbrp.engine.players.characters.appearance

import com.fasterxml.jackson.annotation.JsonIgnore
import org.qbrp.core.mc.player.model.PlayerBehaviour
import org.qbrp.engine.characters.model.AppearanceData
import org.qbrp.engine.characters.model.AppearanceData.Look
import org.qbrp.engine.characters.model.CharacterData
import org.qbrp.engine.players.characters.Character

class Appearance: PlayerBehaviour() {
    @JsonIgnore var look: Look? = null
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