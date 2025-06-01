package org.qbrp.client.engine.anticheat

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.loader.api.FabricLoader
import org.qbrp.main.engine.anticheat.ModIdListContent
import org.qbrp.client.core.networking.ClientNetworkUtil
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.Messages

@Autoload(env = EnvType.CLIENT)
class Anticheat: QbModule("anticheat") {
    override fun onLoad() {
        ClientPlayConnectionEvents.JOIN.register { handler, sender, client ->
            ClientNetworkUtil.sendMessage(
                Message(Messages.MOD_IDS, ModIdListContent().apply {
                    list = FabricLoader.getInstance().allMods.map { it.metadata.id } }
                )
            )
        }
    }

}