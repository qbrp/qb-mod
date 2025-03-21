package org.qbrp.core.components

import net.minecraft.server.MinecraftServer
import net.minecraft.world.World

open class CallContext(val world: World, val server: MinecraftServer) {
}