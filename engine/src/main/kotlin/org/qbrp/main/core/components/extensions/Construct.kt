package org.qbrp.main.core.components.extensions

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.construct.ComponentConstruct

fun ComponentManager.merge(list: List<ComponentConstruct<*>>) {
    list
        .map { it.construct() }
        .forEach {
            addComponentIfNotExist(it.first, { it.second })
        }
}