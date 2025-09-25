package org.qbrp.client.engine.interactions.event

import org.qbrp.main.core.events.Event
import org.qbrp.main.engine.controls.entity.InteractionMode

data class InteractionModeApplyEvent(val interactionMode: InteractionMode): Event