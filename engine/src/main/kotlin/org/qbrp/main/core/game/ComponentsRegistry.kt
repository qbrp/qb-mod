package org.qbrp.main.core.game

import kotlinx.serialization.Serializable
import org.qbrp.main.core.game.model.components.Component
import org.qbrp.main.core.game.model.components.exception.ComponentCollisionException
import org.qbrp.main.core.game.model.components.exception.ComponentNotFoundException
import org.qbrp.main.core.game.serialization.GameMapper
import kotlin.reflect.KClass
import kotlin.reflect.full.hasAnnotation

class ComponentsRegistry {
    private val componentNameMap: MutableMap<String, Class<out Component>> = mutableMapOf()
    private val classToNameMap: MutableMap<Class<out Component>, String> = mutableMapOf()

    fun register(clazz: Class<out Component>, registerSerializer: Boolean = true) {
        componentNameMap.putIfAbsent(clazz.simpleName, clazz).let {
            if (it != null) throw ComponentCollisionException("Компонент ${clazz.simpleName} уже зарегистрирован в реестре!")
        }
        classToNameMap[clazz] = clazz.simpleName
        if (registerSerializer) {
            if (clazz.kotlin.hasAnnotation<Serializable>())
            GameMapper.registerSerializer(clazz.kotlin)
        }
    }

    fun getComponentClass(name: String): KClass<out Component> {
        return componentNameMap[name]?.kotlin ?: throw ComponentNotFoundException("Компонент $name не найден в реестре")
    }

    fun getComponentName(component: Component): String {
        return classToNameMap[component::class.java]
            ?: throw ComponentNotFoundException("Компонент ${component::class.java.simpleName} не зарегистрирован")
    }
}