package org.qbrp.client.core.networking.v2

import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler
import org.qbrp.main.core.networking.NetworkContext

class ClientContext(val client: MinecraftClient, val handler: ClientPlayNetworkHandler): NetworkContext