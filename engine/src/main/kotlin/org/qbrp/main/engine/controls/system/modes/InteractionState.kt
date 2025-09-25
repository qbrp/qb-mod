package org.qbrp.main.engine.controls.system.modes

import org.qbrp.main.engine.controls.entity.InteractionMode

data class InteractionState(val mode: InteractionMode, val noClip: Boolean)