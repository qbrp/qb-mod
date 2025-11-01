package engine.players

import core.TableMock
import core.TestEnv
import core.server.ServerThreadMock
import io.kotest.matchers.shouldBe
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import net.minecraft.text.Text
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.qbrp.main.core.mc.player.dataformats.MinecraftPlayerId
import org.qbrp.main.core.mc.player.dataformats.Username
import org.qbrp.main.core.mc.player.model.PlayerSnapshot
import org.qbrp.main.core.mc.player.service.snapshot.MinecraftPlayerSnapshotService
import org.qbrp.main.engine.players.accounts.Account
import org.qbrp.main.network.player.ConnectionSession
import org.qbrp.main.engine.players.model.ServerPlayerObject
import org.qbrp.main.network.player.DisconnectReason
import org.qbrp.main.engine.players.model.service.PlayerFactory
import org.qbrp.main.engine.players.model.service.PlayerSaver
import org.qbrp.main.engine.players.model.service.ServerPlayerStorage
import org.qbrp.main.core.util.PlayerId
import java.util.UUID

class PlayerConnectionHandlerTest: TestEnv() {
    private lateinit var connectionHandler: PlayerConnectionHandler
    private lateinit var factory: PlayerFactory
    private lateinit var storage: ServerPlayerStorage
    private lateinit var saver: PlayerSaver
    private lateinit var networkHandler: ServerPlayerNetworkHandler
    private lateinit var snapshotService: MinecraftPlayerSnapshotService

    @BeforeEach
    fun setup() {
        factory = mockk(relaxed = true)
        storage = spyk(ServerPlayerStorage())
        saver = mockk(relaxed = true)
        networkHandler = mockk(relaxed = true)
        snapshotService = mockk(relaxed = true)

        connectionHandler = PlayerConnectionHandler(
            table = TableMock(),
            factory = factory,
            serializer = mockk(relaxed = true),
            storage = storage,
            networkHandler = networkHandler,
            saver = saver,
            serverThread = ServerThreadMock(),
        )
    }

    private val testPlayerId = MinecraftPlayerId(UUID.randomUUID())
    private val playerSnapshot = mockk<PlayerSnapshot> {
        every { uuid } returns testPlayerId
        every { username } returns Username("test_player")
    }
    private val account = Account("test", "player123")
    private val session = ConnectionSession(UUID.randomUUID(), PlayerId("player123"))
    private val serverPlayer = mockk<ServerPlayerObject>(relaxed = true) {
        every { id } returns this@PlayerConnectionHandlerTest.session.playerId.value
        every { playerId } returns this@PlayerConnectionHandlerTest.session.playerId
    }

    @Test
    fun cantHandleDisconnectNotExistingPlayer() = runTest {
        with(newEventContext()) {
            connectionHandler.handleDisconnect(playerSnapshot)
            verify(exactly = 0) { storage.remove(any()) }
            verify(exactly = 0) { saver.saveObject(any()) }
        }
    }

    @Test
    fun shouldCreatePlayerAndAddToStorage() = runTest {
        with(newMinecraftAndEventContext()) {
            every { factory.construct(any(), any(), any()) } returns serverPlayer
            connectionHandler.handleAuth(playerSnapshot, account, session)

            verify { factory.construct(playerSnapshot, account, session) }
            verify { storage.add(serverPlayer) }
            verify { saver.saveObject(serverPlayer) }
        }
        coVerify { networkHandler.sendInstantiatePacket(serverPlayer) }
    }

    @Test
    fun shouldHandleExceptionsDuringPlayerCreation() = runTest {
        val ctx = newMinecraftAndEventContext()
        with (ctx) {
            every { factory.construct(any(), any(), any()) } throws IllegalStateException("Broken")

            connectionHandler.handleAuth(playerSnapshot, account, session)
        }

        verify { ctx.playerModeration.disconnect(MinecraftPlayerId(any()), DisconnectReason(any<Text>())) }
        verify(exactly = 0) { storage.add(any()) }
        verify(exactly = 0) { saver.saveObject(any()) }
        coVerify(exactly = 0) { networkHandler.sendInstantiatePacket(any()) }
    }

    @Test
    fun playerShouldBeAccessibleFromStorageAfterAuth() = runTest {
        with(newMinecraftAndEventContext()) {
            every { factory.construct(any(), any(), any()) } returns serverPlayer
            connectionHandler.handleAuth(playerSnapshot, account, session)
        }

        val retrieved = storage.getById("player123")
        retrieved shouldBe serverPlayer
    }

    @Test
    fun playerCantRegisterMultipleTimes() = runTest {
        with(newMinecraftAndEventContext()) {
            every { factory.construct(any(), any(), any()) } returns serverPlayer

            connectionHandler.handleAuth(playerSnapshot, account, session)
            connectionHandler.handleAuth(playerSnapshot, account, session)

            verify(exactly = 1) { factory.construct(any(), any(), any()) }
        }
        verify(exactly = 1) { storage.add(serverPlayer) }
        storage.getAll().size shouldBe 1
    }
}