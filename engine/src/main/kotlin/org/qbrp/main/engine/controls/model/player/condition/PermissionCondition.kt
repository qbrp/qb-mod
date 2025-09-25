package org.qbrp.main.engine.controls.model.player.condition

import org.qbrp.main.core.mc.player.hasPermission
import org.qbrp.main.core.mc.player.permission.Permission
import org.qbrp.main.core.mc.player.permission.PermissionService
import org.qbrp.main.engine.controls.entity.interactable.Interactable
import org.qbrp.main.engine.controls.entity.conditions.AvailableResult
import org.qbrp.main.engine.controls.entity.conditions.Condition
import org.qbrp.main.engine.controls.entity.interactions.InteractionContext

class PermissionCondition(val permission: Permission, private val permissionService: PermissionService): Condition<Interactable, Interactable> {
    override fun isAvailable(sub: Interactable, obj: Interactable, ctx: InteractionContext): AvailableResult {
        return when(ctx.player.hasPermission(permission, permissionService)) {
            true -> AvailableResult.Yes
            false -> AvailableResult.No("Требуется разрешение ${permission.qualifiedName}")
        }
    }
}