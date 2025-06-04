package org.qbrp.client.engine.contentpacks

import net.minecraft.client.MinecraftClient
import net.minecraft.resource.ResourcePackProfile
import org.qbrp.main.core.assets.FileSystem
import org.qbrp.main.core.assets.common.SimpleKey
import org.qbrp.main.core.assets.common.references.JsonFileReference
import org.qbrp.main.engine.assets.contentpacks.build.ContentPack
import org.qbrp.main.engine.assets.contentpacks.PackManifest
import org.qbrp.main.engine.assets.resourcepack.ModelsList
import java.io.File

class ClientContentPack(path: File): ContentPack(
    path,
    JsonFileReference(SimpleKey(path.resolve("manifest")), PackManifest::class).read(),
    JsonFileReference(SimpleKey(path.resolve("modellist")), ModelsList::class).read(),
    ) {

    companion object {
        const val PACK_NAME = "qbrp-pack"
        const val PACK_PROFILE = "file/qbrp-pack"
    }

    val resourcePackProfile = MinecraftClient.getInstance().resourcePackManager.getProfile(PACK_PROFILE)
        ?: throw RuntimeException("Could not load pack")
    val name = path.name

    fun getPackProfileAndCopyTo(resourcePacksPath: File = FileSystem.MINECRAFT_RESOURCEPACKS): ResourcePackProfile {
        resourcePackFile.copyRecursively(resourcePacksPath.resolve(PACK_NAME), true)
        return MinecraftClient.getInstance().resourcePackManager.getProfile(PACK_PROFILE)
            ?: throw RuntimeException("Could not load pack")
                .also { it.printStackTrace() }
    }
}