package org.qbrp.system.modules

import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.KoinComponent
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.core.resources.ServerResources
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages
import org.qbrp.system.networking.messages.types.BooleanContent
import org.qbrp.system.networking.messaging.NetworkManager

abstract class QbModule(private val name: String) : KoinComponent {
    var priority: Int = 5
    var serverState: Boolean = true
    private val dependencies: MutableList<() -> Boolean> = mutableListOf()

    protected fun dependsOn(condition: () -> Boolean) {
        dependencies.add(condition)
    }

    fun sendStateInformation(player: ServerPlayerEntity) {
        NetworkManager.sendMessage(player, Message(Messages.moduleUpdate(name), BooleanContent(isEnabled())))
        NetworkManager.sendMessage(player, Message(Messages.moduleClientUpdate(name), BooleanContent(isEnabled())))
    }

    open fun getName(): String = name
    open fun getKoinModule(): Module = module { }
    open fun getAPI(): ModuleAPI? = null
    open fun load() = Unit

    open fun onDisable() = Unit
    open fun onEnable() = Unit

    open fun isEnabled(): Boolean {
        val isNotDisabled = if (FabricLoader.getInstance().environmentType != EnvType.CLIENT) !ServerResources.getConfig().disabledModules.contains(getName()) else true
        val allDependenciesMet = dependencies.all { it() } // Проверка всех условий
        return isNotDisabled && allDependenciesMet && serverState
    }
}