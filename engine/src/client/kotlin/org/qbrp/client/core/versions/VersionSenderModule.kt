package org.qbrp.client.core.versions

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import org.qbrp.client.core.networking.ClientNetworkUtil
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.QbModule
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.Messages
import org.qbrp.main.core.utils.networking.messages.types.StringContent
import org.qbrp.main.core.versions.VersionsUtil

@Autoload(env = EnvType.CLIENT)
class VersionSenderModule: QbModule("version-sender") {

    override fun onEnable() {
        ClientPlayConnectionEvents.JOIN.register { handler, sender, client ->
            ClientNetworkUtil.sendMessage(
                Message(
                    Messages.HANDLE_VERSION,
                    StringContent(VersionsUtil.getVersion().toString())
                )
            )
        }
    }
}