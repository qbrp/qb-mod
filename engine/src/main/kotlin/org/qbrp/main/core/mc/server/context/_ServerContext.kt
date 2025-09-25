package org.qbrp.main.core.mc.server.context

import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.qbrp.main.core.Core
import org.qbrp.main.core.CoreContext
import org.qbrp.main.core.events.EventContext

fun KoinComponent.withServerContext(post: context(ServerContext) () -> Unit) {
    with(get<ServerContext>()) { post() }
}

fun KoinComponent.withCoreContext(post: context(CoreContext) () -> Unit) {
    with(Core.context) { post() }
}

fun KoinComponent.coreContext(): CoreContext {
    return Core.context
}