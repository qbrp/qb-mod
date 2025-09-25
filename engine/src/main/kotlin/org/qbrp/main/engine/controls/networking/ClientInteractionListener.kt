package org.qbrp.main.engine.controls.networking

import org.qbrp.main.core.networking.PacketListener
import org.qbrp.main.engine.controls.entity.interactions.Intent
import org.qbrp.main.engine.controls.dto.InteractionContextMenuViewDto
import org.qbrp.main.engine.controls.entity.InteractionMode

interface ClientInteractionListener: PacketListener {
    fun onSync(intent: Intent, alternate: Boolean)
    fun onInteractionList(renderData: InteractionContextMenuViewDto)
}