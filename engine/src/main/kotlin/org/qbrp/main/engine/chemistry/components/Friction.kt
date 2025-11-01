package org.qbrp.main.engine.chemistry.components

import kotlinx.serialization.Serializable
import org.qbrp.main.core.components.DataComponent
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.components.objects.ComponentObject
import org.qbrp.main.core.util.networking.messages.components.Cluster
import org.qbrp.main.core.util.networking.messages.components.ClusterBuilder
import org.qbrp.main.core.util.networking.messages.components.readonly.ClusterViewer
import org.qbrp.main.core.util.networking.messages.types.DoubleContent
import org.qbrp.main.engine.chemistry.agent.Agent
import org.qbrp.main.engine.chemistry.materials.Material

//ньютоны
@Serializable
class Friction(val static: Double, val kinetic: Double): DataComponent {

    companion object {
        fun average(materials: List<Material>): Friction {
            val static = 1.0 / materials.sumOf {
                it.proportion / (it.state.get<Friction>()?.static ?: DEFAULT.static)
            }
            val kinetic = 1.0 / materials.sumOf {
                it.proportion / (it.state.get<Friction>()?.kinetic ?: DEFAULT.kinetic)
            }
            return Friction(static, kinetic)
        }
        fun average(agent: Agent): Friction = average(agent.materials)
        fun average(obj: ComponentObject): Friction {
            return average(obj.get<Agent>() ?: return DEFAULT)
        }
        fun deserialize(cluster: ClusterViewer): Friction {
            return Friction(cluster.getComponentData("static")!!, cluster.getComponentData("kinetic")!!)
        }
        val DEFAULT = Friction(0.5, 0.7)
    }

    fun toCluster(): Cluster {
        return ClusterBuilder()
            .component("static", DoubleContent(static))
            .component("kinetic", DoubleContent(kinetic))
            .build()
    }
}