package org.qbrp.main.engine.physics.model.materials

import org.qbrp.main.engine.items.components.StateTick
import org.qbrp.main.engine.physics.PhysicsTick
import org.qbrp.main.engine.physics.model.reactions.Reactable

interface MaterialHandler: Reactable, PhysicsTick, StateTick