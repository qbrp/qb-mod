package org.qbrp.core.resources.structure.integrated

import org.qbrp.system.utils.keys.Key

enum class Branches(val key: Key) {

    MODELS_REGISTRY(Key("models")),
    TEXTURES_REGISTRY(Key("textures")),
    ITEM_MODELS(Key("items")),
    ITEM_TEXTURES(Key("items_textures")),
    UNSET(Key("unset")),

}