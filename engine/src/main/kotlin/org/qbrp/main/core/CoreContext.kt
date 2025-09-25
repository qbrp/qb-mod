package org.qbrp.main.core

import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.events.EventContext
import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.server.context.ServerContext
import org.qbrp.main.core.notifications.NotificationContext

interface CoreContext: ServerContext, EventContext, MinecraftContext, ComponentRegistry, NotificationContext