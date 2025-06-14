package org.qbrp.main.core.regions.model

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.qbrp.main.core.utils.format.Format.asMiniMessage
import org.qbrp.main.engine.music.plasmo.view.View
import org.qbrp.main.core.utils.format.Format.formatMinecraft

class RegionView(val region: Region, val source: ServerPlayerEntity): View {
    private fun getHeader(): Text {
        return ("&a${region.name}&7 | Заполнение: &2${region.getVolume()}." +
                " &7Дистанция: &2${region.distanceTo(source.pos.x, source.pos.y, source.pos.z)}").asMiniMessage()
    }

    override fun getText(): Text {
        return getHeader()
    }
}