package org.qbrp.main.core.fabricmc.player

import kotlinx.serialization.Serializable
import net.luckperms.api.LuckPermsProvider
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.Core
import org.qbrp.main.core.components.extensions.get
import org.qbrp.main.core.fabricmc.PlayerPermissionsComponent
import org.qbrp.main.engine.players.model.ServerPlayerObject

@JvmInline
@Serializable
value class Permission(val name: String) {
    val qualifiedName: String get() = "${Core.MOD_ID}.$name"
    val isEmpty get() = name == ""
}

interface PlayerPermissions {
    val permissions: List<Permission>
    val isOp: Boolean

    fun hasPermission(perm: Permission) = permissions.contains(perm) || isOp
}

val EMPTY_PERMISSIONS = PermissionSnapshot(listOf(), false)

@Serializable
data class PermissionSnapshot(
    override val permissions: List<Permission>,
    override val isOp: Boolean,
) : PlayerPermissions

fun PlayerPermissions.toSerializableSnapshot() = PermissionSnapshot(permissions, isOp)

@Deprecated("")
fun ServerPlayerEntity.hasPermission(permission: String): Boolean {
    return LuckPermsPermission.hasPermission(this, Permission(permission))
}

fun ServerPlayerObject.hasPermission(permission: Permission): Boolean {
    return get<PlayerPermissionsComponent>()?.hasPermission(permission) == true
}

object LuckPermsPermission {
    private val luckPerms by lazy { LuckPermsProvider.get() }

    fun isOp(entity: ServerPlayerEntity): Boolean {
        return entity.hasPermissionLevel(4)
    }

    fun hasPermission(entity: ServerPlayerEntity, permission: Permission): Boolean {
        if (permission.isEmpty) return true
        if (isOp(entity)) return true
        val user = entity.luckPermsData ?: return false

        return user
            .permissionData
            .checkPermission(permission.qualifiedName)
            .asBoolean()
    }

    fun listPermissions(entity: ServerPlayerEntity): List<Permission> {
        val user = entity.luckPermsData ?: return listOf()
        val permissions = user.permissionData.permissionMap.map { Permission(it.key) }
        return permissions
    }

    fun permissionsOf(entity: ServerPlayerEntity): PlayerPermissions {
        val user = entity.luckPermsData ?: return EMPTY_PERMISSIONS
        val permissions = user.permissionData.permissionMap.map { Permission(it.key) }
        val isOp = isOp(entity)
        return PermissionSnapshot(permissions, isOp)
    }

    private val ServerPlayerEntity.luckPermsData get() = luckPerms.userManager.getUser(uuid)?.cachedData
}