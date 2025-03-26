package org.qbrp.engine.chat.addons

import net.minecraft.block.Block
import net.minecraft.block.Blocks
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.koin.core.component.get
import org.qbrp.core.resources.data.config.ConfigUpdateCallback
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.addons.records.RecordsAPI
import org.qbrp.engine.chat.addons.records.Replica
import org.qbrp.engine.chat.addons.tools.MessageFormatTools
import org.qbrp.engine.chat.addons.tools.MessageTextTools
import org.qbrp.engine.chat.core.events.MessageReceivedEvent
import org.qbrp.engine.chat.core.events.MessageSendEvent
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.networking.messages.types.IntContent
import org.qbrp.system.utils.Tracer
import org.qbrp.system.utils.log.Loggers
import kotlin.math.max
import kotlin.random.Random

@Autoload(LoadPriority.ADDON)
class Volume(): ChatAddon("volume") {
    private var config: ServerConfigData.Chat.Volume = get<ServerConfigData.Chat>().volume
    companion object {
        val BLOCK_VOLUMES = mutableMapOf<Block, Double>(
            Blocks.AIR to 0.0
        )
        val TAGS_VOLUMES = mutableMapOf<TagKey<Block>, Double>()
        var VOLUME_LEVELS = listOf<ServerConfigData.Chat.Volume.VolumeLevel>()
        val logger = Loggers.get("chat", "volume")
    }

    private fun loadConfig() {
        for ((blockName, volume) in config.blockVolumes) {
            val id = Identifier(blockName)

            if (Registries.BLOCK.containsId(id)) {
                val block = Registries.BLOCK.get(id)
                BLOCK_VOLUMES[block] = volume
            } else {
                logger.warn("Блок '$blockName' не найден в реестре!")
            }
        }

        for ((tagName, volume) in config.tagVolumes) {
            val tagKey = TagKey.of(RegistryKeys.BLOCK, Identifier(tagName))
            TAGS_VOLUMES[tagKey] = volume
        }

        VOLUME_LEVELS = config.volumeLevels
    }

    fun getBlockVolumeModifier(blockPos: BlockPos, world: World): Double {
        val blockState = world.getBlockState(blockPos)
        val block = blockState.block

        BLOCK_VOLUMES[block]?.let { return it }

        for ((tag, value) in TAGS_VOLUMES) {
            if (blockState.isIn(tag)) return value
        }

        return config.defaultVolumeBlockModifier
    }

    override fun load() {
        loadConfig()
        ConfigUpdateCallback.EVENT.register { config ->
            this.config = config.chat.volume
            loadConfig()
        }

        // Первичная обработка
        MessageReceivedEvent.EVENT.register { message ->
            if (message.getTags().getComponentData<Boolean>("ignoreVolume") == true) { return@register ActionResult.PASS }
            val author = message.getAuthorEntity() ?: return@register ActionResult.PASS
            val pos = author.blockPos
            var volume = message.getTags().getComponentData<Int>("volume") ?: 0
            var charsHandled = 0
            val text = MessageTextTools.getTextContent(message)

            fun charIsPrefix(char: Char): Boolean = config.volumePrefixes.contains(char.toString())

            var i = 0
            while (i < text.length && charIsPrefix(text[i])) {
                volume += config.volumePrefixes[text[i].toString()] ?: 0
                charsHandled++
                i++
            }

            MessageTextTools.setTextContent(message, text.substring(charsHandled, text.length))
            message.apply {
                setTags(getTagsBuilder()
                    .component("sourceX", IntContent(pos.x))
                    .component("sourceY", IntContent(pos.y))
                    .component("sourceZ", IntContent(pos.z))
                    .component("volume", IntContent(config.defaultVolume + volume)))
            }
            Engine.getAPI<RecordsAPI>()!!.addLine(
                message,
                Replica(message.authorName, MessageTextTools.getTextContent(message), volume)
            )

            ActionResult.PASS
        }

        // Изменение Volume
        MessageSendEvent.EVENT.register { sender, message, receiver, networking ->
            val tags = message.getTags()
            if (tags.getComponentData<Boolean>("ignoreVolume") == true) { return@register ActionResult.PASS }
            val volume = tags.getComponentData<Int>("volume") ?: return@register ActionResult.PASS
            val sourceX = tags.getComponentData<Int>("sourceX") ?: return@register ActionResult.PASS
            val sourceY = tags.getComponentData<Int>("sourceY") ?: return@register ActionResult.PASS
            val sourceZ = tags.getComponentData<Int>("sourceZ") ?: return@register ActionResult.PASS

            var volumeEdit = 0.0
            Tracer.tracePathAndModify(Vec3d(sourceX.toDouble(), sourceY.toDouble(), sourceZ.toDouble()), Vec3d(receiver.x, receiver.y, receiver.z)) { pos ->
                volumeEdit -= getBlockVolumeModifier(pos, receiver.world) + 0.1
                true
            }
            val newVolume = (volume + volumeEdit).toInt()
            if (newVolume < 0) return@register ActionResult.FAIL
            message.apply {
                setTags(getTagsBuilder()
                    .component("volume", IntContent(newVolume))
                    .component("volumeHandled", true))
            }
            val handledMessageText = if (tags.getComponentData<Boolean>("format") != false) format(newVolume, message,tags.getComponentData<Boolean>("distort") != false)
            else MessageTextTools.getTextContent(message)

            MessageTextTools.setTextContent(message, handledMessageText)
            ActionResult.PASS
        }
    }

    private fun format(level: Int, message: ChatMessage, distort: Boolean): String {
        val text = MessageTextTools.getTextContent(message)
        var formatSymbol = "&f"
        if (level < VOLUME_LEVELS.first().value) {
            formatSymbol = config.formatMinVolume
        } else {
            VOLUME_LEVELS.forEach {
                if (level >= it.value) {
                    formatSymbol =  it.formatText
                    message.getTagsBuilder()
                        .placeholder("formatSep", it.formatSep)
                }
            }
        }

        var processedMessage = text
        if (level <= config.distortionLevel) {
            val distortionStrength = max(((config.distortionLevel - level).toDouble() / config.distortionLevel * config.maxDistortion).toInt(), config.minDistortion)
            if (distort) processedMessage = distort(processedMessage, distortionStrength)
        }

        return MessageFormatTools.addFormatting(processedMessage, formatSymbol)
    }

    private fun distort(message: String, strength: Int): String {
        val artifacts = config.artifacts
        val result = StringBuilder()
        var i = 0
        while (i < message.length) {
            val c = message[i]
            if (c.isLetterOrDigit() && Random.nextInt(100) < strength) {
                // Случайно выбираем: заменить символ на артефакт или поменять местами с последующим символом
                if (Random.nextBoolean() && i < message.lastIndex) {
                    // Меняем местами текущий символ с следующим
                    result.append(message[i + 1])
                    result.append(c)
                    i += 2
                    continue
                } else {
                    // Заменяем символ на случайный артефакт
                    result.append(artifacts.random())
                    i++
                    continue
                }
            } else {
                result.append(c)
                i++
            }
        }
        return result.toString()
    }
}