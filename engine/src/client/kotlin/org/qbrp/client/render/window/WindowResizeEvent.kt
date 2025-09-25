package org.qbrp.client.render.window

import net.minecraft.client.MinecraftClient
import org.qbrp.main.core.events.Event

data class WindowResizeEvent(val client: MinecraftClient, val window: Window): Event