package org.qbrp.main.core.mc.player.attribute

import java.util.UUID
import java.util.concurrent.atomic.AtomicReference

data class McAttributeState(val addition: AtomicReference<AdditionModifier>, var modifierId: UUID)