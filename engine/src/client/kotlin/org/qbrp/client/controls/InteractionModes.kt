package org.qbrp.client.controls

import org.qbrp.main.core.events.emit
import org.qbrp.main.core.fabricmc.player.PlayerPermissions
import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.controls.system.modes.BUILDER_MODE_PERMISSION
import org.qbrp.main.engine.controls.system.modes.CROW_MODE_PERMISSION
import org.qbrp.main.engine.controls.system.modes.GM_MODE_PERMISSION
import org.qbrp.main.engine.controls.system.modes.NO_CLIP_PERMISSION

class ClientInteractionModeManager() {
    private var access = mapOf<InteractionMode, Boolean>()
    var noClipAccess = false
        private set
    var currentMode: InteractionMode = InteractionMode.DEFAULT
        private set
    var noClip: Boolean = false
        private set

    fun setAccessibility(permissions: PlayerPermissions) {
        access = MODES.associateWith { it.hasAccess(permissions) }
        noClipAccess = permissions.hasPermission(NO_CLIP_PERMISSION)
    }

    fun setMode(interactionMode: InteractionMode) {
        if (!hasAccess(interactionMode)) return
        currentMode = interactionMode
        InteractionModeApplyEvent(interactionMode).emit()
    }

    fun toggleNoClip() {
        if (!noClipAccess) return
        noClip = !noClip
        NoClipApplyEvent(noClip).emit()
    }

    fun hasAccess(interactionMode: InteractionMode): Boolean {
        return access[interactionMode] ?: false
    }

    companion object {
        private val MODES = listOf(
            InteractionMode.DEFAULT,
            InteractionMode.BUILDER,
            InteractionMode.GM,
            InteractionMode.CROW
        )
    }
}

fun InteractionMode.hasAccess(permissions: PlayerPermissions) = when(this) {
    InteractionMode.BUILDER -> permissions.hasPermission(BUILDER_MODE_PERMISSION)
    InteractionMode.GM -> permissions.hasPermission(GM_MODE_PERMISSION)
    InteractionMode.CROW -> permissions.hasPermission(CROW_MODE_PERMISSION)
    InteractionMode.DEFAULT -> true
}
