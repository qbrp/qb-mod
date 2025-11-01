package org.qbrp.main.engine.chemistry.materials

import org.qbrp.main.engine.chemistry.reactions.Reactable
import org.qbrp.main.engine.items.components.StateTick
import org.qbrp.main.engine.chemistry.PhysicsTick

interface MaterialHandler: Reactable, PhysicsTick, StateTick