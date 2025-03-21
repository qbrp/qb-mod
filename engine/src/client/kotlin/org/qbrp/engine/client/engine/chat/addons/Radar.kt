package org.qbrp.engine.client.engine.chat.addons

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import org.koin.core.component.get
import org.qbrp.engine.chat.ChatAddon
import org.qbrp.engine.chat.ChatModule.Companion.SYSTEM_MESSAGE_AUTHOR
import org.qbrp.engine.chat.addons.placeholders.Placeholders
import org.qbrp.engine.chat.addons.placeholders.PlaceholdersAPI
import org.qbrp.engine.chat.core.messages.ChatMessage
import org.qbrp.engine.client.EngineClient
import org.qbrp.engine.client.engine.chat.ClientChatAddon
import org.qbrp.engine.client.engine.chat.system.MessageStorage
import org.qbrp.engine.client.engine.chat.system.events.MessageAddedEvent
import org.qbrp.engine.client.engine.chat.system.events.TextUpdateCallback
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import kotlin.math.atan2


@Autoload(LoadPriority.ADDON, EnvType.CLIENT)
class Radar(): ClientChatAddon("radar") {
    override fun load() {
        TextUpdateCallback.EVENT.register { text, line ->
            if (line.message.getTags().getComponentData<Boolean>("radar") == true) {
                line.update(getTextWithRadar(line.message))
            } else {
                null
            }
        }
        MessageAddedEvent.EVENT.register { message, storage ->
            if (message.authorName == MinecraftClient.getInstance().player?.name?.string
                || message.authorName == SYSTEM_MESSAGE_AUTHOR
            ) {
                message.getTagsBuilder()
                    .placeholder("radar", "")
                EngineClient.getAPI<PlaceholdersAPI>()!!.handle(message)
            }
            ActionResult.PASS
        }
    }

    private fun getTextWithRadar(msg: ChatMessage): String {
        val sourceX = msg.getTags().getComponentData<Int>("sourceX") ?: return ""
        val sourceZ = msg.getTags().getComponentData<Int>("sourceZ") ?: return ""
        val symbol = getCurrentRadarSymbol(x = sourceX, z = sourceZ)
        val timeElapsed = System.currentTimeMillis() - msg.timestamp
        if (msg.getTags().getComponentData<Boolean>("radar") == true && timeElapsed < 20_000) {
            return msg.getText().replace("{radar}", "<gray>($symbol)</gray>")
        } else {
            return msg.getText().replace("{radar}", "")
        }

    }


    private fun getCurrentRadarSymbol(x: Int, z: Int): String {
        val player = MinecraftClient.getInstance().player ?: return "⚠"
        val targetPos = BlockPos(x, 64, z)

        val dx = targetPos.x - player.x
        val dz = targetPos.z - player.z

        val directionAngle = Math.toDegrees(atan2(-dz.toDouble(), dx.toDouble()))
        val playerYawConverted = (360 - (player.yaw + 90)) % 360

        var angle = (playerYawConverted - directionAngle + 360) % 360

        return when {
            angle < 22.5 -> "↑"  // Север
            angle < 67.5 -> "↗" // Северо-восток
            angle < 112.5 -> "→" // Восток
            angle < 157.5 -> "↘" // Юго-восток
            angle < 202.5 -> "↓" // Юг
            angle < 247.5 -> "↙" // Юго-запад
            angle < 292.5 -> "←" // Запад
            angle < 337.5 -> "↖" // Северо-запад
            else -> "↑"          // Север (замыкающий случай)
        }
    }
}