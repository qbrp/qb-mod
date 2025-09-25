package org.qbrp.client.core.mc

import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.client.core.mc.command.ClientCommandExecutor
import org.qbrp.client.core.mc.skin.SkinTextureManager
import org.qbrp.client.core.mc.skin.SkinTextureManagerImpl
import org.qbrp.client.core.networking.v2.ClientContext
import org.qbrp.main.core.mc.server.ServerModule
import org.qbrp.main.core.mc.server.command.CommandExecutePacket
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.core.networking.PacketListenerActivator

@Autoload(env = EnvType.CLIENT)
class ClientMinecraftModule: EngineModule("client-minecraft") {
    override fun getKoinModule(): Module = module {
        single { ClientCommandExecutor(MinecraftClient.getInstance()) }
        single<SkinTextureManager> { SkinTextureManagerImpl(MinecraftClient.getInstance().networkHandler!!) }
    }

    override fun onEnable() {
        get<PacketListenerActivator<ClientContext>>().run(
            ServerModule.COMMAND_EXECUTE_CHANNEL,
            get<ClientCommandExecutor>(),
            { buf, ctx -> CommandExecutePacket(buf) }
        )
    }
}