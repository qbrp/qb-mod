package org.qbrp.main.engine.controls.system.modes

import net.minecraft.world.GameMode
import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.core.components.extensions.getOrAdd
import org.qbrp.main.core.fabricmc.player.Permission
import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.players.model.PlayerObject
import org.qbrp.main.engine.players.model.ServerPlayerObject
import kotlin.properties.Delegates.observable

fun <T> dirtyObservable(initialValue: T, onDirty: () -> Unit) =
    observable(initialValue) { _, old, new ->
        if (old != new) onDirty()
    }

class InteractionModeComponent(
    initialMode: InteractionMode,
    initialNoClip: Boolean,
    var dirty: Boolean = false,
) : DataComponent {
    var mode: InteractionMode by dirtyObservable(initialMode) { dirty = true }
    var noClip: Boolean by dirtyObservable(initialNoClip) { dirty = true }
}

fun PlayerObject.setInteractionMode(mode: InteractionMode) {
    getInteractionModeComponent().mode = mode
}

fun PlayerObject.setNoClip(enabled: Boolean) {
    getInteractionModeComponent().noClip = enabled
}

fun PlayerObject.getInteractionModeComponent(): InteractionModeComponent {
    return getOrAdd {
        InteractionModeComponent(
            initialMode = InteractionMode.DEFAULT,
            initialNoClip = false,
        )
    }
}

val PlayerObject.interactionMode get() = this.getInteractionModeComponent().mode

internal val InteractionMode.gameMode
    get() = when (this) {
        InteractionMode.DEFAULT -> GameMode.SURVIVAL
        InteractionMode.BUILDER -> GameMode.CREATIVE
        InteractionMode.CROW -> GameMode.ADVENTURE
        InteractionMode.GM -> GameMode.CREATIVE
    }

val NO_CLIP_PERMISSION = Permission("no-clip")
val BUILDER_MODE_PERMISSION = Permission("builder-mode")
val GM_MODE_PERMISSION = Permission("gm-mode")
val CROW_MODE_PERMISSION = Permission("crow-mode")