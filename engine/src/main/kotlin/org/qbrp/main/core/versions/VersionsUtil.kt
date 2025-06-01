package org.qbrp.main.core.versions

import klite.NotFoundException
import net.fabricmc.loader.api.FabricLoader
import org.qbrp.main.core.Core
import kotlin.jvm.optionals.getOrNull

object VersionsUtil {
    fun getVersion(): Version {
        val version = FabricLoader.getInstance().getModContainer(Core.MOD_ID)
            .map { it.metadata.version.friendlyString }
        return Version.fromString(version.getOrNull() ?: throw NotFoundException("Не найдена версия в fabric.mod.json"))
    }
}