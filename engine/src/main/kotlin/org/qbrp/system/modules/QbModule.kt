package org.qbrp.system.modules

import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.KoinComponent
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.core.assets.Assets
import org.qbrp.core.assets.FileSystem
import org.qbrp.core.assets.common.NamedAsset
import org.qbrp.core.assets.common.files.YamlFileReference
import org.qbrp.core.resources.ServerResources
import org.qbrp.engine.Engine
import org.qbrp.system.networking.messages.Message
import org.qbrp.system.networking.messages.Messages
import org.qbrp.system.networking.messages.types.BooleanContent
import org.qbrp.system.networking.messaging.NetworkManager

abstract class QbModule(private val name: String) : KoinComponent {
    var priority: Int = 5
    var serverState: Boolean = true
    var isDynamicActivationAllowed = false
    var isDynamicLoadingAllowed = true
    var createFile = false
    private val dependencies: MutableList<() -> Boolean> = mutableListOf()
    val scripts: MutableMap<String, () -> String> = mutableMapOf()

    companion object {
        val triggeredOnceMethods: MutableList<String> = mutableListOf()
    }

    protected inline fun <T> ifEnabled(crossinline block: (T) -> Unit): (T) -> Unit = {
        if (Engine.moduleManager.isModuleEnabled(getName())) block(it)
    }

    protected open fun ifEnabled(method: () -> Unit) {
        if (Engine.moduleManager.isModuleEnabled(name)) method()
    }

    fun once(runnable: () -> Unit) {
        if (!triggeredOnceMethods.contains(getName())) runnable()
    }

    protected fun allowDynamicActivation() {
        isDynamicActivationAllowed = true
    }

    protected fun allowDynamicLoading() {
        isDynamicLoadingAllowed = true
    }

    protected fun dependsOn(condition: () -> Boolean) {
        dependencies.add(condition)
    }

    protected inline fun <reified T: ModuleAPI> requireApi() = Engine.getAPI<T>()!!

    fun runScript(name: String): String {
        return scripts[name]?.invoke() ?: "Скрипт не обнаружен"
    }

    fun listScripts(): List<String> {
        return scripts.keys.toList()
    }

    fun sendStateInformation(player: ServerPlayerEntity) {
        NetworkManager.sendMessage(player, Message(Messages.moduleUpdate(name), BooleanContent(shouldLoad())))
        NetworkManager.sendMessage(player, Message(Messages.moduleClientUpdate(name), BooleanContent(shouldLoad())))
    }

    open fun getName(): String = name
    open fun getKoinModule(): Module = module { }
    open fun getAPI(): ModuleAPI? = null

    protected open fun onDisable() = Unit
    protected open fun onEnable() = Unit

    protected val configs = mutableMapOf<Class<out NamedAsset>, NamedAsset>()

    protected fun createModuleFileOnInit() {
        createFile = true
    }

    protected fun getModuleFile() = FileSystem.getModuleFile(this)

    protected inline fun <reified T : NamedAsset> createConfig(configAsset: T): T {
        val key = ModuleKey(this, configAsset.name)
        val clazz = T::class.java
        val asset: T = Assets.loadOrCreate(configAsset, YamlFileReference(key, clazz))
        configs[clazz] = asset
        return asset
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : NamedAsset> requireConfig(clazz: Class<T>): T =
        (configs[clazz] as? T)
            ?: throw IllegalStateException("Config for ${clazz.simpleName} is not created")

    inline fun <reified T : NamedAsset> requireConfig(): T = requireConfig(T::class.java)

    open fun onLoad() = Unit
    open fun onUnload() = Unit

    fun load() {
        loadKoinModules(getKoinModule())
        onLoad()
        enable()
    }

    fun unload() {
        disable()
        unloadKoinModules(getKoinModule())
        onUnload()

    }

    fun reload() {
        unload()
        load()
    }

    fun enable() {
        onEnable()
    }

    fun disable() {
        onDisable()
    }

    open fun shouldLoad(): Boolean {
        val isNotDisabled = if (FabricLoader.getInstance().environmentType != EnvType.CLIENT) !ServerResources.getConfig().disabledModules.contains(getName()) else true
        val allDependenciesMet = dependencies.all { it() } // Проверка всех условий
        return isNotDisabled && allDependenciesMet && serverState
    }
}