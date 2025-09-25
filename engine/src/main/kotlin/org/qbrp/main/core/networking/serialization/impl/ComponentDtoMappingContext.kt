package org.qbrp.main.core.networking.serialization.impl

import org.qbrp.main.core.components.ComponentManager

interface ComponentDtoMappingContext<C: ComponentManager> {
    val componentManager: C
}