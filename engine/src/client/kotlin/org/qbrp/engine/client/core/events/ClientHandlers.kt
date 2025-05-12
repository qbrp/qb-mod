package org.qbrp.engine.client.core.events

import dev.felnull.specialmodelloader.api.SpecialModelLoaderAPI
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.util.ModelIdentifier
import net.minecraft.util.Identifier
import org.qbrp.engine.client.EngineClient
import org.qbrp.engine.client.system.networking.ClientNetworkManager
import org.qbrp.system.VersionChecker
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages
import org.qbrp.system.networking.messages.types.StringContent

object ClientHandlers {
    val client = MinecraftClient.getInstance()
    val player = client.player

    fun registerEvents() {
        ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
            EngineClient.keybindsManager.clearHiddenKeyBindings()
        }
        ClientPlayConnectionEvents.JOIN.register { handler, sender, client ->
            ClientNetworkManager.sendMessage(
                Message(Messages.HANDLE_VERSION, StringContent(VersionChecker.getVersionObject().toString()))
            )
            EngineClient.registrationManager.autoLogin()
        }
    }
}