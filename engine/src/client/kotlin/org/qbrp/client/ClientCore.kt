package org.qbrp.client

import dev.felnull.specialmodelloader.api.event.SpecialModelLoaderEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreens
import org.qbrp.main.core.Core
import org.qbrp.main.ApplicationLayer

object ClientCore: ApplicationLayer("org.qbrp.client.core") {
    fun getServerIp(): String? {
        val client = MinecraftClient.getInstance()
        val networkHandler = client.networkHandler
        return networkHandler?.connection?.address?.toString()
            ?.split("/")?.last()
            ?.split(":")?.first()
    }

    override fun initialize() {
        super.initialize()
    }
}