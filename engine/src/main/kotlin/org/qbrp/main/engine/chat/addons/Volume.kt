package org.qbrp.main.engine.chat.addons

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.block.Block
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket
import net.minecraft.particle.ParticleTypes
import net.minecraft.registry.Registries
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.ActionResult
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.world.World
import org.qbrp.main.core.mc.commands.CommandsModule
import org.qbrp.main.core.mc.commands.CommandRegistryEntry
import org.qbrp.deprecated.resources.data.config.ConfigInitializationCallback
import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.main.engine.chat.ChatAddon
import org.qbrp.main.engine.chat.addons.tools.MessageFormatTools
import org.qbrp.main.engine.chat.addons.tools.MessageTextTools
import org.qbrp.main.engine.chat.core.events.MessageReceivedEvent
import org.qbrp.main.engine.chat.core.events.MessageSendEvent
import org.qbrp.main.engine.chat.core.messages.ChatMessage
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.utils.networking.messages.types.IntContent
import org.qbrp.main.core.utils.Tracer
import org.qbrp.main.core.utils.format.Format.asMiniMessage
import org.qbrp.main.core.utils.log.LoggerUtil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.sin
import kotlin.random.Random
import org.koin.core.component.get
import org.qbrp.main.core.mc.commands.CommandsAPI

@Autoload(LoadPriority.ADDON)
class Volume(): ChatAddon("volume"), CommandRegistryEntry {
    private var config: ServerConfigData.Chat.Volume = getLocal<ServerConfigData>().chat.volume
    private var overhearPlayers: MutableMap<ServerPlayerEntity, Boolean> = mutableMapOf()

    companion object {
        val BLOCK_VOLUMES = mutableMapOf<Block, Double>()
        val TAGS_VOLUMES = mutableMapOf<TagKey<Block>, Double>()
        val logger = LoggerUtil.get("chat", "volume")
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

    fun findOffsetOrigins(
        origin: Vec3d,
        world: World
    ): List<Vec3d> {
        val positions = mutableListOf<Vec3d>()
        val step = 1.0
        val maxOffset = config.maxOffset
        val dirCount = config.vectorCount

        // 12 направлений по окружности
        val angles = (0 until dirCount).map { it * 2.0 * Math.PI / dirCount }
        val dirs = angles.map { ang -> Vec3d(-sin(ang), 0.0, cos(ang)) }
        val yLevels = listOf(-1.0, 0.0, 1.0)

        yLevels.forEach { yOff ->
            dirs.forEach { dir ->
                var start = Vec3d(origin.x, origin.y + yOff, origin.z)
                var dist = 0.0
                while (dist < maxOffset) {
                    val next = start.add(dir.multiply(step))
                    val bp = BlockPos(
                        floor(next.x).toInt(),
                        floor(next.y).toInt(),
                        floor(next.z).toInt()
                    )
                    val mod = getBlockVolumeModifier(bp, world)
                    if (mod > config.stopVectorVolumeModifier) {
                        positions.add(start)
                        return@forEach
                    }
                    start = next
                    dist += step
                }
                // если не встретили - добавляем последнюю точку
                positions.add(start)
            }
        }

        return positions
    }

    // Не нужно. Метод весьма кривой
    fun generateSphereDirections(samples: Int): List<Vec3d> {
        val directions = mutableListOf<Vec3d>()
        val goldenAngle = Math.PI * (3 - Math.sqrt(5.0))

        for (i in 0 until samples) {
            val y = 1 - (i.toDouble() / (samples - 1)) * 2
            val radius = Math.sqrt(1 - y * y)
            val theta = goldenAngle * i

            val x = Math.cos(theta) * radius
            val z = Math.sin(theta) * radius

            directions.add(Vec3d(x, y, z))
        }

        return directions
    }

    override fun onLoad() {
        loadConfig()
        ConfigInitializationCallback.EVENT.register { config ->
            this.config = config.chat.volume
            loadConfig()
        }
        get<CommandsAPI>().add(this)

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
            ActionResult.PASS
        }

        // Изменение Volume
        MessageSendEvent.register { sender, message, receiver, networking ->
            val tags = message.getTags()
            if (tags.getComponentData<Boolean>("handleVolume") != true) {
                return@register ActionResult.PASS
            }

            val volume = tags.getComponentData<Int>("volume") ?: return@register ActionResult.PASS
            val sourceX = tags.getComponentData<Int>("sourceX") ?: return@register ActionResult.PASS
            val sourceY = tags.getComponentData<Int>("sourceY") ?: return@register ActionResult.PASS
            val sourceZ = tags.getComponentData<Int>("sourceZ") ?: return@register ActionResult.PASS

            val originBase = Vec3d(sourceX.toDouble(), sourceY.toDouble(), sourceZ.toDouble())
            val target     = Vec3d(receiver.x, receiver.y, receiver.z)

            val offsetOrigins = findOffsetOrigins(originBase, receiver.world)

            // Для каждого направления: сначала находим “продвинутый” origin, потом запускаем трассировку
            val volumeEdits = (offsetOrigins + originBase).map { dir ->
                if (config.debug) sendParticlePacket(
                    world    = receiver.world as ServerWorld,
                    pos      = BlockPos(dir.x.toInt(), dir.y.toInt(), dir.z.toInt()),
                    count    = 1,
                    offsetX  = 0.0,
                    offsetY  = 0.0,
                    offsetZ  = 0.0,
                    speed    = 0.0
                )
                // 2) собственно считаем спад громкости
                var edit = 0.0
                Tracer.tracePathAndModify(dir, target) { pos ->
                    edit -= getBlockVolumeModifier(pos, receiver.world) + 0.1
                    true
                }
                edit
            }
            // Берём максимальное значение

            val maxCaseReduction = volumeEdits.max()
            val newVolume = (volume + maxCaseReduction).toInt()

            // Проверяем порог слышимости
            val minAllowed = if (overhearPlayers[receiver] == true) config.minOverhearVolume else config.minVolume
            if (newVolume <= minAllowed) {
                return@register ActionResult.FAIL
            }

            // Записываем новый уровень громкости в теги
            message.apply {
                setTags(getTagsBuilder()
                    .component("volume", IntContent(newVolume))
                    .component("volumeHandled", true))
            }

            ActionResult.PASS
        }

        // Форматирование
        MessageSendEvent.register() { sender, message, receiver, networking ->
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

        return MessageFormatTools.addFormatting(message, formatSymbol)
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

    fun sendParticlePacket(world: ServerWorld, pos: BlockPos, count: Int, offsetX: Double, offsetY: Double, offsetZ: Double, speed: Double) {
        val packet = ParticleS2CPacket(
            ParticleTypes.SMOKE,          // Тип частицы, например, ParticleTypes.SMOKE
            false,                 // Долгоживущая частица (false для обычных)
            pos.x + 0.5,           // X координата (центр блока)
            pos.y + 0.5,           // Y координата (центр блока)
            pos.z + 0.5,           // Z координата (центр блока)
            offsetX.toFloat(),               // Смещение по X
            offsetY.toFloat(),               // Смещение по Y
            offsetZ.toFloat(),               // Смещение по Z
            speed.toFloat(),                 // Скорость движения частиц
            count                  // Количество частиц
        )

        // Отправка пакета всем игрокам в мире
        world.players.forEach { player ->
            player.networkHandler.sendPacket(packet)
        }
    }
}