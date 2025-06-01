package org.qbrp.client.engine.players

import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import org.qbrp.client.core.networking.ClientReceiverContext
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.networking.ClientReceiver
import org.qbrp.main.core.utils.networking.messages.Messages
import org.qbrp.main.core.utils.networking.messages.types.StringContent

@Autoload(env = EnvType.CLIENT)
class ClientPlayersModule: QbModule("client-players") {

    override fun onEnable() {
        ClientReceiver<ClientReceiverContext>(Messages.INVOKE_COMMAND, StringContent::class) { message, context, receiver ->
            MinecraftClient.getInstance().player?.networkHandler?.sendChatCommand(message.getContent())
            true
        }.register()
    }
}