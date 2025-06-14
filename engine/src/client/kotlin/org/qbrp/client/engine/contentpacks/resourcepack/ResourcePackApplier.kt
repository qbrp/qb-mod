package org.qbrp.client.engine.contentpacks.resourcepack

import net.fabricmc.api.EnvType
import net.minecraft.client.MinecraftClient
import org.koin.core.component.get
import org.qbrp.client.ClientCore
import org.qbrp.client.core.resources.ModelRepository
import org.qbrp.client.engine.contentpacks.ContentPackEvents
import org.qbrp.client.engine.contentpacks.ClientContentPacksAPI
import org.qbrp.main.core.modules.Autoload
import org.qbrp.main.core.modules.LoadPriority
import org.qbrp.main.core.modules.QbModule
import java.util.prefs.Preferences

@Autoload(LoadPriority.LOWEST, EnvType.CLIENT)
class ResourcePackApplier: QbModule("resourcepack-applier") {
    init {
        dependsOn { ClientCore.isApiAvailable<ClientContentPacksAPI>() }
        dependsOn { ClientCore.isApiAvailable<ModelRepository>() }
    }
    val prefs = Preferences.userNodeForPackage(ResourcePackApplier::class.java)

    override fun onEnable() {
        val rpm = MinecraftClient.getInstance().resourcePackManager
        ContentPackEvents.ON_APPLY.register { pack ->
            // Включаем ресурспак
            val versionsMatch = getLastContentPack() == pack.versionWithName
            val profile = if (!versionsMatch) pack.getPackProfileAndCopyTo() else pack.resourcePackProfile
            if (!rpm.enabledProfiles.contains(profile) || !versionsMatch) {
                rpm.enable(profile.name)
                MinecraftClient.getInstance().reloadResources()
                setLastContentPack(pack.name, pack.version)
            }

            // Ставим айдишники из modellist.json
            get<ModelRepository>().ids.apply {
                clear()
                addAll(pack.modelsList.toModelEntry())
            }
        }
    }

    fun getLastContentPack() = prefs.get("pack-version", "NONE")
    fun getLastContentPackVersion() = prefs.get("pack-version", "NONE").split(":")[1]
    fun setLastContentPack(name: String, ver: String) = prefs.put("pack-version", "$name:$ver")
}