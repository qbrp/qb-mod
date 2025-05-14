package org.qbrp.engine.client.engine.contentpacks.resourcepack

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.resource.ResourceManagerHelper
import net.fabricmc.fabric.api.resource.ResourcePackActivationType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import net.minecraft.util.Identifier
import org.qbrp.core.Core
import org.qbrp.core.assets.FileSystem
import org.qbrp.core.game.IDGenerator
import org.qbrp.engine.Engine
import org.qbrp.engine.client.EngineClient
import org.qbrp.engine.client.engine.contentpacks.ServerContentPackEvents
import org.qbrp.engine.client.engine.contentpacks.ServerPacksAPI
import org.qbrp.engine.client.system.ClientModule
import org.qbrp.system.modules.Autoload
import org.qbrp.system.modules.LoadPriority
import org.qbrp.system.modules.QbModule
import java.util.prefs.Preferences

@Autoload(LoadPriority.LOWEST, EnvType.CLIENT)
class ResourcePackApplier: ClientModule("resourcepack-applier") {
    init {
        dependsOn { EngineClient.isApiAvailable<ServerPacksAPI>() }
        allowDynamicActivation()
    }
    val prefs = Preferences.userNodeForPackage(ResourcePackApplier::class.java)

    override fun onEnable() {
        val rpm = MinecraftClient.getInstance().resourcePackManager
        val resPacks = FileSystem.MINECRAFT_RESOURCEPACKS
        once {
            ServerContentPackEvents.ON_APPLY.register { ifEnabled {
                val profile = it.resourcePackProfile
                if (profile == null || getLastContentPackVersion() != it.version) {
                    it.resourcePack.copyRecursively(resPacks.resolve("qbrp-pack"), true)
                    rpm.enable(it.name)
                    MinecraftClient.getInstance().reloadResources()
                    setLastContentPackVersion(it.version)
                }
            }}
        }
    }

    fun getLastContentPackVersion() = prefs.get("pack-version", "NONE")
    fun setLastContentPackVersion(ver: String) = prefs.put("pack-version", ver)
}