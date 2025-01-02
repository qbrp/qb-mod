package org.qbrp.core.resources

import org.qbrp.core.resources.data.config.ServerConfigData
import org.qbrp.core.resources.structure.PackStructure
import java.io.File

class ResourcePack(val structure: PackStructure,
    val packMcMeta: ServerConfigData.Resources.Pack,
    val icon: File) {
    val content = PackContent(structure)
    private var baked = false

    fun isBaked(): Boolean { return baked }

    // TODO: По нормальному сделать проверку статуса
    fun bake(zipDirectory: File, checkBakingStatus: (PackStructure) -> Boolean) {
        structure.initResourcePack(packMcMeta, icon)
        baked = checkBakingStatus(structure) // Устанавливаем значение baked, вызвав переданную функцию
        try {
            structure.zip(zipDirectory, createContainer = true, containerName = "qbrp")
        } catch (e: Exception) {
            ServerResources.getLogger().log("Возникла ошибка при архивации пакета ресурсов: ${e.message}")
            baked = false
        }
    }
}
