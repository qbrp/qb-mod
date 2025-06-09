package org.qbrp.main.engine.items.model

import net.minecraft.util.math.Vec3d
import org.qbrp.main.core.utils.networking.messages.components.Cluster
import org.qbrp.main.core.utils.networking.messages.components.ClusterBuilder
import org.qbrp.main.core.utils.networking.messages.types.ItemStackContent
import org.qbrp.main.engine.inventory.Stackable
import org.qbrp.main.engine.items.components.ItemTickContext
import org.qbrp.main.core.synchronization.position.SquaredRadiusSynchronizable

interface ItemSync: SquaredRadiusSynchronizable, ItemTickContext, Stackable {
    override val pos: Vec3d get() = entity?.pos ?: holder?.pos ?: Vec3d.ZERO

    override fun getCluster(): Cluster {
        return ClusterBuilder()
            .component("itemstack", ItemStackContent(asItemStack()))
            .build()
    }
}