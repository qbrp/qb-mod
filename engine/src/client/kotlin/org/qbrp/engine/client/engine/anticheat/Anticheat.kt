package org.qbrp.engine.client.engine.anticheat

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import org.qbrp.engine.anticheat.ModIdListContent
import org.qbrp.engine.client.system.networking.ClientNetworkManager
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.QbModule
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages

@Autoload(env = EnvType.CLIENT)
class Anticheat: QbModule("anticheat") {
    override fun load() {
        ClientPlayConnectionEvents.JOIN.register { handler, sender, client ->
            ClientNetworkManager.sendMessage(
                Message(Messages.MOD_IDS, ModIdListContent().apply {
                    list = FabricLoader.getInstance().allMods.map { it.metadata.id } }
                )
            )
        }
    }

}