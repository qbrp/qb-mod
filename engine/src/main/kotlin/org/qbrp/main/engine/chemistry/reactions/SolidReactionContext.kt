package org.qbrp.main.engine.chemistry.reactions

import org.qbrp.main.engine.chemistry.agent.Agent

//contactArea в сантиметрах
class SolidReactionContext(override val strength: Double, val contactArea: Double? = null, ): ReactionContext {
    override fun getPressure(agent: Agent): Double {
        return TODO()
    }

    companion object {
        val NO_MOVE = SolidReactionContext(0.0)
    }
}