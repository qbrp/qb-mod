package org.qbrp.main.core.mc.context

import org.koin.core.component.KoinComponent
import org.koin.core.component.get

fun <T> KoinComponent.withMinecraftContext(post: context(MinecraftContext) () -> T): T {
    with(get<MinecraftContext>()) {
        return post()
    }
}

suspend fun KoinComponent.suspendMinecraftContext(post: suspend context(MinecraftContext) () -> Unit) {
    with(get<MinecraftContext>()) { post() }
}