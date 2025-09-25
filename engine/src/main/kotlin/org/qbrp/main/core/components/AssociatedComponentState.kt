package org.qbrp.main.core.components

import org.qbrp.main.core.components.objects.ComponentObject

interface AssociatedComponentState<T: Any>: ComponentManager {
    var parent: T
}