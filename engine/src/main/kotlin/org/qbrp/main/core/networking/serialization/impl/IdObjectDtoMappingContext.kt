package org.qbrp.main.core.networking.serialization.impl

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.objects.StateObject

class IdObjectDtoMappingContext(override val componentManager: StateObject<out ComponentManager>) : ComponentDtoMappingContext<StateObject<out ComponentManager>> {}