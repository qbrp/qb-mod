package org.qbrp.client

import dev.felnull.specialmodelloader.api.event.SpecialModelLoaderEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.screen.ingame.HandledScreens
import org.koin.core.component.get
import org.qbrp.main.core.Core
import org.qbrp.main.ApplicationLayer
import org.qbrp.main.core.mc.registry.GameRegistries

object ClientCore: ApplicationLayer("org.qbrp.client.core") {
    fun getServerIp(): String? {
        val client = MinecraftClient.getInstance()
        val networkHandler = client.networkHandler
        return networkHandler?.connection?.address?.toString()
            ?.split("/")?.last()
            ?.split(":")?.first()
    }

    override fun initialize() {
        Core.get<GameRegistries>().enable()
        super.initialize()
    }
}