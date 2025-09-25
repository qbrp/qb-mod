package engine.text

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.matchers.shouldBe
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.qbrp.main.core.networking.serialization.SerializersRegistry
import org.qbrp.main.core.networking.serialization.SerializersRegistryImpl
import org.qbrp.main.core.networking.serialization.mapping.toDtoTyped
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.components.TextSequence
import org.qbrp.main.engine.text.literal
import org.qbrp.main.engine.text.map.LiteralTextDTO
import org.qbrp.main.engine.text.map.StyleDto
import org.qbrp.main.engine.text.map.TextDto
import org.qbrp.main.engine.text.map.TextMapperRegistrar
import org.qbrp.main.engine.text.map.TextMappingRegisterContext
import org.qbrp.main.engine.text.map.TextMappingRegistry
import org.qbrp.main.engine.text.map.TextSequenceDto

class TextTests {
    @Test
    fun `generic text dto should serialize`() {
        val genericTextDto: TextDto = LiteralTextDTO("123", StyleDto(0))
        shouldNotThrow<Exception> {
            Json.encodeToString(TextDto.serializer(), genericTextDto)
        }
    }

    @Test
    fun `text sequence mapping is correct`() {
        val text = TextSequence.Builder()
            .literal("A Literal")
            .literal(" - ")
            .literal("A Literal 2")
            .build()
        val dto = mapperRegistry.toDtoTyped<TextSequenceDto>(text)
        dto.components[0] shouldBe LiteralTextDTO("A Literal", StyleDto(-1))
        dto.components[1] shouldBe LiteralTextDTO(" - ", StyleDto(-1))
        dto.components[2] shouldBe LiteralTextDTO("A Literal 2", StyleDto(-1))
    }

    companion object {
        fun newContext(mappingRegistry: TextMappingRegistry): TextMappingRegisterContext {
            return object : TextMappingRegisterContext {
                override val mapperRegistry: TextMappingRegistry = mappingRegistry
                override val serializersRegistry: SerializersRegistry = SerializersRegistryImpl()
            }
        }
        private val registrar = TextMapperRegistrar()
        private val mapperRegistry: TextMappingRegistry = TextMappingRegistry()

        @BeforeAll
        @JvmStatic
        fun setup() {
            with(newContext(mapperRegistry)) { registrar.registerAll() }
        }
    }
}