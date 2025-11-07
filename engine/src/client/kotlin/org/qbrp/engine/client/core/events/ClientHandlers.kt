package org.qbrp.engine.client.core.events

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.ActionResult
import org.qbrp.core.game.player.PlayerManager.getPlayerSession
import org.qbrp.engine.client.EngineClient
import org.qbrp.engine.client.system.networking.ClientNetworkManager
import org.qbrp.system.VersionChecker
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages
import org.qbrp.system.networking.messages.components.ClusterBuilder
import org.qbrp.system.networking.messages.types.StringContent
import org.qbrp.system.networking.messaging.NetworkManager

fun registerClientEvents() {
    ClientPlayConnectionEvents.DISCONNECT.register { _, _ ->
        EngineClient.keybindsManager.clearHiddenKeyBindings()
    }
    ClientPlayConnectionEvents.JOIN.register { handler, sender, client ->
        ClientNetworkManager.sendMessage(
            Message(Messages.HANDLE_VERSION, StringContent(VersionChecker.getVersionObject().toString()))
        )
    }
    UseEntityCallback.EVENT.register { player, world, hand, entity, hitResult ->
        val e = hitResult?.entity
        if (e as? PlayerEntity != null) {
            ClientNetworkManager.sendMessage(
                Message(
                    Messages.INTERACTION_PLAYER,
                    StringContent()
                        .apply { setData(e.uuid) }
                )
            )
        }

        ActionResult.PASS
    }
}