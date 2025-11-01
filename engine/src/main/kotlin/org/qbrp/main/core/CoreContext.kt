package org.qbrp.main.core

import org.koin.core.component.KoinComponent
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.events.EventContext

interface CoreContext : EventContext, ComponentRegistry