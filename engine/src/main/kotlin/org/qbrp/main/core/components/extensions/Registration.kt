package org.qbrp.main.core.components.extensions

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.main.core.components.ComponentRegistrar

fun KoinComponent.componentRegistrar(build: (ComponentRegistrar).() -> Unit) {
    get<ComponentRegistrar>().build()
}