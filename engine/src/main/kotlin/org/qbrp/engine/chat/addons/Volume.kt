package org.qbrp.engine.chat.addons

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.block.Block
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.koin.core.component.get
import org.qbrp.core.game.registry.CommandsRepository
import org.qbrp.core.game.registry.ServerModCommand
import org.qbrp.core.resources.data.config.ConfigInitializationCallback
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
import org.qbrp.system.utils.format.Format.asMiniMessage
import org.qbrp.system.utils.log.Loggers
import kotlin.math.max
import kotlin.random.Random

@Autoload(LoadPriority.ADDON)
class Volume(): ChatAddon("volume"), ServerModCommand {
    private var config: ServerConfigData.Chat.Volume = get<ServerConfigData.Chat>().volume
    private var overhearPlayers: MutableMap<ServerPlayerEntity, Boolean> = mutableMapOf()

    companion object {
        val BLOCK_VOLUMES = mutableMapOf<Block, Double>()
        val TAGS_VOLUMES = mutableMapOf<TagKey<Block>, Double>()
        val logger = Loggers.get("chat", "volume")
    }

    override fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(CommandManager.literal("overhear")
            .executes() { ctx ->
                val player = ctx.source.player ?: return@executes 0
                val currentValue = overhearPlayers.getOrPut(player) { false }
                overhearPlayers[player] = !currentValue
                ctx.source.sendMessage("<gray>Режим подслушивания ${if (currentValue) "выключен" else "включен"}.".asMiniMessage())
                1
            })
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
        ConfigInitializationCallback.EVENT.register { config ->
            this.config = config.chat.volume
            loadConfig()
        }
        CommandsRepository.add(this)

        // Первичная обработка
        MessageReceivedEvent.EVENT.register { message ->
            if (message.getTags().getComponentData<Boolean>("handleVolume") != true) { return@register ActionResult.PASS }
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
                    .placeholder("formatSep", config.getVolumeLevelFor(config.defaultVolume + volume).formatSep)
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
            if (tags.getComponentData<Boolean>("handleVolume") != true) { return@register ActionResult.PASS }
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

            if (newVolume <= (if (overhearPlayers[receiver] == true) config.minOverhearVolume else config.minVolume)) return@register ActionResult.FAIL

            message.apply {
                setTags(getTagsBuilder()
                    .component("volume", IntContent(newVolume))
                    .component("volumeHandled", true))
            }

            if (message.getAuthorEntity() == receiver) {
                log(message)
                message.getTagsBuilder()
                    .component("log", true)
            }
            ActionResult.PASS
        }

        // Форматирование
        MessageSendEvent.EVENT.register() { sender, message, receiver, networking ->
            val tags = message.getTags()
            if (tags.getComponentData<Boolean>("format") != true) { return@register ActionResult.PASS }
            val volume = tags.getComponentData<Int>("volume") ?: return@register ActionResult.PASS

            val handledMessageText = format(volume, message,tags.getComponentData<Boolean>("distort") != false)

            MessageTextTools.setTextContent(message, handledMessageText)
            ActionResult.PASS
        }
    }

    private fun log(message: ChatMessage) {
        logger.log(MessageTextTools.getTextContent(message))
    }

    private fun format(level: Int, message: ChatMessage, distort: Boolean): String {
        val text = MessageTextTools.getTextContent(message)
        val volumeLevel = config.getVolumeLevelFor(level)
        var formatSymbol = volumeLevel.formatText

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
        var ignore = false
        while (i < message.length) {
            val c = message[i]
            if (c == '{') ignore = true
            if (c == '*') { ignore = !ignore }
            if (c == '}') ignore = false
            if (c.isLetter() && Random.nextInt(100) < strength && !ignore) {
                if (Random.nextBoolean() && i < message.lastIndex) {
                    result.append(message[i + 1])
                    result.append(c)
                    i += 2
                    continue
                } else {
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