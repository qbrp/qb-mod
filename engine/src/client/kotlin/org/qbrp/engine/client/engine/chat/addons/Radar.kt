package org.qbrp.engine.client.engine.chat.addons

import com.google.common.math.IntMath.pow
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.util.ActionResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
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
import kotlin.math.sqrt


@Autoload(LoadPriority.ADDON, EnvType.CLIENT)
class Radar(): ClientChatAddon("radar") {
    override fun onLoad() {
        TextUpdateCallback.EVENT.register { text, line ->
            if (line.message.getTags().getComponentData<Boolean>("radar") == true) {
                line.update(getTextWithRadar(line.message))
            } else {
                null
            }
        }
        MessageAddedEvent.EVENT.register { message, storage ->
            val player = MinecraftClient.getInstance().player
            val sourceX = message.getTags().getComponentData<Int>("sourceX") ?: return@register ActionResult.PASS
            val sourceZ = message.getTags().getComponentData<Int>("sourceZ") ?: return@register ActionResult.PASS
            if (message.authorName == MinecraftClient.getInstance().player?.name?.string
                || message.authorName == SYSTEM_MESSAGE_AUTHOR
                //|| (calculateDistance(message, player?.x?.toInt() ?: 0, player?.z?.toInt() ?: 0) > 3
                        //&& getAngle(sourceX, sourceZ) < 160
                        )
            {
                message.getTagsBuilder()
                    .placeholder("radar", "")
                    .component("radar", false)
                EngineClient.getAPI<PlaceholdersAPI>()!!.handle(message)
            }
            ActionResult.PASS
        }
    }

    private fun calculateDistance(msg: ChatMessage, x: Int, z: Int): Int {
        val sourceX = msg.getTags().getComponentData<Int>("sourceX") ?: return 0
        val sourceZ = msg.getTags().getComponentData<Int>("sourceZ") ?: return 0
        return sqrt((pow((sourceX - x), 2) + pow((sourceZ - z), 2)).toFloat()).toInt()
            .also {  }
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

    private fun getAngle(x: Int, z: Int): Double {
        val player = MinecraftClient.getInstance().player ?: return -1.0
        val targetPos = BlockPos(x, 64, z)

        val dx = targetPos.x - player.x
        val dz = targetPos.z - player.z

        val directionAngle = Math.toDegrees(atan2(-dz.toDouble(), dx.toDouble()))
        val playerYawConverted = (360 - (player.yaw + 90)) % 360

        return (playerYawConverted - directionAngle + 360) % 360
    }

    private fun getCurrentRadarSymbol(x: Int, z: Int): String {
        var angle = getAngle(x, z)
        if (angle == -1.0) return "⚠"

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