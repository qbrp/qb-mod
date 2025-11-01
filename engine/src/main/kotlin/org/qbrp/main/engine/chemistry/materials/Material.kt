package org.qbrp.main.engine.chemistry.materials

import org.qbrp.main.core.components.AssociatedComponentState
import org.qbrp.main.core.components.Component
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.extensions.instCtx
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.core.components.state.ComponentState
import org.qbrp.main.engine.chemistry.agent.Agent
import org.qbrp.main.engine.chemistry.params.MassContributor
import org.qbrp.main.engine.chemistry.params.contributeMass
import org.qbrp.main.engine.chemistry.reactions.Reactable
import org.qbrp.main.engine.chemistry.reactions.ReactionContext
import org.qbrp.main.engine.items.components.StateTick
import org.qbrp.main.engine.chemistry.PhysicsTick
import org.qbrp.main.engine.chemistry.environment.Environment

typealias AgentSubstance = AssociatedComponentState<Agent>

open class Material(
    ctx: MaterialInstantiateContext,
    val name: String,
    val density: Double, //кг / м3
    val molarMass: Double, // г/моль
    val componentManager: ComponentManager = ComponentState<Material>(),
) : Component<AgentSubstance>, ComponentManager by componentManager, MaterialHandler, MassContributor {
    var enabled = false

    override var state: AgentSubstance = ctx.componentManager
    var externalState: ComponentManager? = ctx.externalState
    val proportion: Double = ctx.proportion
    val agent: Agent get() = state.parent
    val proportionMass: Double get() = proportion * agent.mass

    val ctx: MaterialPropertyContext get() = instCtx(this)
    override val dto: DTO? get() = MaterialDTO(name, proportion, density, molarMass)

    override fun calculateMass(): Double {
        return agent.volume * density * proportion + contributeMass()
    }

    override fun react(agent: Agent, ctx: ReactionContext, env: Environment) {
        if (enabled) componentManager.filterIsInstance<Reactable>()
            .forEach { it.react(agent, ctx, env) }
    }

    override fun tickPhysics(env: Environment, parentEnv: Environment?) {
        if (enabled) componentManager.filterIsInstance<PhysicsTick>()
            .forEach { it.tickPhysics(env, parentEnv) }
    }

    override fun tickState() {
        if (enabled) componentManager.filterIsInstance<StateTick>()
            .forEach { it.tickState() }
    }
}
