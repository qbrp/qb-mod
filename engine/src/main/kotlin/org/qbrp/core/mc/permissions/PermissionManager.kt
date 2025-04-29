
import net.luckperms.api.LuckPermsProvider
import net.luckperms.api.query.QueryOptions
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.core.Core
import org.qbrp.core.Core.Companion.MOD_ID
import org.qbrp.core.ServerCore

object PermissionManager {
    fun checkPermission(player: ServerPlayerEntity, permission: String): Boolean {
        if (player.hasPermissionLevel(4)) return true
        val luckPerms = LuckPermsProvider.get()
        val user = luckPerms.userManager.getUser(player.uuid) ?: return false

        return user.cachedData
            .permissionData
            .checkPermission(permission)
            .asBoolean()
    }

    fun ServerPlayerEntity.hasPermission(permission: String): Boolean {
        if (permission == "") return true
        return checkPermission(this, "${MOD_ID}.$permission")
    }

}