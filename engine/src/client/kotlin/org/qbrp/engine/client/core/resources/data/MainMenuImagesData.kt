package org.qbrp.engine.client.core.resources.data

import icyllis.modernui.graphics.BitmapFactory
import icyllis.modernui.graphics.Image
import org.qbrp.core.Core
import org.qbrp.core.resources.data.Data
import org.qbrp.core.resources.structure.Branch

data class MainMenuImagesData(val images: List<ImageDescription> = listOf(
    ImageDescription("Яма", "Рогули смотрят на яму", 1)
)): Data() {
    override fun toFile(): String = gson.toJson(this)

    data class ImageDescription(val name: String, val description: String, val imageId: Int)
}