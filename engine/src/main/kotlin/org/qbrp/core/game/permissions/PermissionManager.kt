package org.qbrp.core.game.permissions
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.server.network.ServerPlayerEntity
import org.qbrp.core.Core.Companion.MOD_ID

object PermissionManager {
    private val permissions: MutableList<String> = mutableListOf()

    fun getOrRegister(module: String, permission: String): String {
        val fullPermission = "$MOD_ID.$module.$permission"
        if (!permissions.contains(fullPermission)) {
            permissions.add(fullPermission)
        }
        return fullPermission
    }

    fun getOrRegister(permission: String): String {
        val parts = permission.split(".", limit = 2)
        if (parts.size == 2) {
            return getOrRegister(parts[0], parts[1])
        } else {
            throw IllegalArgumentException("Неправильный формат. Требуется 'module.permission'.")
        }
    }

    fun getPermission(module: String, permission: String): String? {
        return permissions.find { it == "$MOD_ID.$module.$permission" }
    }

    fun getPermissionsByModule(module: String): List<String> {
        return permissions.filter { it.startsWith("$MOD_ID.$module.") }
    }

    fun getPermission(permission: String): String? {
        val parts = permission.split(".", limit = 2)
        if (parts.size == 2) {
            return getPermission(parts[0], parts[1])
        }
        return null
    }

    private fun checkPermission(source: ServerCommandSource, permission: String): Boolean {
//        val result = PermissionCheckEvent.EVENT.invoker().onPermissionCheck(source, permission)
//        return when (result) {
//            TriState.TRUE -> true
//            TriState.FALSE -> false
//            TriState.DEFAULT -> false // По умолчанию запрещаем, если право не обработано
//        }
        return true
    }

    fun ServerPlayerEntity.hasPermission(permission: String): Boolean {
        if (permission == "") return true
        return true
//        if (permission == null) { println("Передано значение null в hasPermission"); return true }
//        return checkPermission(this.commandSource, permission)
    }

}