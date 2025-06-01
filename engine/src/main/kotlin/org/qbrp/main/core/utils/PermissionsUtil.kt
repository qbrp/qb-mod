
import net.luckperms.api.LuckPermsProvider
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.main.core.Core

object PermissionsUtil {
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
        return checkPermission(this, "${Core.MOD_ID}.$permission")
    }

}