package org.qbrp.main.core.mc.context

import org.koin.core.context.GlobalContext
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.main.core.mc.player.permission.PermissionService
import org.qbrp.main.core.mc.player.service.MinecraftPlayerManager
import org.qbrp.main.core.mc.player.service.PlayerModeration
import org.qbrp.main.core.mc.player.service.attribute.MinecraftPlayerAttributeService
import org.qbrp.main.core.mc.server.command.ServerCommandExecutor
import org.qbrp.main.core.mc.server.thread.ServerThread
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.Priorities
import org.qbrp.main.core.modules.EngineModule

@Autoload(Priorities.LOWEST)
class MinecraftModule() : EngineModule("minecraft"), MinecraftContext {
    override fun getKoinModule(): Module = module {
        single<MinecraftContext> { this@MinecraftModule }
    }
    override val permissionService: PermissionService = getOrCrash()
    override val playerModeration: PlayerModeration = getOrCrash()
    override val playerManager: MinecraftPlayerManager = getOrCrash()
    override val serverThread: ServerThread = getOrCrash()
    override val commandExecutor: ServerCommandExecutor = getOrCrash()
    override val attributeService: MinecraftPlayerAttributeService = getOrCrash()

    inline fun <reified K : Any> getOrCrash(): K {
        return GlobalContext.get().getOrNull<K>() ?: crash(
            "Контекст Minecraft не инициализирован. Проверьте порядок загрузки модулей.",
            NullPointerException("${K::class.qualifiedName}")
        )
    }
}