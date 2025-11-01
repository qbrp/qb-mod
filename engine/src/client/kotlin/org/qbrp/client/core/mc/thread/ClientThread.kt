package org.qbrp.client.core.mc.thread

import net.minecraft.client.MinecraftClient
import org.qbrp.main.core.fabricmc.server.LogicThread

class ClientThread(private val client: MinecraftClient) : LogicThread {
    override fun assertOnServerThread() {
        if (!client.isOnThread) throw IllegalThreadStateException()
    }

    override fun execute(runnable: Runnable) {
        client.execute(runnable)
    }
}