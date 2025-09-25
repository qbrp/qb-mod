package org.qbrp.main.core.components.events

import org.qbrp.main.core.components.ComponentManager
import org.qbrp.main.core.components.objects.StateObject
import org.qbrp.main.core.dataformats.AggregateStorage

class EventSupportersStorage: AggregateStorage<StateObject<out ComponentManager>>()