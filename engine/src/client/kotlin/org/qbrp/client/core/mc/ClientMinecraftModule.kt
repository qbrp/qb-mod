package org.qbrp.client.core.mc

import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import org.koin.core.component.get
import org.koin.core.component.inject
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.client.core.mc.skin.SkinTextureManager
import org.qbrp.client.core.mc.skin.SkinTextureManagerImpl
import org.qbrp.client.core.onClientTick
import org.qbrp.client.core.onClientWorldTick
import org.qbrp.client.engine.players.ClientMinecraftEntities
import org.qbrp.client.engine.players.ClientMinecraftEntityAccess
import org.qbrp.client.engine.players.ClientPlayerList
import org.qbrp.client.engine.players.ClientPlayerManager
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.events.on
import org.qbrp.main.core.fabricmc.CameraOrientationComponent
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.EngineModule
import org.qbrp.main.engine.ModLoadedEvent

@Autoload(env = EnvType.CLIENT)
class ClientMinecraftModule : EngineModule("client-minecraft") {
    override fun getKoinModule(): Module = module {
        single<SkinTextureManager> { SkinTextureManagerImpl(MinecraftClient.getInstance().networkHandler!!) }
        single<ClientMinecraftEntities> { ClientMinecraftEntityAccess(get(), get()) }
        single { CameraOrientationSystem() }
    }

    override fun onEnable() {
        on<ModLoadedEvent>() {
            val system = get<CameraOrientationSystem>()
            val clientPlayerList = get<ClientPlayerList>()
            val clientMinecraftEntities = get<ClientMinecraftEntities>()
            onClientTick {
                system.update(clientPlayerList, clientMinecraftEntities)
            }
        }
    }
}