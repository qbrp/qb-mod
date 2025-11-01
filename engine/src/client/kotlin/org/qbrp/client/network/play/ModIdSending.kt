package org.qbrp.client.network.play

import net.fabricmc.loader.api.FabricLoader
import org.koin.core.component.KoinComponent
import org.qbrp.client.network.transport.registerClientReceiver
import org.qbrp.client.network.transport.sendC2SPacket
import org.qbrp.main.network.player.MOD_IDS_ENDPOINT
import org.qbrp.main.network.player.ModIdsPacket

fun sendModIds() {
    MOD_IDS_ENDPOINT
        .sendC2SPacket(ModIdsPacket(FabricLoader.getInstance().allMods.map { it.metadata.id }))
}