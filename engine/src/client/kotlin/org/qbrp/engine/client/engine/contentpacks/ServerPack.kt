package org.qbrp.engine.client.engine.contentpacks

import net.minecraft.client.MinecraftClient
import org.qbrp.core.assets.Assets
import org.qbrp.core.assets.common.Asset
import org.qbrp.core.assets.common.SimpleKey
import org.qbrp.core.assets.common.files.FileReference
import org.qbrp.core.assets.common.files.JsonFileReference
import org.qbrp.engine.assets.resourcepack.versioning.PackManifest
import java.io.File

class ServerPack(val path: File): Asset() {
    val resourcePack = path.resolve("qbrp-pack")
    val resourcePackProfile = MinecraftClient.getInstance().resourcePackManager.getProfile("qbrp-pack")
    val name = path.name
    val manifest: PackManifest = Assets.getOrLoad(
        JsonFileReference(SimpleKey(path.resolve("manifest")), PackManifest::class.java)
    )
    val version
        get() = manifest.version
}