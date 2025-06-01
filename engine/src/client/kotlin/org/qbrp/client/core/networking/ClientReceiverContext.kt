package org.qbrp.client.core.networking

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import org.qbrp.main.core.utils.networking.messaging.ReceiverContext

data class ClientReceiverContext(
    val client: MinecraftClient,
    val handler: ClientPlayNetworkHandler
): ReceiverContext