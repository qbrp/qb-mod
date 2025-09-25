package org.qbrp.main.engine.physics.model.agent

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.state.ComponentState
import org.qbrp.main.engine.physics.PhysicsTick
import org.qbrp.main.engine.physics.components.Friction
import org.qbrp.main.engine.physics.environment.Environment
import org.qbrp.main.engine.physics.model.MaterialManager
import org.qbrp.main.engine.physics.model.params.PhysicalParameters
import org.qbrp.main.engine.physics.model.materials.Material
import org.qbrp.main.engine.physics.model.materials.MaterialInstantiateContext
import org.qbrp.main.engine.physics.model.params.contributeMass
import org.qbrp.main.engine.physics.model.reactions.Reactable
import org.qbrp.main.engine.physics.model.reactions.ReactionContext

typealias AgentComponents = ComponentState<Agent>

interface Agent: PhysicalParameters, Reactable, PhysicsTick  {
    val displayName: String //для отладки и прочего говна
    var temperature: Double //В кельвинах
    val volume: Double //в м3
    val materialManager: MaterialManager

    val externalState: ComponentManager? get() = null
    val materials: List<Material> get() = materialManager.getComponents() as List<Material>

    val averageDensity: Double
        get() = 1.0 / materials.sumOf { it.proportion / it.density }

    override val mass: Double get() = materialManager.contributeMass() + (externalState?.contributeMass() ?: 0.0)
    override val averageFriction: Friction get() = Friction.Companion.average(materials)

    //Реакция всех материалов агента на другого агента
    override fun react(agent: Agent, ctx: ReactionContext, env: Environment) {
        materials
            .forEach { it.react(agent, ctx, env) }
    }

    override fun tickPhysics(env: Environment, parentEnv: Environment?) {
        materials
            .forEach { it.tickPhysics(env, parentEnv) }
    }

    fun materialCtx(proportion: Double) = MaterialInstantiateContext(materialManager, externalState, proportion)
}