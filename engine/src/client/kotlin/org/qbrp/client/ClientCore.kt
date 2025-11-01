package org.qbrp.client

import net.minecraft.client.MinecraftClient
import org.koin.core.component.get
import org.qbrp.client.core.mc.registerClientItems
import org.qbrp.main.core.Core
import org.qbrp.main.ApplicationLayer

object ClientCore : ApplicationLayer("org.qbrp.client.core") {
    fun getServerIp(): String? {
        val client = MinecraftClient.getInstance()
        val networkHandler = client.networkHandler
        return networkHandler?.connection?.address?.toString()
            ?.split("/")?.last()
            ?.split(":")?.first()
    }

    override fun initialize() {
        registerClientItems()
        super.initialize()
    }
}