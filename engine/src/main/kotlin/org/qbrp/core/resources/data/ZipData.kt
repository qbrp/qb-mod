package org.qbrp.core.resources.data

import org.qbrp.core.resources.units.DownloadedUnit
import java.io.File

data class ZipData(
    val destinationPath: String,// Путь, куда будет сохранён файл
) : Data(unit = DownloadedUnit::class.java) {
    override fun toFile(): String = destinationPath

    companion object {
        fun fromFile(file: File): ZipData {
            return ZipData(file.path)
        }
    }
}
