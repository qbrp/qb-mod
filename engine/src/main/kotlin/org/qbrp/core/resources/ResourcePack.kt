package org.qbrp.core.resources

import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.core.resources.structure.PackStructure
import java.io.File

class ResourcePack(val structure: PackStructure,
    val packMcMeta: ServerConfigData.Resources.Pack,
    val icon: File) {
    val content = PackContent(structure)

    // TODO: По нормальному сделать проверку статуса
    fun bake(zipDirectory: File) {
        structure.initResourcePack(packMcMeta, icon)
        try {
            structure.zip(zipDirectory, createContainer = true, containerName = "qbrp")
        } catch (e: Exception) {
            ServerResources.getLogger().log("Возникла ошибка при архивации пакета ресурсов: ${e.message}")
        }
    }
}
