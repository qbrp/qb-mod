package org.qbrp.core.game

import org.qbrp.core.game.model.components.Component
import org.qbrp.core.game.model.components.exception.ComponentCollisionException
import org.qbrp.core.game.model.components.exception.ComponentNotFoundException

class ComponentsRegistry {
    private val componentNameMap: MutableMap<String, Class<out Component>> = mutableMapOf()
    private val classToNameMap: MutableMap<Class<out Component>, String> = mutableMapOf()

    fun register(clazz: Class<out Component>) {
        componentNameMap.putIfAbsent(clazz.simpleName, clazz).let {
            if (it != null) throw ComponentCollisionException("Компонент ${clazz.simpleName} уже зарегистрирован в реестре!")
        }
        classToNameMap[clazz] = clazz.simpleName
    }

    fun getComponent(name: String): Class<out Component> {
        return componentNameMap[name] ?: throw ComponentNotFoundException("Компонент $name не найден в реестре")
    }

    fun getComponentName(component: Component): String {
        return classToNameMap[component::class.java]
            ?: throw ComponentNotFoundException("Компонент ${component::class.java.simpleName} не зарегистрирован")
    }

}