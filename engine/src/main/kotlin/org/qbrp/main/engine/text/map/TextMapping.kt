package org.qbrp.main.engine.text.map

import klite.publicProperties
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.qbrp.main.core.networking.serialization.SerializerRegistryId
import org.qbrp.main.core.networking.serialization.SerializersRegistry
import org.qbrp.main.engine.text.style.Style
import org.qbrp.main.engine.text.style.TextColor
import org.qbrp.main.engine.text.components.TextComponent
import org.qbrp.main.engine.text.components.LiteralTextComponent
import org.qbrp.main.engine.text.components.MiniMessageTextComponent
import org.qbrp.main.engine.text.components.TextSequence

context(textMapper: TextMappingRegistry)
fun TextComponent.toDto() = textMapper.toDto(this)

context(textMapper: TextMappingRegistry)
fun TextDto.toText() = textMapper.fromDto(this)

class TextMapperRegistrar() {
    context(ctx: TextMappingRegisterContext)
    fun registerAll() {
        register(LiteralTextMapper)
        register(TextSequenceMapper(ctx.mapperRegistry))
        register(MiniMessageTextMapper)
    }

    context(ctx: TextMappingRegisterContext)
    inline fun <reified C: TextComponent, reified D: TextDto> register(mapper: TextMapper<C, D>) {
        ctx.mapperRegistry.register(mapper, D::class, C::class)
        ctx.serializersRegistry.registerSerializer(mapper.serializerRegistryId, D::class)
    }
}

interface TextMappingRegisterContext {
    val serializersRegistry: SerializersRegistry
    val mapperRegistry: TextMappingRegistry
}

@Serializable @SerialName("mini_message") data class MiniMessageTextDto(val text: String, override val style: StyleDto): TextDto
object MiniMessageTextMapper: TextMapper<MiniMessageTextComponent, MiniMessageTextDto> {
    override val serializerRegistryId: SerializerRegistryId = SerializerRegistryId(MiniMessageTextDto::class)
    override fun fromDto(dto: MiniMessageTextDto): MiniMessageTextComponent {
        return MiniMessageTextComponent(dto.text)
    }

    override fun toDto(obj: MiniMessageTextComponent): MiniMessageTextDto {
        return MiniMessageTextDto(obj.data, StyleMapper.styleToDto(obj.style))
    }
}

@Serializable @SerialName("literal") data class LiteralTextDTO(val text: String, override val style: StyleDto): TextDto
object LiteralTextMapper: TextMapper<LiteralTextComponent, LiteralTextDTO> {
    override val serializerRegistryId: SerializerRegistryId = SerializerRegistryId(LiteralTextDTO::class)
    override fun fromDto(dto: LiteralTextDTO): LiteralTextComponent {
        return LiteralTextComponent(dto.text, StyleMapper.dtoToStyle(dto.style))
    }

    override fun toDto(obj: LiteralTextComponent): LiteralTextDTO {
        return LiteralTextDTO(obj.text, StyleMapper.styleToDto(obj.style))
    }
}

@Serializable @SerialName("text_sequence")  data class TextSequenceDto(val components: List<TextDto>, override val style: StyleDto): TextDto
class TextSequenceMapper(private val registry: TextMappingRegistry): TextMapper<TextSequence, TextSequenceDto> {

    override val serializerRegistryId: SerializerRegistryId = SerializerRegistryId(TextSequenceDto::class)
    override fun fromDto(dto: TextSequenceDto): TextSequence {
        return TextSequence(
            StyleMapper.dtoToStyle(dto.style),
            dto.components.map { registry.fromDto(it) }
        )
    }

    override fun toDto(obj: TextSequence): TextSequenceDto {
        return TextSequenceDto(
            obj.children.map { registry.toDto(it) },
            StyleMapper.styleToDto(obj.style)
        )
    }
}

object StyleMapper {
    fun styleToDto(style: Style): StyleDto {
        return StyleDto(style.color.hex)
    }
    fun dtoToStyle(dto: StyleDto): Style {
        return Style(TextColor(dto.colorHex))
    }
}