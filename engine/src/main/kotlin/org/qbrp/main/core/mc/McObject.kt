package org.qbrp.main.core.mc

import net.minecraft.util.math.Vec3d
import org.qbrp.main.core.game.lifecycle.Lifecycle
import org.qbrp.main.core.game.model.objects.BaseObject

abstract class McObject() : BaseObject() {
    abstract val pos: Vec3d
    abstract fun getTooltip(): String
}