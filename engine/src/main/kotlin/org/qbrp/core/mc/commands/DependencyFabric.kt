package org.qbrp.core.mc.commands

class DependencyFabric {
    private val dependencies = mutableMapOf<String, Any>()

    fun <T> register(name: String, dependency: T): DependencyFabric {
        dependencies[name] = dependency as Any
        return this
    }

    fun createDeps(): Deps {
        return Deps(dependencies)
    }
}
