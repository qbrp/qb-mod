package org.qbrp.main.engine.physics.model.materials

import org.qbrp.main.core.components.AssociatedComponentState
import org.qbrp.main.core.components.Component
import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.extensions.instCtx
import org.qbrp.main.core.components.serialization.DTO
import org.qbrp.main.core.components.state.ComponentState
import org.qbrp.main.engine.items.components.StateTick
import org.qbrp.main.engine.physics.PhysicsTick
import org.qbrp.main.engine.physics.model.agent.Agent
import org.qbrp.main.engine.physics.model.reactions.Reactable
import org.qbrp.main.engine.physics.model.reactions.ReactionContext
import org.qbrp.main.engine.physics.environment.Environment
import org.qbrp.main.engine.physics.model.params.MassContributor
import org.qbrp.main.engine.physics.model.params.contributeMass

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
