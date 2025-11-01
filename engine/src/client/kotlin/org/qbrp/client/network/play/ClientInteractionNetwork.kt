package org.qbrp.client.network.play

import org.koin.core.component.KoinComponent
import org.qbrp.client.controls.ClientInteractionCallEvent
import org.qbrp.client.controls.ClientInteractionManager
import org.qbrp.client.controls.InteractionModeApplyEvent
import org.qbrp.client.controls.NoClipApplyEvent
import org.qbrp.client.controls.SelectInteractionEvent
import org.qbrp.client.engine.items.ClientItemObject
import org.qbrp.client.network.transport.registerClientReceiver
import org.qbrp.client.network.transport.sendC2SPacket
import org.qbrp.main.core.events.EventPriority
import org.qbrp.main.core.events.on
import org.qbrp.main.engine.containers.util.ContainerId
import org.qbrp.main.engine.controls.ControlsModule
import org.qbrp.main.engine.controls.entity.InteractionMode
import org.qbrp.main.engine.controls.entity.interactions.InteractionId
import org.qbrp.main.engine.items.model.ItemId
import org.qbrp.main.network.interaction.INTERACTION_CALL_ENDPOINT
import org.qbrp.main.network.interaction.INTERACTION_LIST_ENDPOINT
import org.qbrp.main.network.interaction.INTERACTION_MODE_APPLY_ENDPOINT
import org.qbrp.main.network.interaction.INTERACTION_SELECT_ENDPOINT
import org.qbrp.main.network.interaction.InteractCallPacket
import org.qbrp.main.network.interaction.InteractionModeApplyPacket
import org.qbrp.main.network.interaction.InteractionSelectPacket
import org.qbrp.main.network.interaction.NO_CLIP_ENDPOINT
import org.qbrp.main.network.interaction.NoClipPacket
import org.qbrp.main.network.interaction.contextMenuDtoToView

fun KoinComponent.registerClientInteractionHandler(interactionManager: ClientInteractionManager) {
    INTERACTION_LIST_ENDPOINT
        .registerClientReceiver {
            val viewData = contextMenuDtoToView(contextMenuDto)
            interactionManager.applyInteractActivation(viewData)
        }

    on<ClientInteractionCallEvent>() {
        sendInteractionCallPacket(selectedInventory, selectedItem)
    }

    on<SelectInteractionEvent>() {
        sendInvokeInteractionPacket(id)
    }

    on<InteractionModeApplyEvent>(EventPriority.LATEST) {
        sendInteractionModePacket(interactionMode)
    }

    on<NoClipApplyEvent>() {
        sendNoClipPacket(update)
    }
}

fun sendInteractionCallPacket(
    selectedInventory: ContainerId?,
    selectedItem: ItemId?
) {
    INTERACTION_CALL_ENDPOINT
        .sendC2SPacket(
            InteractCallPacket(
                selectedInventory,
                selectedItem
            )
        )
}

fun sendInvokeInteractionPacket(id: InteractionId) {
    INTERACTION_SELECT_ENDPOINT
        .sendC2SPacket(InteractionSelectPacket(id))
}

fun sendInteractionModePacket(mode: InteractionMode) {
    INTERACTION_MODE_APPLY_ENDPOINT
        .sendC2SPacket(InteractionModeApplyPacket(mode))
}

fun sendNoClipPacket(enabled: Boolean) {
    NO_CLIP_ENDPOINT
        .sendC2SPacket(NoClipPacket(enabled))
}