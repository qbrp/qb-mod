package org.qbrp.engine.players.characters.appearance

import com.fasterxml.jackson.annotation.JsonIgnore
import org.qbrp.core.mc.player.model.PlayerBehaviour
import org.qbrp.engine.characters.model.AppearanceData
import org.qbrp.engine.characters.model.AppearanceData.Look
import org.qbrp.engine.characters.model.CharacterData
import org.qbrp.engine.players.characters.Character

class Appearance: PlayerBehaviour() {
    @JsonIgnore var tooltip = ""
    var look: Look? = null

    private var skinUrl = look?.skinUrl
    private var model = "slim"

    fun updateLook(look: Look) {
        this.look = look
        applyLook()
    }

    fun setModelFromAppearance(appearanceData: AppearanceData) {
        model = appearanceData.model
    }

    fun applyLook() {
        try {
            tooltip = composeDescription()
            if (look?.skinUrl != null) {
                player.executeCommand("""skin url "$skinUrl" $model""")
            }
            sendMessage("<gray>Применён облик $look")
        } catch (e: NullPointerException) {
            sendMessage("<gray>Персонаж не найден.")
        }
    }

    private fun composeDescription(): String {
        val character = getComponent<Character>()?.data ?: return ""
        return "${character.appearance.description}${look?.textDescription}"
    }
}