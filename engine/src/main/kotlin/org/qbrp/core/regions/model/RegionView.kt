package org.qbrp.core.regions.model

import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.text.Text
import org.qbrp.engine.music.plasmo.view.View
import org.qbrp.system.utils.format.Format.formatMinecraft

class RegionView(val region: Region, val source: ServerPlayerEntity): View {
    private fun getHeader(): Text {
        return "&a${region.name}&7 | Заполнение: &2${region.getVolume()}. &7Дистанция: &2${region.distanceTo(source.pos.x, source.pos.y, source.pos.z)}".formatMinecraft()
    }

    override fun getText(): Text {
        return getHeader()
    }
}