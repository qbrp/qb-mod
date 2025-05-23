package org.qbrp.core.mc

import net.minecraft.util.math.Vec3d
import org.qbrp.core.game.lifecycle.Lifecycle
import org.qbrp.core.game.model.objects.BaseObject

abstract class McObject(
    lifecycle: Lifecycle<McObject>
) : BaseObject(lifecycle = lifecycle as Lifecycle<BaseObject>) {
    abstract val pos: Vec3d
    abstract fun getTooltip(): String
}