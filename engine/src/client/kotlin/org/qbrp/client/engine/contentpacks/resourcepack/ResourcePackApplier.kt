package org.qbrp.client.engine.contentpacks.resourcepack

import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import org.qbrp.client.ClientCore
import org.qbrp.client.engine.contentpacks.ServerContentPackEvents
import org.qbrp.client.engine.contentpacks.ServerPacksAPI
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.modules.QbModule
import java.util.prefs.Preferences

@Autoload(LoadPriority.LOWEST, EnvType.CLIENT)
class ResourcePackApplier: QbModule("resourcepack-applier") {
    init {
        dependsOn { ClientCore.isApiAvailable<ServerPacksAPI>() }
        allowDynamicActivation()
    }
    val prefs = Preferences.userNodeForPackage(ResourcePackApplier::class.java)

    override fun onEnable() {
        val rpm = MinecraftClient.getInstance().resourcePackManager
        once {
            ServerContentPackEvents.ON_APPLY.register {
                val profile = if (getLastContentPackVersion() != it.version) it.getPackProfileAndCopyTo() else it.resourcePackProfile
                if (!rpm.enabledProfiles.contains(profile)) {
                    rpm.enable(profile.name)
                    MinecraftClient.getInstance().reloadResources()
                    setLastContentPackVersion(it.version)
                }
            }
        }
    }

    fun getLastContentPackVersion() = prefs.get("pack-version", "NONE")
    fun setLastContentPackVersion(ver: String) = prefs.put("pack-version", ver)
}