package org.qbrp.engine.client.system.networking

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import org.qbrp.system.networking.messaging.ReceiverContext

data class ClientReceiverContext(
    val client: MinecraftClient,
    val handler: ClientPlayNetworkHandler
): ReceiverContext