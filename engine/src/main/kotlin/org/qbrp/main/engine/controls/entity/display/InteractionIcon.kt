package org.qbrp.main.engine.controls.entity.display

import org.qbrp.main.engine.players.model.util.PlayerId

sealed class InteractionIcon() {
    data class Asset(val id: IconTextureId): InteractionIcon()
    data class Player(val id: PlayerId): InteractionIcon()
    data class Item(val id: IconItemModelId): InteractionIcon()
}