package org.qbrp.engine.chat.addons.volume

import net.minecraft.block.Blocks
import net.minecraft.util.ActionResult
import net.minecraft.util.math.Vec3d
import org.koin.core.component.get
import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.engine.Engine
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.addons.records.RecordsAPI
import org.qbrp.engine.chat.addons.records.Replica
import org.qbrp.engine.chat.addons.tools.MessageTextTools
import org.qbrp.engine.chat.core.events.MessageReceivedEvent
import org.qbrp.engine.chat.core.events.MessageSendEvent
import org.qbrp.engine.chat.core.events.MessageSenderPipeline
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.networking.messages.types.IntContent
import kotlin.ranges.contains
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket
import net.minecraft.particle.ParticleType
import net.minecraft.particle.ParticleTypes
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos
import org.qbrp.system.utils.Tracer


//@Autoload(LoadPriority.ADDON)
class Volume2: ChatAddon("volume2") {
    private lateinit var config: ServerConfigData.Chat.Volume

    override fun load() {
        config = get<ServerConfigData>().chat.volume
        MessageReceivedEvent.EVENT.register { message ->
            if (message.getTags().getComponentData<Boolean>("ignoreVolume") == true) {
                return@register ActionResult.PASS
            }
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
                setTags(
                    getTagsBuilder()
                        .component("sourceX", IntContent(pos.x))
                        .component("sourceY", IntContent(pos.y))
                        .component("sourceZ", IntContent(pos.z))
                        .component("volume", IntContent(config.defaultVolume + volume))
                )
            }
            Engine.getAPI<RecordsAPI>()!!.addLine(
                message,
                Replica(message.authorName, message.getText(), volume)
            )

            ActionResult.PASS
        }

        MessageSenderPipeline.EVENT.register { message, sender ->
            val tags = message.getTags()
            if (tags.getComponentData<Boolean>("ignoreVolume") == true) {
                return@register ActionResult.PASS
            }
            val volume = tags.getComponentData<Int>("volume") ?: return@register ActionResult.PASS
            val sourceX = tags.getComponentData<Int>("sourceX") ?: return@register ActionResult.PASS
            val sourceY = tags.getComponentData<Int>("sourceY") ?: return@register ActionResult.PASS
            val sourceZ = tags.getComponentData<Int>("sourceZ") ?: return@register ActionResult.PASS

            val author = message.getAuthorEntity() ?: return@register ActionResult.PASS

            VectorLauncher().launchVectors3D(Vec3d(sourceX.toDouble(), sourceY.toDouble(), sourceZ.toDouble()), -1.0, 3, author.rotationVecClient) {
                if (author.world.getBlockState(it) != Blocks.AIR) {
                    author.server.playerManager.playerList.forEach {
                        Tracer.tracePathAndModify(Vec3d(it.x, it.y, it.z), it.pos) {
                            sendParticlePacket(author.world as ServerWorld, it, 2, 0.05, 0.05, 0.05, 0.001)
                            true
                        }
                    }
                    sendParticlePacket(author.world as ServerWorld, it, 2, 0.05, 0.05, 0.05, 0.001)
                }
                true
            }

            ActionResult.SUCCESS
        }
    }

    // Функция для отправки пакета частиц
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