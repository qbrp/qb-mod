package org.qbrp.client.core.networking.v1

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import org.qbrp.main.core.util.networking.messaging.ReceiverContext

data class ClientReceiverContext(
    val client: MinecraftClient,
    val handler: ClientPlayNetworkHandler
): ReceiverContext