package org.qbrp.client.render.ui.view

import org.qbrp.main.core.events.EventHandler
import kotlin.reflect.KClass

class ScreenEventHandler<C: ViewComponent, E, R>(
    private val screen: Screen,
    val instanceClass: KClass<C>,
    val handler: (C, E) -> R
) : EventHandler<E, List<R>> {

    override suspend fun invoke(p1: E): List<R> {
        return propagateEvent(screen, p1)
    }

    private fun propagateEvent(view: View, event: E): List<R> {
        return view.viewComponents
            .filter { it::class == instanceClass }
            .mapNotNull { handler(it as C, event) } + view.children.flatMap { propagateEvent(it, event) }
    }
}