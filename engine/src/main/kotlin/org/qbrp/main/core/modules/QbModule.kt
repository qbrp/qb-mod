package org.qbrp.main.core.modules

import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.network.ServerPlayerEntity
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.ScopeDSL
import org.koin.dsl.module
import org.qbrp.main.core.Core
import org.qbrp.main.core.assets.FileSystem
import org.qbrp.main.core.assets.common.NamedAsset
import org.qbrp.main.core.assets.common.references.YamlFileReference
import org.qbrp.deprecated.resources.data.config.ServerConfigData
import org.qbrp.main.engine.Engine
import org.qbrp.main.core.utils.networking.messages.Message
import org.qbrp.main.core.utils.networking.messages.Messages
import org.qbrp.main.core.utils.networking.messages.types.BooleanContent
import org.qbrp.main.core.utils.networking.messaging.NetworkUtil

abstract class QbModule(private val name: String) : KoinComponent {
    var priority: Int = 5
    var serverState: Boolean = true
    var isDynamicActivationAllowed = false
    var isDynamicLoadingAllowed = true
    var createFile = false
    private val dependencies: MutableList<() -> Boolean> = mutableListOf()
    val linkedModules: MutableList<String> = mutableListOf()
    val scripts: MutableMap<String, () -> String> = mutableMapOf()

    companion object {
        val triggeredOnceMethods: MutableList<String> = mutableListOf()
    }

    protected inline fun <T> ifEnabled(crossinline block: (T) -> Unit): (T) -> Unit = {
        if (shouldLoad()) block(it)
    }

    protected open fun ifEnabled(method: () -> Unit) {
        if (shouldLoad()) method()
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

    fun sendStates(player: ServerPlayerEntity) {
        linkedModules.forEach { name ->
            NetworkUtil.sendMessage(player, Message(Messages.moduleUpdate(name), BooleanContent(shouldLoad())))
            NetworkUtil.sendMessage(player, Message(Messages.moduleClientUpdate(name), BooleanContent(shouldLoad())))
        }
    }

    open fun getName(): String = name
    open fun getKoinModule(): Module = module { }

    @Deprecated("Использовать koin-инъекцию")
    open fun getAPI(): Any? = null

    protected open fun onDisable() = Unit
    protected open fun onEnable() = Unit

    protected val configs = mutableMapOf<Class<out NamedAsset>, NamedAsset>()

    protected fun createModuleFileOnInit() {
        createFile = true
    }

    fun getModuleFile() = FileSystem.getModuleFile(this)

    protected inline fun <reified T : NamedAsset> createConfig(configAsset: T): T {
        val key = ModuleKey(this, configAsset.name)
        val clazz = T::class.java
        val asset: T = Core.ASSETS.loadOrCreate(configAsset, YamlFileReference(key, clazz))
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
    }

    fun unload() {
        unloadKoinModules(getKoinModule())
        onUnload()
    }

    fun reload() {
        unload()
        load()
    }

    fun enable() {
        load()
        onEnable()
    }

    fun disable() {
        unload()
        onDisable()
    }

    val scope by lazy { getKoin().createScope(getName(), named(getName())) }

    inline fun <reified T: Any> getLocal(): T {
        return scope.get<T>()
    }

    inline fun <reified ApiClass> inner(
        api: ApiClass? = null,
        crossinline definitions: ScopeDSL.() -> Unit
    ): Module {
        return module {
            scope(named(getName())) {
                definitions()
            }
            if (api != null) single<ApiClass> { api }
        }
    }

    inline fun <reified ApiClass> onlyApi(
        api: ApiClass,
    ): Module {
        return module {
            single<ApiClass> { api }
        }
    }

    fun inner(
         definitions: ScopeDSL.() -> Unit
    ): Module {
        return module {
            scope(named(getName())) {
                definitions()
            }
        }
    }

    fun innerWithApi(
        api: Module.() -> Unit,
        inner: ScopeDSL.() -> Unit
    ): Module = module {
        // API–слой (singleton, factory и т.п.)
        api()
        scope(named(getName())) {
            inner()
        }
    }

    fun link(moduleName: String) {
        linkedModules += moduleName
    }

    open fun shouldLoad(): Boolean {
        val isNotDisabled = if (FabricLoader.getInstance().environmentType != EnvType.CLIENT) !get<ServerConfigData>().disabledModules.contains(getName()) else true
        val allDependenciesMet = dependencies.all { it() } // Проверка всех условий
        return isNotDisabled && allDependenciesMet && serverState
    }
}