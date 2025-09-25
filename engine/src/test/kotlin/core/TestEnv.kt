package core

import core.server.ServerThreadMock
import io.mockk.mockk
import org.qbrp.main.core.components.ComponentRegistry
import org.qbrp.main.core.components.ComponentRegistryImpl
import org.qbrp.main.core.components.events.EventSupportersStorage
import org.qbrp.main.core.dataformats.StringId
import org.qbrp.main.core.events.DefaultEventContext
import org.qbrp.main.core.events.EventBus
import org.qbrp.main.core.events.EventContext
import org.qbrp.main.core.events.EventEnv
import org.qbrp.main.core.events.EventManager
import org.qbrp.main.core.events.component.ComponentEventContext
import org.qbrp.main.core.game.IDGenerator
import org.qbrp.main.core.mc.context.MinecraftContext
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.permission.PermissionService
import org.qbrp.main.core.mc.player.service.MinecraftPlayerManager
import org.qbrp.main.core.mc.player.service.PlayerModeration
import org.qbrp.main.core.mc.player.service.attribute.MinecraftPlayerAttributeService
import org.qbrp.main.core.mc.server.command.ServerCommandExecutor
import org.qbrp.main.core.mc.server.thread.ServerThread
import java.util.UUID

abstract class TestEnv: EventEnv {
    fun setupComponentsRegistry(registration: ComponentRegistry.() -> Unit): ComponentRegistry {
        return ComponentRegistryImpl(mockk(relaxed = true), mockk(relaxed = true)).apply { registration() }
    }

    fun newEventBus(): EventBus = EventManager()
    fun newEventSupportersStorage(): EventSupportersStorage = EventSupportersStorage()
    fun newEventContext(bus: EventBus = newEventBus(),
                        storage: EventSupportersStorage = newEventSupportersStorage()
    ): ComponentEventContext = DefaultEventContext(bus, storage)
    fun newMinecraftContext(): MinecraftContext {
        return MutableMinecraftContext()
    }
    fun newMinecraftAndEventContext(mc: MinecraftContext = newMinecraftContext(),
                                    ev: EventContext=  newEventContext()
    ): MinecraftAndEventContext {
        return MinecraftAndEventContext(mc, ev)
    }

    class MutableMinecraftContext(): MinecraftContext {
        override var playerModeration: PlayerModeration = mockk(relaxed = true)
        override var permissionService: PermissionService = mockk(relaxed = true)
        override var playerManager: MinecraftPlayerManager = mockk(relaxed = true)
        override var serverThread: ServerThread = ServerThreadMock()
        override var commandExecutor: ServerCommandExecutor = mockk(relaxed = true)
        override var attributeService: MinecraftPlayerAttributeService = mockk(relaxed = true)
    }

    class MinecraftAndEventContext(mc: MinecraftContext, ev: EventContext): MinecraftContext by mc, EventContext by ev
}

fun randomMinecraftPlayerId() = MinecraftPlayerId(UUID.randomUUID())
fun randomStringId() = StringId(IDGenerator.nextIdStr())