package org.qbrp.system.modules

import org.koin.core.component.KoinComponent
import org.koin.core.module.Module
import org.koin.dsl.module
import org.qbrp.core.resources.ServerResources
abstract class QbModule(private val name: String) : KoinComponent {
    var priority: Int = 5
    private val dependencies: MutableList<() -> Boolean> = mutableListOf()

    protected fun dependsOn(condition: () -> Boolean) {
        dependencies.add(condition)
    }

    open fun getName(): String = name
    open fun getKoinModule(): Module = module { }
    open fun getAPI(): ModuleAPI? = null
    open fun load() = Unit

    open fun onDisable() = Unit
    open fun onEnable() = Unit

    open fun isEnabled(): Boolean {
        val isNotDisabled = !ServerResources.getConfig().disabledModules.contains(getName())
        val allDependenciesMet = dependencies.all { it() } // Проверка всех условий
        return isNotDisabled && allDependenciesMet
    }
}