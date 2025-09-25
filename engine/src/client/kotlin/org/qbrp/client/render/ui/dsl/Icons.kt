package org.qbrp.client.render.ui.dsl

import org.qbrp.client.core.EngineId
import org.qbrp.client.render.Render
import org.qbrp.client.render.ui.UiContext
import org.qbrp.client.render.ui.components.item.Item
import org.qbrp.client.render.ui.components.texture.PlayerHeadTexture
import org.qbrp.main.engine.items.ModelId
import org.qbrp.main.engine.players.model.util.PlayerId

sealed class IconPrefab {
    data class Asset(val id: EngineId) : IconPrefab()
    data class PlayerHead(val id: PlayerId) : IconPrefab()
    data class Item(val id: ModelId) : IconPrefab()
}

fun IconAssetId(id: EngineId) = IconAssetId(id.path)

class IconAssetId(id: String) : EngineId("${Render.ICON_TEXTURES_PATH}/${id}.png")

@UiDsl
context(ui: UiContext)
fun ViewScope.icon(icon: IconPrefab, scale: Float) {
    fixedSize(scale, scale)
    when (icon) {
        is IconPrefab.Asset -> texture(IconAssetId(icon.id), 32f)
        is IconPrefab.PlayerHead -> {
            val head = ui.playerProfileManager.getProfile(icon.id)
            val headExists = head != null

            when (headExists) {
                true -> component { ctx -> PlayerHeadTexture(ctx, head) }
                false -> missingTexture()
            }
        }

        is IconPrefab.Item -> component { Item(it, icon.id, scale) }
    }
}