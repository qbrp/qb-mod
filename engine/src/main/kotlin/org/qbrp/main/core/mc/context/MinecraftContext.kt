package org.qbrp.main.core.mc.context

import org.qbrp.main.core.mc.player.permission.PermissionService
import org.qbrp.main.core.mc.player.service.MinecraftPlayerManager
import org.qbrp.main.core.mc.player.service.PlayerModeration
import org.qbrp.main.core.mc.player.service.attribute.MinecraftPlayerAttributeService
import org.qbrp.main.core.mc.player.service.snapshot.MinecraftPlayerSnapshotService
import org.qbrp.main.core.mc.server.command.ServerCommandExecutor
import org.qbrp.main.core.mc.server.thread.ServerThread

interface MinecraftContext {
    val playerModeration: PlayerModeration
    val permissionService: PermissionService
    val playerManager: MinecraftPlayerManager
    val serverThread: ServerThread
    val commandExecutor: ServerCommandExecutor
    val attributeService: MinecraftPlayerAttributeService
}