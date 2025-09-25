package org.qbrp.main.engine.controls.entity

import org.qbrp.main.engine.controls.entity.display.IconTextureId
import org.qbrp.main.engine.controls.entity.display.InteractionIcon

object IconIds {
    val NOTHING = IconTextureId("nothing")
    val HAND = IconTextureId("hand")
    val COPY = IconTextureId("copy")
    val TRASH_CAN = IconTextureId("delete")
    val BACKPACK = IconTextureId("open_inventory")
    val THROW_AWAY = IconTextureId("throw_away")
}

fun IconTextureId.toIcon() = InteractionIcon.Asset(this)