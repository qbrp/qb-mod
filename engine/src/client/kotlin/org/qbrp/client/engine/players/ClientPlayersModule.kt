package org.qbrp.client.engine.players

import net.minecraft.client.MinecraftClient
import org.koin.core.component.get
import org.qbrp.client.core.synchronization.ClientLocalMessageSender
import org.qbrp.client.core.synchronization.ObjectSynchronizeChannel
import org.qbrp.main.core.mc.player.PlayersModule
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.networking.ClientReceiver
import org.qbrp.main.core.utils.networking.messages.Messages
import org.qbrp.main.core.utils.networking.messages.types.StringContent
import org.qbrp.main.engine.items.ItemsModule

//@Autoload(env = EnvType.CLIENT)
class ClientPlayersModule: QbModule("client-players") {
    override fun getKoinModule() = inner {
        scoped { CPlayerStorage() }
        scoped { ClientLocalMessageSender(ItemsModule.ITEMS_MESSAGING_CHANNEL) }
    }

    val storage: CPlayerStorage get() = getLocal()

    override fun onEnable() {
        ObjectSynchronizeChannel<ClientPlayerObject>(PlayersModule.PLAYERS_CHANNEL, storage) { cluster, id ->
            val playerName = cluster.getComponentData<String>("playerName")!!
            ClientPlayerObject(
                MinecraftClient.getInstance().world?.getPlayers()?.find { it.name.string == playerName },
                playerName,
                getLocal()
            ).apply { state.putObjectAndEnableBehaviours(this) }
        }.apply {
        }

        // Заменить на CommandInvoker-компонент
        ClientReceiver(Messages.INVOKE_COMMAND, StringContent::class) { message, context, receiver ->
            MinecraftClient.getInstance().player?.networkHandler?.sendChatCommand(message.getContent())
            true
        }.register()
    }
}