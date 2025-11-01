package engine.interactions

import io.kotest.assertions.throwables.shouldNotThrow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Test
import org.qbrp.main.engine.controls.entity.interactions.Intent
import org.qbrp.main.network.interaction.InteractableIconDTO
import org.qbrp.main.network.interaction.InteractableViewDTO
import org.qbrp.main.engine.controls.dto.InteractionViewDTO
import org.qbrp.main.engine.text.map.LiteralTextDTO
import org.qbrp.main.engine.text.map.StyleDto

class InteractionMappingText {

    @Test
    fun `interaction view dto serializes correctly`() {
        val view = InteractionViewDTO(
            InteractableViewDTO(LiteralTextDTO("1", StyleDto(-1)), InteractableIconDTO.Asset("id"),  -1),
            InteractableViewDTO(LiteralTextDTO("1", StyleDto(-1)), InteractableIconDTO.Asset("id"),  -1),
            LiteralTextDTO("Name", StyleDto(-1)),
            Intent.DEFAULT,
            true,
            listOf(),
            listOf()
        )
        shouldNotThrow<Exception> {
            val json = Json.encodeToString(view)
            val value = Json.decodeFromString<InteractionViewDTO>(json)
        }
    }

}